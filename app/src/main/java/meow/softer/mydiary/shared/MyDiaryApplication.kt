package meow.softer.mydiary.shared

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.listener.RequestLoggingListener
import java.util.Locale

class MyDiaryApplication : Application() {
    var isHasPassword: Boolean = false
    override fun onCreate() {
        super.onCreate()
        //Use Fresco
        val listeners: MutableSet<RequestListener?> = HashSet<RequestListener?>()
        listeners.add(RequestLoggingListener())
        val config = ImagePipelineConfig.newBuilder(this)
            .setRequestListeners(listeners as Set<RequestListener>?)
            .setDownsampleEnabled(true)
            .build()
        Fresco.initialize(this, config)

        //To fix bug : spinner bg is dark when mode is night.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Check password
        if (SPFManager.getPassword(this) == "") {
            this.isHasPassword = false
        } else {
            this.isHasPassword = true
        }

        //init Theme & language
        initTheme()
        setLocaleLanguage()
    }

    private fun initTheme() {
        val themeManager = ThemeManager.instance!!
        themeManager.currentTheme = SPFManager.getTheme(this)
    }

    private fun setLocaleLanguage() {
        mLocale = when (SPFManager.getLocalLanguageCode(this)) {
            1 -> Locale.ENGLISH
            2 -> Locale.CHINESE
            3 -> Locale("bn", "")
            else -> Locale.getDefault()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context? {
        if (mLocale == null) {
            mLocale = Locale.getDefault()
        }
        Locale.setDefault(mLocale)
        val configuration = context.resources.configuration
        configuration.setLocale(mLocale)
        return context.createConfigurationContext(configuration)
    }


    companion object {
        /*
    * try to fix the locale
    * */
        @JvmField
        var mLocale: Locale? = null
    }
}
