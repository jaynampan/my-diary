package meow.softer.mydiary.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yalantis.ucrop.UCrop
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ColorTools
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.PermissionHelper
import meow.softer.mydiary.shared.SPFManager
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess
import androidx.core.graphics.drawable.toDrawable

class SettingActivity : AppCompatActivity(), View.OnClickListener,
    SettingColorPickerFragment.colorPickerCallback, AdapterView.OnItemSelectedListener {
    /**
     * Theme
     */
    private var themeManager: ThemeManager? = null

    //For spinner first run
    private var isThemeFirstRun = true
    private var isLanguageFirstRun = true

    //Because the default profile bg is color ,
    //so we should keep main color for replace when main color was changed.
    private var tempMainColorCode = 0

    /**
     * Profile
     */
    private var profileBgFileName: String? = ""
    private var isAddNewProfileBg = false

    /**
     * File
     */
    private var tempFileManager: FileManager? = null

    /**
     * UI
     */
    private var SP_setting_theme: Spinner? = null
    private var SP_setting_language: Spinner? = null
    private var IV_setting_profile_bg: ImageView? = null
    private var IV_setting_theme_main_color: ImageView? = null
    private var IV_setting_theme_dark_color: ImageView? = null
    private var But_setting_theme_default_bg: Button? = null
    private var But_setting_theme_default: Button? = null
    private var But_setting_theme_apply: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
         
        


        themeManager = ThemeManager.instance!!
        //Create fileManager for get temp folder
        tempFileManager = FileManager(this, FileManager.TEMP_DIR)
        tempFileManager!!.clearDir()

        SP_setting_theme = findViewById<View?>(R.id.SP_setting_theme) as Spinner
        IV_setting_profile_bg = findViewById<View?>(R.id.IV_setting_profile_bg) as ImageView
        IV_setting_theme_main_color =
            findViewById<View?>(R.id.IV_setting_theme_main_color) as ImageView
        IV_setting_theme_dark_color =
            findViewById<View?>(R.id.IV_setting_theme_dark_color) as ImageView
        But_setting_theme_default_bg =
            findViewById<View?>(R.id.But_setting_theme_default_bg) as Button
        But_setting_theme_default = findViewById<View?>(R.id.But_setting_theme_default) as Button
        But_setting_theme_apply = findViewById<View?>(R.id.But_setting_theme_apply) as Button
        But_setting_theme_apply!!.setOnClickListener(this)

        SP_setting_language = findViewById<View?>(R.id.SP_setting_language) as Spinner


        initSpinner()
        initTheme(themeManager!!.currentTheme)
        initLanguage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PROFILE_BG) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.data != null) {
                    //Compute the bg size
                    val bgWidth = ScreenHelper.getScreenWidth(this)
                    val bgHeight = getResources().getDimensionPixelOffset(R.dimen.top_bar_height)
                    val options = UCrop.Options()
                    options.setToolbarColor(ThemeManager.instance!!.getThemeMainColor(this))
                    options.setStatusBarColor(ThemeManager.instance!!.getThemeDarkColor(this))
                    UCrop.of(
                        data.data!!,
                        Uri.fromFile(
                            File(
                                tempFileManager!!.dir
                                    .toString() + "/" + FileManager.createRandomFileName()
                            )
                        )
                    )
                        .withMaxResultSize(bgWidth, bgHeight)
                        .withOptions(options)
                        .withAspectRatio(bgWidth.toFloat(), bgHeight.toFloat())
                        .start(this)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_photo_intent_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    val resultUri = UCrop.getOutput(data)
                    IV_setting_profile_bg!!.setImageBitmap(BitmapFactory.decodeFile(resultUri!!.path))
                    profileBgFileName = FileManager.getFileNameByUri(this, resultUri)
                    isAddNewProfileBg = true
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.toast_crop_profile_banner_fail),
                        Toast.LENGTH_LONG
                    ).show()
                    //sample error
                    // final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == PermissionHelper.REQUEST_WRITE_ES_PERMISSION) {
            if (grantResults.isNotEmpty()
                && PermissionHelper.checkAllPermissionResult(grantResults)
            ) {
                FileManager.startBrowseImageFile(this, SELECT_PROFILE_BG)
            } else {
                PermissionHelper.showAddPhotoDialog(this)
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        //Revert current theme
        themeManager!!.currentTheme = SPFManager.getTheme(this)
    }

    private fun initLanguage() {
        if (SPFManager.getLocalLanguageCode(this) != -1) {
            SP_setting_language!!.setSelection(SPFManager.getLocalLanguageCode(this))
        }
    }

    private fun initTheme(themeId: Int) {
        if (themeId == ThemeManager.CUSTOM) {
            IV_setting_profile_bg!!.setOnClickListener(this)
            IV_setting_theme_main_color!!.setOnClickListener(this)
            IV_setting_theme_dark_color!!.setOnClickListener(this)

            But_setting_theme_default_bg!!.setOnClickListener(this)
            But_setting_theme_default_bg!!.setEnabled(true)
            But_setting_theme_default!!.setOnClickListener(this)
            But_setting_theme_default!!.setEnabled(true)

            IV_setting_profile_bg!!.setImageBitmap(null)
        } else {
            IV_setting_profile_bg!!.setOnClickListener(null)
            IV_setting_theme_main_color!!.setOnClickListener(null)
            IV_setting_theme_dark_color!!.setOnClickListener(null)

            But_setting_theme_default_bg!!.setOnClickListener(null)
            But_setting_theme_default_bg!!.setEnabled(false)
            But_setting_theme_default!!.setOnClickListener(null)
            But_setting_theme_default!!.setEnabled(false)
        }
        //Save the temp Main Color Code
        tempMainColorCode = themeManager!!.getThemeMainColor(this)
        IV_setting_profile_bg!!.setImageDrawable(themeManager!!.getProfileBgDrawable(this))
        setThemeColor()
    }

    private fun setThemeColor() {
        IV_setting_theme_main_color!!.setImageDrawable(
            themeManager!!.getThemeMainColor(this).toDrawable()
        )
        IV_setting_theme_dark_color!!.setImageDrawable(
            themeManager!!.getThemeDarkColor(this).toDrawable()
        )
    }

    private fun initSpinner() {
        //Theme Spinner
        val themeAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this, R.layout.spinner_simple_text,
            getResources().getStringArray(R.array.theme_list)
        )
        SP_setting_theme!!.setAdapter(themeAdapter)
        SP_setting_theme!!.setSelection(themeManager!!.currentTheme)
        SP_setting_theme!!.onItemSelectedListener = this

        //Language spinner
        val languageAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this, R.layout.spinner_simple_text,
            getResources().getStringArray(R.array.language_list)
        )
        SP_setting_language!!.setAdapter(languageAdapter)
        SP_setting_language!!.setSelection(SPFManager.getLocalLanguageCode(this))
        SP_setting_language!!.onItemSelectedListener = this
    }

    private fun applySetting(killProcess: Boolean) {
        //Restart App
        val i = this.baseContext.packageManager
            .getLaunchIntentForPackage(this.baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)

        if (killProcess) {
            Process.killProcess(Process.myPid())
            exitProcess(0)
        } else {
            this.finish()
        }
    }

    @SuppressLint("UseKtx")
    override fun onColorChange(colorCode: Int, viewId: Int) {
        when (viewId) {
            R.id.IV_setting_theme_main_color -> {
                tempMainColorCode = colorCode
                IV_setting_theme_main_color!!.setImageDrawable(tempMainColorCode.toDrawable())
                if (IV_setting_profile_bg!!.getDrawable() is ColorDrawable) {
                    IV_setting_profile_bg!!.setImageDrawable(
                        tempMainColorCode.toDrawable()
                    )
                }
            }

            R.id.IV_setting_theme_dark_color -> IV_setting_theme_dark_color!!.setImageDrawable(
                ColorDrawable(colorCode)
            )
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.But_setting_theme_default_bg -> {
                IV_setting_profile_bg!!.setImageDrawable(tempMainColorCode.toDrawable())
                profileBgFileName = ""
                isAddNewProfileBg = true
            }

            R.id.IV_setting_profile_bg -> if (PermissionHelper.checkPermission(
                    this,
                    PermissionHelper.REQUEST_WRITE_ES_PERMISSION
                )
            ) {
                FileManager.startBrowseImageFile(this, SELECT_PROFILE_BG)
            }

            R.id.But_setting_theme_default -> {
                IV_setting_theme_main_color!!.setImageDrawable(
                    ColorTools.getColor(
                        this,
                        R.color.themeColor_custom_default
                    ).toDrawable()
                )
                IV_setting_theme_dark_color!!.setImageDrawable(
                    ColorTools.getColor(
                        this,
                        R.color.theme_dark_color_custom_default
                    ).toDrawable()
                )
                //Also revert the tempMainColor & profile bg
                tempMainColorCode = ColorTools.getColor(this, R.color.themeColor_custom_default)
                if (IV_setting_profile_bg!!.getDrawable() is ColorDrawable) {
                    IV_setting_profile_bg!!.setImageDrawable(tempMainColorCode.toDrawable())
                }
            }

            R.id.But_setting_theme_apply -> {
                //Save custom theme value
                if (themeManager!!.currentTheme == ThemeManager.CUSTOM) {
                    //Check is add new profile
                    if (isAddNewProfileBg) {
                        //For checking new profile bg is image or color.
                        var hasCustomProfileBannerBg = false
                        val settingFM = FileManager(this, FileManager.SETTING_DIR)
                        if ("" != profileBgFileName) {
                            try {
                                //Copy the profile into setting dir
                                FileManager.copy(
                                    File(tempFileManager!!.dirAbsolutePath + "/" + profileBgFileName),
                                    File(settingFM.dirAbsolutePath + "/" + ThemeManager.CUSTOM_PROFILE_BANNER_BG_FILENAME)
                                )
                                hasCustomProfileBannerBg = true
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(
                                    this,
                                    getString(R.string.toast_save_profile_banner_fail),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            File(settingFM.dirAbsolutePath + "/" + ThemeManager.CUSTOM_PROFILE_BANNER_BG_FILENAME).delete()
                        }
                        SPFManager.setCustomProfileBannerBg(this, hasCustomProfileBannerBg)
                    }
                    //Save new color
                    SPFManager.setMainColor(
                        this,
                        (IV_setting_theme_main_color!!.getDrawable() as ColorDrawable).color
                    )
                    SPFManager.setSecondaryColor(
                        this,
                        (IV_setting_theme_dark_color!!.getDrawable() as ColorDrawable).color
                    )
                }
                //Save new theme style
                themeManager!!.saveTheme(
                    this@SettingActivity,
                    SP_setting_theme!!.selectedItemPosition
                )
                //Send Toast
                Toast.makeText(this, getString(R.string.toast_change_theme), Toast.LENGTH_SHORT)
                    .show()
                applySetting(false)
            }

            R.id.IV_setting_theme_main_color -> {
                val mainColorPickerFragment = SettingColorPickerFragment.newInstance(
                    themeManager!!.getThemeMainColor(this),
                    R.id.IV_setting_theme_main_color
                )
                mainColorPickerFragment.show(supportFragmentManager, "mainColorPickerFragment")
            }

            R.id.IV_setting_theme_dark_color -> {
                val secColorPickerFragment =
                    SettingColorPickerFragment.newInstance(
                        themeManager!!.getThemeDarkColor(this),
                        R.id.IV_setting_theme_dark_color
                    )
                secColorPickerFragment.show(supportFragmentManager, "secColorPickerFragment")
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        when (parent.id) {
            R.id.SP_setting_theme -> if (!isThemeFirstRun) {
                //Temp set currentTheme .
                //If it doesn't apply , revert it on onDestroy .
                themeManager!!.currentTheme = position
                initTheme(position)
            } else {
                //First time do nothing
                isThemeFirstRun = false
            }

            R.id.SP_setting_language -> if (!isLanguageFirstRun) {
                SPFManager.setLocalLanguageCode(this, position)
                applySetting(true)
            } else {
                //First time do nothing
                isLanguageFirstRun = false
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Do nothing
    }

    companion object {
        private const val SELECT_PROFILE_BG = 0
    }
}
