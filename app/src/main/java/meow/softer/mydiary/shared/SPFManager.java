package meow.softer.mydiary.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import meow.softer.mydiary.R;

public class SPFManager {
    /**
     * config
     */
    private static final String SPF_CONFIG = "CONFIG";
    //Local language
    private static final String CONFIG_LOCAL_LANGUAGE = "CONFIG_LOCAL_LANGUAGE";
    /**
     * profile
     */
    private static final String SPF_PROFILE = "PROFILE";
    private static final String PROFILE_YOUR_NAME_IS = "YOUR_NAME_IS";
    private static final String PROFILE_MAIN_PAGE_BANNER_BG = "PROFILE_MAIN_PAGE_BANNER_BG";

    /**
     * Theme
     */
    //Support old version: CONFIG - CONFIG_THEME
    private static final String CONFIG_THEME = "CONFIG_THEME";
    //Theme SFP setting
    private static final String SPF_THEME = "THEME";
    private static final String THEME_MAIN_COLOR = "THEME_MAIN_COLOR";
    private static final String THEME_SEC_COLOR = "THEME_SEC_COLOR";

    /**
     * System
     */
    private static final String SPF_SYSTEM = "SYSTEM";
    //@deprecated
    private static final String FIRST_RUN = "FIRST_RUN";
    private static final String SYSTEM_VERSIONCODE = "VERSIONCODE";
    public static final int DEFAULT_VERSIONCODE = -1;
    private static final String DESCRIPTION_CLOSE = "DESCRIPTION_CLOSE";
    private static final String ENCRYPTED_PASSWORD = "ENCRYPTED_PASSWORD";

    /**
     * OOBE:
     * Add in  Version 33 , Not use now.
     */
    private static final String SPF_OOBE = "OOBE";

    /**
     * Diary auto save
     */
    private static final String SPF_DIARY = "DIARY";
    //The json file like the backup file
    private static final String DIARY_AUTO_SAVE = "DIARY_AUTO_SAVE_";

    /**
     * Config method
     */

    public static int getLocalLanguageCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG, 0);
        //default is 0 , follow the system
        return settings.getInt(CONFIG_LOCAL_LANGUAGE, 0);
    }

    public static void setLocalLanguageCode(Context context, int languageCode) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(CONFIG_LOCAL_LANGUAGE, languageCode);
        PE.commit();
        // note here it must use commit for immediate writing,because the thread will
        // be destroyed soon but apply is async,will do its job when the system is free
        Log.e("Mytest", "language code was set to" + languageCode);
    }

    /**
     * Profile method
     */

    public static String getYourName(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_PROFILE, 0);
        //default is space
        return settings.getString(PROFILE_YOUR_NAME_IS, "");
    }

    public static void setYourName(Context context, String yourNameIs) {
        SharedPreferences settings = context.getSharedPreferences(SPF_PROFILE, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(PROFILE_YOUR_NAME_IS, yourNameIs);
        PE.apply();
    }

    public static boolean hasCustomProfileBannerBg(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_PROFILE, 0);
        //default is space
        return settings.getBoolean(PROFILE_MAIN_PAGE_BANNER_BG, false);
    }

    public static void setCustomProfileBannerBg(Context context, boolean customProfileBg) {
        SharedPreferences settings = context.getSharedPreferences(SPF_PROFILE, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putBoolean(PROFILE_MAIN_PAGE_BANNER_BG, customProfileBg);
        PE.apply();
    }

    /**
     * Theme method
     */

    public static int getTheme(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG, 0);
        //default is close
        return settings.getInt(CONFIG_THEME, ThemeManager.getInstance().currentTheme);

    }

    public static void setTheme(Context context, int theme) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(CONFIG_THEME, theme);
        PE.apply();
    }

    public static int getMainColor(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_THEME, 0);
        //default is space
        return settings.getInt(THEME_MAIN_COLOR,
                ColorTools.getColor(context, R.color.themeColor_custom_default));
    }

    public static void setMainColor(Context context, int colorCode) {
        SharedPreferences settings = context.getSharedPreferences(SPF_THEME, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(THEME_MAIN_COLOR, colorCode);
        PE.apply();
    }

    public static int getSecondaryColor(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_THEME, 0);
        //default is space
        return settings.getInt(THEME_SEC_COLOR,
                ColorTools.getColor(context, R.color.theme_dark_color_custom_default));
    }

    public static void setSecondaryColor(Context context, int colorCode) {
        SharedPreferences settings = context.getSharedPreferences(SPF_THEME, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(THEME_SEC_COLOR, colorCode);
        PE.apply();
    }
    /**
     * System method
     */

    /**
     * @param firstRun
     * @deprecated it after version 33
     * now use ShowcaseView - singleShot to run OOBE onve.
     */
    public static void setFirstRun(Context context, boolean firstRun) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putBoolean(FIRST_RUN, firstRun);
        PE.apply();
    }

    /**
     * @param context
     * @return
     * @deprecated it after version 33
     * now use ShowcaseView - singleShot to run OOBE once.
     */
    public static boolean getFirstRun(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        return settings.getBoolean(FIRST_RUN, true);
    }

    public static void setVersionCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(SYSTEM_VERSIONCODE, Build.VERSION.SDK_INT);
        PE.apply();
    }


    public static int getVersionCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        return settings.getInt(SYSTEM_VERSIONCODE, DEFAULT_VERSIONCODE);
    }

    public static boolean getReleaseNoteClose(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        return settings.getBoolean(DESCRIPTION_CLOSE, false);
    }

    public static void setReleaseNoteClose(Context context, boolean close) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putBoolean(DESCRIPTION_CLOSE, close);
        PE.apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        /*
         * if string is "" , it is mean no password now.
         */
        return settings.getString(ENCRYPTED_PASSWORD, "");

    }

    public static void setAndEncryptPassword(Context context, String password) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(ENCRYPTED_PASSWORD, password);
        PE.apply();
    }
    /**
     * Diary
     */

    /**
     * Set  the  auto saved diary
     * The key is DIARY_AUTO_SAVE_TOPICID
     * <p>
     * set String null to clear it
     *
     * @param context
     * @param topicId
     * @param diaryJson
     */
    public static void setDiaryAutoSave(Context context, long topicId, String diaryJson) {
        SharedPreferences settings = context.getSharedPreferences(SPF_DIARY, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(DIARY_AUTO_SAVE + topicId, diaryJson);
        PE.apply();
    }

    /**
     * set the null value to clear auto save content
     *
     * @param context
     * @param topicId
     */
    public static void clearDiaryAutoSave(Context context, long topicId) {
        SharedPreferences settings = context.getSharedPreferences(SPF_DIARY, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(DIARY_AUTO_SAVE + topicId, null);
        PE.apply();
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
    public static String getDiaryAutoSave(Context context, long topicId) {
        SharedPreferences settings = context.getSharedPreferences(SPF_DIARY, 0);
        return settings.getString(DIARY_AUTO_SAVE + topicId, null);
    }
}
