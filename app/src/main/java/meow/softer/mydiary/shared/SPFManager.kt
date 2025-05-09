package meow.softer.mydiary.shared

import android.content.Context
import android.os.Build
import android.util.Log
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ColorTools.getColor
import androidx.core.content.edit

object SPFManager {
    /**
     * config
     */
    private const val SPF_CONFIG = "CONFIG"

    //Local language
    private const val CONFIG_LOCAL_LANGUAGE = "CONFIG_LOCAL_LANGUAGE"

    /**
     * profile
     */
    private const val SPF_PROFILE = "PROFILE"
    private const val PROFILE_YOUR_NAME_IS = "YOUR_NAME_IS"
    private const val PROFILE_MAIN_PAGE_BANNER_BG = "PROFILE_MAIN_PAGE_BANNER_BG"

    /**
     * Theme
     */
    //Support old version: CONFIG - CONFIG_THEME
    private const val CONFIG_THEME = "CONFIG_THEME"

    //Theme SFP setting
    private const val SPF_THEME = "THEME"
    private const val THEME_MAIN_COLOR = "THEME_MAIN_COLOR"
    private const val THEME_SEC_COLOR = "THEME_SEC_COLOR"

    /**
     * System
     */
    private const val SPF_SYSTEM = "SYSTEM"

    //@deprecated
    private const val FIRST_RUN = "FIRST_RUN"
    private const val SYSTEM_VERSIONCODE = "VERSIONCODE"
    val DEFAULT_VERSIONCODE: Int = -1
    private const val DESCRIPTION_CLOSE = "DESCRIPTION_CLOSE"
    private const val ENCRYPTED_PASSWORD = "ENCRYPTED_PASSWORD"

    /**
     * OOBE:
     * Add in  Version 33 , Not use now.
     */
    private const val SPF_OOBE = "OOBE"

    /**
     * Diary auto save
     */
    private const val SPF_DIARY = "DIARY"

    //The json file like the backup file
    private const val DIARY_AUTO_SAVE = "DIARY_AUTO_SAVE_"

    /**
     * Config method
     */
    @JvmStatic
    fun getLocalLanguageCode(context: Context): Int {
        val settings = context.getSharedPreferences(SPF_CONFIG, 0)
        //default is 0 , follow the system
        return settings.getInt(CONFIG_LOCAL_LANGUAGE, 0)
    }

    fun setLocalLanguageCode(context: Context, languageCode: Int) {
        val settings = context.getSharedPreferences(SPF_CONFIG, 0)
        val PE = settings.edit()
        PE.putInt(CONFIG_LOCAL_LANGUAGE, languageCode)
        PE.commit()
        // note here it must use commit for immediate writing,because the thread will
        // be destroyed soon but apply is async,will do its job when the system is free
        Log.e("Mytest", "language code was set to" + languageCode)
    }

    /**
     * Profile method
     */
    fun getYourName(context: Context): String {
        val settings = context.getSharedPreferences(SPF_PROFILE, 0)
        //default is space
        return settings.getString(PROFILE_YOUR_NAME_IS, "")!!
    }

    fun setYourName(context: Context, yourNameIs: String?) {
        val settings = context.getSharedPreferences(SPF_PROFILE, 0)
        val PE = settings.edit()
        PE.putString(PROFILE_YOUR_NAME_IS, yourNameIs)
        PE.apply()
    }

    fun hasCustomProfileBannerBg(context: Context): Boolean {
        val settings = context.getSharedPreferences(SPF_PROFILE, 0)
        //default is space
        return settings.getBoolean(PROFILE_MAIN_PAGE_BANNER_BG, false)
    }

    fun setCustomProfileBannerBg(context: Context, customProfileBg: Boolean) {
        val settings = context.getSharedPreferences(SPF_PROFILE, 0)
        val PE = settings.edit()
        PE.putBoolean(PROFILE_MAIN_PAGE_BANNER_BG, customProfileBg)
        PE.apply()
    }

    /**
     * Theme method
     */
    fun getTheme(context: Context): Int {
        val settings = context.getSharedPreferences(SPF_CONFIG, 0)
        //default is close
        return settings.getInt(CONFIG_THEME, ThemeManager.instance!!.currentTheme)
    }

    @JvmStatic
    fun setTheme(context: Context, theme: Int) {
        val settings = context.getSharedPreferences(SPF_CONFIG, 0)
        val PE = settings.edit()
        PE.putInt(CONFIG_THEME, theme)
        PE.apply()
    }

