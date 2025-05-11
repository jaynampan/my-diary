package meow.softer.mydiary.security

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import meow.softer.mydiary.MainActivity
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.Encryption
import meow.softer.mydiary.shared.MyDiaryApplication
import meow.softer.mydiary.shared.SPFManager
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper

class PasswordActivity : AppCompatActivity(), View.OnClickListener {
    private var currentMode = 0

    /**
     * Password keeper
     */
    private var passwordPointer = 0

    //the current password
    private var passwordStrBuilder: StringBuilder? = null

    //For verify password;
    private var createdPassword: String? = null

    //For start Main activity
    private var showReleaseNote = false

    /**
     * UI
     */
    private var IV_password_number_1: ImageView? = null
    private var IV_password_number_2: ImageView? = null
    private var IV_password_number_3: ImageView? = null
    private var IV_password_number_4: ImageView? = null

    private var But_password_key_1: Button? = null
    private var But_password_key_2: Button? = null
    private var But_password_key_3: Button? = null
    private var But_password_key_4: Button? = null
    private var But_password_key_5: Button? = null
    private var But_password_key_6: Button? = null
    private var But_password_key_7: Button? = null
    private var But_password_key_8: Button? = null
    private var But_password_key_9: Button? = null
    private var But_password_key_cancel: Button? = null
    private var But_password_key_0: Button? = null
    private var But_password_key_backspace: ImageButton? = null


    private var TV_password_message: TextView? = null
    private var TV_password_sub_message: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
         
        

        //Get this page mode
        currentMode = intent.getIntExtra("password_mode", FAIL)
        showReleaseNote = intent.getBooleanExtra("showReleaseNote", false)
        if (currentMode == FAIL) {
            finish()
        }
        IV_password_number_1 = findViewById<ImageView>(R.id.IV_password_number_1)
        IV_password_number_2 = findViewById<ImageView>(R.id.IV_password_number_2)
        IV_password_number_3 = findViewById<ImageView>(R.id.IV_password_number_3)
        IV_password_number_4 = findViewById<ImageView>(R.id.IV_password_number_4)


        TV_password_message = findViewById<TextView>(R.id.TV_password_message)
        TV_password_sub_message = findViewById<TextView>(R.id.TV_password_sub_message)

        But_password_key_1 = findViewById<Button>(R.id.But_password_key_1)
        But_password_key_2 = findViewById<Button>(R.id.But_password_key_2)
        But_password_key_3 = findViewById<Button>(R.id.But_password_key_3)
        But_password_key_4 = findViewById<Button>(R.id.But_password_key_4)
        But_password_key_5 = findViewById<Button>(R.id.But_password_key_5)
        But_password_key_6 = findViewById<Button>(R.id.But_password_key_6)
        But_password_key_7 = findViewById<Button>(R.id.But_password_key_7)
        But_password_key_8 = findViewById<Button>(R.id.But_password_key_8)
        But_password_key_9 = findViewById<Button>(R.id.But_password_key_9)
        But_password_key_cancel = findViewById<Button>(R.id.But_password_key_cancel)
        But_password_key_0 = findViewById<Button>(R.id.But_password_key_0)
        But_password_key_backspace = findViewById<ImageButton>(R.id.But_password_key_backspace)

        But_password_key_1!!.setOnClickListener(this)
        But_password_key_2!!.setOnClickListener(this)
        But_password_key_3!!.setOnClickListener(this)
        But_password_key_4!!.setOnClickListener(this)
        But_password_key_5!!.setOnClickListener(this)
        But_password_key_6!!.setOnClickListener(this)
        But_password_key_7!!.setOnClickListener(this)
        But_password_key_8!!.setOnClickListener(this)
        But_password_key_9!!.setOnClickListener(this)
        But_password_key_0!!.setOnClickListener(this)
        But_password_key_backspace!!.setOnClickListener(this)

