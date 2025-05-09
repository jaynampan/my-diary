package meow.softer.mydiary.init

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import meow.softer.mydiary.MainActivity
import meow.softer.mydiary.R
import meow.softer.mydiary.init.InitTask.InitCallBack
import meow.softer.mydiary.security.PasswordActivity
import meow.softer.mydiary.shared.MyDiaryApplication
import java.util.Locale

class InitActivity : AppCompatActivity(), InitCallBack {
    private val initTime = 2200 // 2.5S
    private var initHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        initHandler = Handler()
    }

    override fun onResume() {
        super.onResume()
        initHandler!!.postDelayed(Runnable {
            InitTask(
                this@InitActivity,
                this@InitActivity
            ).execute()
        }, initTime.toLong())
    }

    override fun onPause() {
        super.onPause()
        initHandler!!.removeCallbacksAndMessages(null)
    }


    override fun onInitCompiled(showReleaseNote: Boolean) {
        if ((application as MyDiaryApplication).isHasPassword) {
            val goSecurityPageIntent = Intent(this, PasswordActivity::class.java)
            goSecurityPageIntent.putExtra("password_mode", PasswordActivity.VERIFY_PASSWORD)
            goSecurityPageIntent.putExtra("showReleaseNote", showReleaseNote)
            finish()
            this@InitActivity.startActivity(goSecurityPageIntent)
        } else {
            val goMainPageIntent = Intent(this@InitActivity, MainActivity::class.java)
            goMainPageIntent.putExtra("showReleaseNote", showReleaseNote)
            finish()
            this@InitActivity.startActivity(goMainPageIntent)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context? {
        val locale = MyDiaryApplication.mLocale
        Log.e("Mytest", "init mLocale:$locale")
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}
