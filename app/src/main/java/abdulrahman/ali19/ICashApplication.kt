package abdulrahman.ali19

import abdulrahman.ali19.di.appModule
import abdulrahman.core.util.Constants
import android.app.Application
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction
import com.paypal.pyplcheckout.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class ICashApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ICashApplication)
            modules(appModule)
        }
        val config = CheckoutConfig(
            application = this,
            clientId = Constants.PAYPAL_CLIENT_ID,
            environment = Environment.SANDBOX,
            returnUrl = "${BuildConfig.APP_NAME}://paypalpay",
            currencyCode = CurrencyCode.USD,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )
        PayPalCheckout.setConfig(config)
    }
}