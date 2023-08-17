package abdulrahman.core.data.remote

import abdulrahman.core.util.Constant
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.accept
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object ServiceProvider {
    @OptIn(ExperimentalSerializationApi::class)
    fun getKoinApi(): HttpClient = HttpClient(Android) {
        val ktorClientTag = "KoinHttpClient"
        engine {
            connectTimeout = 30_000
            socketTimeout = 30_000
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = true
                encodeDefaults = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.i(ktorClientTag, "log: $message")
                }
            }
            level = LogLevel.ALL
        }
        install(ResponseObserver) {
            onResponse { response: HttpResponse ->
                Log.i(ktorClientTag, "getKoinApi: $response")
            }
        }
        install(DefaultRequest) {
            url(Constant.BASE_URL)
            contentType(Json)
            accept(Json)
        }
        expectSuccess = true
    }
}