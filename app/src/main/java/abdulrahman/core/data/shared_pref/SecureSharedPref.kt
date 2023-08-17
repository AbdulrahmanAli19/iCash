package abdulrahman.core.data.shared_pref

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


class SecureSharedPref(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val cipher: Cipher
    private val json: Json

    init {
        sharedPreferences = createEncryptedSharedPreferences(context)
        cipher = createCipher()
        json = Json { ignoreUnknownKeys = true }
    }

    private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "secure_shared_pref",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun createCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_GCM + "/" +
                    KeyProperties.ENCRYPTION_PADDING_NONE
        )
    }

    private fun encrypt(data: ByteArray, secretKey: SecretKey): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(data)
    }

    private fun decrypt(data: ByteArray, secretKey: SecretKey, iv: ByteArray): ByteArray {
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(data)
    }

    fun <T : Any> putObject(key: String, obj: T, serializer: KSerializer<T>) {
        val jsonString = json.encodeToString(serializer, obj)
        val encryptedData = encrypt(jsonString.toByteArray(), getSecretKey())
        val encodedValue = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        sharedPreferences.edit().putString(key, encodedValue).apply()
    }

    fun <T : Any> getObject(key: String, serializer: KSerializer<T>): T? {
        val encodedValue = sharedPreferences.getString(key, null) ?: return null
        val encryptedData = Base64.decode(encodedValue, Base64.DEFAULT)
        val decryptedData = decrypt(encryptedData, getSecretKey(), cipher.iv)
        val jsonString = decryptedData.toString(Charset.defaultCharset())
        return json.decodeFromString(serializer, jsonString)
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val secretKeyEntry =
            keyStore.getEntry("secure_shared_pref_key", null) as? KeyStore.SecretKeyEntry

        return if (secretKeyEntry != null) {
            secretKeyEntry.secretKey
        } else {
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                "secure_shared_pref_key",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()

            (keyStore.getEntry("secure_shared_pref_key", null) as KeyStore.SecretKeyEntry).secretKey
        }
    }

    // Methods for storing and retrieving primitive data types
    // (similar to the previous implementation)

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}