        But_password_key_cancel!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })

        clearUiPassword()
        initUI()
    }

    private fun initUI() {
        IV_password_number_1!!.requestFocus()
        when (currentMode) {
            CREATE_PASSWORD -> TV_password_message!!.text = getString(R.string.password_create_pwd)
            CREATE_PASSWORD_WITH_VERIFY -> TV_password_message!!.text = getString(R.string.password_create_pwd_with_verify)
            VERIFY_PASSWORD -> {
                TV_password_message!!.text = getString(R.string.password_verify_pwd)
                But_password_key_cancel!!.visibility = View.INVISIBLE
                But_password_key_cancel!!.setOnClickListener(null)
            }

            REMOVE_PASSWORD -> TV_password_message!!.text = getString(R.string.password_remove_pwd)
        }
    }

    private fun setSubMessage() {
        when (currentMode) {
            CREATE_PASSWORD_WITH_VERIFY -> TV_password_sub_message!!.text = getString(R.string.password_create_pwd_with_verify_msg)
            VERIFY_PASSWORD -> TV_password_sub_message!!.text = getString(R.string.password_verify_pwd_msg)
            REMOVE_PASSWORD -> TV_password_sub_message!!.text = getString(R.string.password_remove_pwd_msg)
        }
    }

    private fun clearUiPassword() {
        passwordPointer = 0
        passwordStrBuilder = StringBuilder()
        IV_password_number_1!!.setImageResource(R.drawable.ic_password_no_text_48dp)
        IV_password_number_2!!.setImageResource(R.drawable.ic_password_no_text_48dp)
        IV_password_number_3!!.setImageResource(R.drawable.ic_password_no_text_48dp)
        IV_password_number_4!!.setImageResource(R.drawable.ic_password_no_text_48dp)

        IV_password_number_1!!.clearColorFilter()
        IV_password_number_2!!.clearColorFilter()
        IV_password_number_3!!.clearColorFilter()
        IV_password_number_4!!.clearColorFilter()
    }

    private fun afterPasswordChanged() {
        when (currentMode) {
            CREATE_PASSWORD -> {
                createdPassword = passwordStrBuilder.toString()
                clearUiPassword()
                currentMode = CREATE_PASSWORD_WITH_VERIFY
                initUI()
            }

            CREATE_PASSWORD_WITH_VERIFY -> if (createdPassword == passwordStrBuilder.toString()) {
                savePassword(Encryption.SHA256(passwordStrBuilder.toString()))
                (application as MyDiaryApplication).isHasPassword = true
                finish()
            } else {
                clearUiPassword()
                setSubMessage()
            }

            VERIFY_PASSWORD -> if (isPasswordCorrect(passwordStrBuilder.toString())) {
                val goMainPageIntent = Intent(this, MainActivity::class.java)
                goMainPageIntent.putExtra("showReleaseNote", showReleaseNote)
                finish()
                this.startActivity(goMainPageIntent)
            } else {
                clearUiPassword()
                setSubMessage()
            }

            REMOVE_PASSWORD -> if (isPasswordCorrect(passwordStrBuilder.toString())) {
                savePassword("")
                (application as MyDiaryApplication).isHasPassword = false
                finish()
            } else {
                clearUiPassword()
                setSubMessage()
            }
        }
    }

    private fun isPasswordCorrect(password: String?): Boolean {
        return Encryption.SHA256(password.toString()) == SPFManager.getPassword(this)
    }

    private fun savePassword(password: String?) {
        SPFManager.setAndEncryptPassword(this, password)
    }

    private fun addPasswordStr(passwordStr: String?) {
        passwordStrBuilder!!.append(passwordStr)
        when (passwordPointer) {
            0 -> {
                IV_password_number_1!!.setImageResource(R.drawable.ic_password_dot_48dp)
                IV_password_number_1!!.setColorFilter(
                    ThemeManager.instance!!.getThemeMainColor(this),
                    PorterDuff.Mode.SRC_ATOP
                )
            }

            1 -> {
                IV_password_number_2!!.setImageResource(R.drawable.ic_password_dot_48dp)
                IV_password_number_2!!.setColorFilter(
                    ThemeManager.instance!!.getThemeMainColor(this),
                    PorterDuff.Mode.SRC_ATOP
                )
            }

            2 -> {
                IV_password_number_3!!.setImageResource(R.drawable.ic_password_dot_48dp)
                IV_password_number_3!!.setColorFilter(
                    ThemeManager.instance!!.getThemeMainColor(this),
                    PorterDuff.Mode.SRC_ATOP
                )
            }

            3 -> {
                IV_password_number_4!!.setImageResource(R.drawable.ic_password_dot_48dp)
                IV_password_number_4!!.setColorFilter(
                    ThemeManager.instance!!.getThemeMainColor(this),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }
        passwordPointer++
    }

    private fun removePasswordStr() {
        if (passwordPointer > 0) {
            passwordStrBuilder!!.setLength(passwordStrBuilder!!.length - 1)
            when (passwordPointer) {
                1 -> {
                    IV_password_number_1!!.setImageResource(R.drawable.ic_password_no_text_48dp)
                    IV_password_number_1!!.clearColorFilter()
                }

                2 -> {
                    IV_password_number_2!!.setImageResource(R.drawable.ic_password_no_text_48dp)
                    IV_password_number_2!!.clearColorFilter()
                }

                3 -> {
                    IV_password_number_3!!.setImageResource(R.drawable.ic_password_no_text_48dp)
                    IV_password_number_3!!.clearColorFilter()
                }
            }
            passwordPointer--
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.But_password_key_1 -> addPasswordStr("1")
            R.id.But_password_key_2 -> addPasswordStr("2")
            R.id.But_password_key_3 -> addPasswordStr("3")
            R.id.But_password_key_4 -> addPasswordStr("4")
            R.id.But_password_key_5 -> addPasswordStr("5")
            R.id.But_password_key_6 -> addPasswordStr("6")
            R.id.But_password_key_7 -> addPasswordStr("7")
            R.id.But_password_key_8 -> addPasswordStr("8")
            R.id.But_password_key_9 -> addPasswordStr("9")
            R.id.But_password_key_0 -> addPasswordStr("0")
            R.id.But_password_key_backspace -> removePasswordStr()
        }
        if (passwordPointer > 3) {
            afterPasswordChanged()
        }
    }

    companion object {
        /**
         * Mode
         */
        val FAIL: Int = -1
        const val CREATE_PASSWORD: Int = 0
        const val CREATE_PASSWORD_WITH_VERIFY: Int = 1
        const val VERIFY_PASSWORD: Int = 2
        const val REMOVE_PASSWORD: Int = 3
    }
}