    @JvmStatic
    fun getMainColor(context: Context): Int {
        val settings = context.getSharedPreferences(SPF_THEME, 0)
        //default is space
        return settings.getInt(
            THEME_MAIN_COLOR,
            getColor(context, R.color.themeColor_custom_default)
        )
    }

    fun setMainColor(context: Context, colorCode: Int) {
        val settings = context.getSharedPreferences(SPF_THEME, 0)
        val PE = settings.edit()
        PE.putInt(THEME_MAIN_COLOR, colorCode)
        PE.apply()
    }

    @JvmStatic
    fun getSecondaryColor(context: Context): Int {
        val settings = context.getSharedPreferences(SPF_THEME, 0)
        //default is space
        return settings.getInt(
            THEME_SEC_COLOR,
            getColor(context, R.color.theme_dark_color_custom_default)
        )
    }

    fun setSecondaryColor(context: Context, colorCode: Int) {
        val settings = context.getSharedPreferences(SPF_THEME, 0)
        val PE = settings.edit()
        PE.putInt(THEME_SEC_COLOR, colorCode)
        PE.apply()
    }

    @Deprecated(
        """it after version 33
      now use ShowcaseView - singleShot to run OOBE onve."""
    )
    fun setFirstRun(context: Context, firstRun: Boolean) {
        val settings = context.getSharedPreferences(SPF_SYSTEM, 0)
        val PE = settings.edit()
        PE.putBoolean(FIRST_RUN, firstRun)
        PE.apply()
    }

    /**
     * @param context
     * @return
     */
    @Deprecated(
        """it after version 33
      now use ShowcaseView - singleShot to run OOBE once."""
    )
    fun getFirstRun(context: Context): Boolean {
        val settings = context.getSharedPreferences(SPF_SYSTEM, 0)
        return settings.getBoolean(FIRST_RUN, true)
    }

    fun setVersionCode(context: Context) {
        val settings = context.getSharedPreferences(SPF_SYSTEM, 0)
        val PE = settings.edit()
        PE.putInt(SYSTEM_VERSIONCODE, Build.VERSION.SDK_INT)
        PE.apply()
    }


    fun getVersionCode(context: Context): Int {
        val settings = context.getSharedPreferences(SPF_SYSTEM, 0)
        return settings.getInt(SYSTEM_VERSIONCODE, DEFAULT_VERSIONCODE)
    }

    fun getReleaseNoteClose(context: Context): Boolean {
        val settings = context.getSharedPreferences(SPF_SYSTEM, 0)
        return settings.getBoolean(DESCRIPTION_CLOSE, false)
    }

    fun setReleaseNoteClose(context: Context, close: Boolean) {
        val settings = context.getSharedPreferences(SPF_SYSTEM, 0)
        settings.edit {
            putBoolean(DESCRIPTION_CLOSE, close)
        }
    }

    fun getPassword(context: Context): String {
        val settings = context.getSharedPreferences(SPF_SYSTEM, 0)
        /*
         * if string is "" , it is mean no password now.
         */
        return settings.getString(ENCRYPTED_PASSWORD, "")!!
    }

    fun setAndEncryptPassword(context: Context, password: String?) {
        val settings = context.getSharedPreferences(SPF_SYSTEM, 0)
        settings.edit {
            putString(ENCRYPTED_PASSWORD, password)
        }
    }

    /**
     * Diary
     */
    /**
     * Set  the  auto saved diary
     * The key is DIARY_AUTO_SAVE_TOPICID
     *
     *
     * set String null to clear it
     *
     * @param context
     * @param topicId
     * @param diaryJson
     */
    fun setDiaryAutoSave(context: Context, topicId: Long, diaryJson: String?) {
        val settings = context.getSharedPreferences(SPF_DIARY, 0)
        val PE = settings.edit()
        PE.putString(DIARY_AUTO_SAVE + topicId, diaryJson)
        PE.apply()
    }

    /**
     * set the null value to clear auto save content
     *
     * @param context
     * @param topicId
     */
    fun clearDiaryAutoSave(context: Context, topicId: Long) {
        val settings = context.getSharedPreferences(SPF_DIARY, 0)
        val PE = settings.edit()
        PE.putString(DIARY_AUTO_SAVE + topicId, null)
        PE.apply()
    }


    /**
     * Get auto saved diary
     * The key is DIARY_AUTO_SAVE_TOPICID
     * if  no any file in it , it will return null.
     *
     * @param context
     * @param topicId
     * @return the auto saved diary json.
     */
    fun getDiaryAutoSave(context: Context, topicId: Long): String? {
        val settings = context.getSharedPreferences(SPF_DIARY, 0)
        return settings.getString(DIARY_AUTO_SAVE + topicId, null)
    }
}
