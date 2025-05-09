package meow.softer.mydiary.shared

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.Log
import meow.softer.mydiary.R
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.shared.ColorTools.getColor
import meow.softer.mydiary.shared.SPFManager.getMainColor
import meow.softer.mydiary.shared.SPFManager.getSecondaryColor
import meow.softer.mydiary.shared.SPFManager.setTheme
import meow.softer.mydiary.shared.ScreenHelper.dpToPixel
import meow.softer.mydiary.shared.ScreenHelper.getScreenHeight
import meow.softer.mydiary.shared.ScreenHelper.getScreenWidth
import meow.softer.mydiary.shared.ScreenHelper.getStatusBarHeight
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper.Companion.deviceStatusBarType
import meow.softer.mydiary.shared.statusbar.PhoneModel
import java.io.File

class ThemeManager private constructor() {
    //Default color is TAKI
    var currentTheme: Int = TAKI

    fun getTopicBgWidth(context: Context): Int {
        return getScreenWidth(context)
    }

    fun getTopicBgHeight(context: Context): Int {
        val topbarHeight = context.resources.getDimensionPixelOffset(R.dimen.top_bar_height)
        val bgHeight: Int = if (deviceStatusBarType == PhoneModel.OTHER) {
            getScreenHeight(context) -
                    getStatusBarHeight(context) -  //diary activity top bar  + edit bottom bar
                    dpToPixel(context.resources, 40) - topbarHeight
        } else {
            getScreenHeight(context) -  //diary activity top bar  + edit bottom bar
                    dpToPixel(context.resources, 40) - topbarHeight
        }
        return bgHeight
    }

    fun getTopicBgWithoutEditBarHeight(context: Context): Int {
        val topbarHeight = context.resources.getDimensionPixelOffset(R.dimen.top_bar_height)
        val withoutEditBarHeight = getScreenHeight(context) -  //diary activity top bar
                topbarHeight
        return withoutEditBarHeight
    }

    fun saveTheme(context: Context, themeId: Int) {
        setTheme(context, themeId)
    }

    fun getProfileBgDrawable(context: Context): Drawable? {
        val bgDrawable: Drawable?
        Log.e("Mytest", "getProfileBgDrawable currentTheme:" + currentTheme)
        when (currentTheme) {
            TAKI -> {
                bgDrawable = ViewTools.getDrawable(context, R.drawable.profile_theme_bg_taki)
                Log.e("Mytest", "getProfileBgDrawable currentTheme:" + currentTheme + "TAKI")
            }

            MITSUHA -> {
                bgDrawable = ViewTools.getDrawable(context, R.drawable.profile_theme_bg_mitsuha)
                Log.e("Mytest", "getProfileBgDrawable currentTheme:" + currentTheme + "Mitsuha")
            }

            else -> {
                val settingFM = FileManager(context, FileManager.SETTING_DIR)
                val profileBgFile = File(
                    (settingFM.dirAbsolutePath
                            + "/" + CUSTOM_PROFILE_BANNER_BG_FILENAME)
                )
                bgDrawable = if (profileBgFile.exists()) {
                    Drawable.createFromPath(profileBgFile.absolutePath)
                } else {
                    ColorDrawable(getThemeMainColor(context))
                }
                Log.e("Mytest", "getProfileBgDrawable currentTheme:" + currentTheme + "default")
            }
        }
        return bgDrawable
    }

    fun getProfilePictureDrawable(context: Context): Drawable? {
        var pictureDrawable: Drawable? = try {
            val settingFM = FileManager(context, FileManager.SETTING_DIR)
            val pictureFile = File(
                (settingFM.dirAbsolutePath
                        + "/" + CUSTOM_PROFILE_PICTURE_FILENAME)
            )
            if (pictureFile.exists()) {
                Drawable.createFromPath(pictureFile.absolutePath)
            } else {
                ViewTools.getDrawable(context, R.drawable.ic_person_picture_default)
            }
        } catch (e: NullPointerException) {
            ViewTools.getDrawable(context, R.drawable.ic_person_picture_default)
        }
        return pictureDrawable
    }

    fun getTopicItemSelectDrawable(context: Context): Drawable {
        return createTopicItemSelectBg(context)
    }

    private fun createTopicItemSelectBg(context: Context): Drawable {
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(
            intArrayOf(android.R.attr.state_pressed),
            ColorDrawable(getThemeMainColor(context))
        )
        stateListDrawable.addState(
            intArrayOf(),
            ColorDrawable(Color.WHITE)
        )
        return stateListDrawable
    }

    /**
     * Any theme using the same topic bg , if it exist.
     *
     * @param context
     * @param topicId
     * @param topicType
     * @return
     */
    fun getTopicBgDrawable(context: Context, topicId: Long, topicType: Int): Drawable? {
        val returnDrawable: Drawable? = when (topicType) {
            ITopic.TYPE_MEMO -> getMemoBgDrawable(context, topicId)
            ITopic.TYPE_CONTACTS -> getContactsBgDrawable(context, topicId)
            else -> getEntriesBgDrawable(context, topicId)
        }
        return returnDrawable
    }

    fun getEntriesBgDrawable(context: Context, topicId: Long): Drawable? {
        val bgDrawable: Drawable?
        val diaryFM = FileManager(context, FileManager.DIARY_ROOT_DIR)
        val entriesBg = File(
            (diaryFM.dirAbsolutePath
                    + "/" + topicId
                    + "/" + CUSTOM_TOPIC_BG_FILENAME)
        )
        bgDrawable = if (entriesBg.exists()) {
            Drawable.createFromPath(entriesBg.absolutePath)
        } else {
            when (currentTheme) {
                TAKI -> ViewTools.getDrawable(context, R.drawable.theme_bg_taki)
                MITSUHA -> ViewTools.getDrawable(context, R.drawable.theme_bg_mitsuha)
                else -> ColorDrawable(getMainColor(context))
            }
        }
        return bgDrawable
    }

    fun getMemoBgDrawable(context: Context, topicId: Long): Drawable? {
        val bgDrawable: Drawable?
        val memoFM = FileManager(context, FileManager.MEMO_ROOT_DIR)
        val memoBg = File(
            (memoFM.dirAbsolutePath
                    + "/" + topicId
                    + "/" + CUSTOM_TOPIC_BG_FILENAME)
        )
        bgDrawable = if (memoBg.exists()) {
            Drawable.createFromPath(memoBg.absolutePath)
        } else {
            when (currentTheme) {
                TAKI, MITSUHA -> ColorDrawable(Color.WHITE)
                else -> ColorDrawable(Color.WHITE)
            }
        }
        return bgDrawable
    }

    fun getContactsBgDrawable(context: Context, topicId: Long): Drawable? {
        val bgDrawable: Drawable?
        val contactsFM = FileManager(context, FileManager.CONTACTS_ROOT_DIR)
        val contactsBg = File(
            (contactsFM.dirAbsolutePath
                    + "/" + topicId
                    + "/" + CUSTOM_TOPIC_BG_FILENAME)
        )
        bgDrawable = if (contactsBg.exists()) {
            Drawable.createFromPath(contactsBg.absolutePath)
        } else {
            when (currentTheme) {
                TAKI -> ViewTools.getDrawable(context, R.drawable.contacts_bg_taki)
                MITSUHA -> ViewTools.getDrawable(context, R.drawable.contacts_bg_mitsuha)

                else -> ColorDrawable(getMainColor(context))
            }
        }
        return bgDrawable
    }

    fun getTopicBgDefaultDrawable(context: Context, topicType: Int): Drawable? {
        val returnDefaultDrawable: Drawable? = when (topicType) {
            ITopic.TYPE_MEMO -> this.memoBgDefaultDrawable
            ITopic.TYPE_CONTACTS -> getContactsDefaultBgDrawable(context)
            else -> getEntriesBgDefaultDrawable(context)
        }
        return returnDefaultDrawable
    }

    private fun getEntriesBgDefaultDrawable(context: Context): Drawable? {
        val defaultBgDrawable = when (currentTheme) {
            TAKI -> ViewTools.getDrawable(context, R.drawable.theme_bg_taki)
            MITSUHA -> ViewTools.getDrawable(context, R.drawable.theme_bg_mitsuha)

            else -> ColorDrawable(getMainColor(context))
        }
        return defaultBgDrawable
    }

    val memoBgDefaultDrawable: Drawable
        get() {
            val defaultBgDrawable = when (currentTheme) {
                TAKI, MITSUHA -> ColorDrawable(Color.WHITE)

                else -> ColorDrawable(Color.WHITE)
            }
            return defaultBgDrawable
        }

    fun getContactsDefaultBgDrawable(context: Context): Drawable? {
        val defaultBgDrawable = when (currentTheme) {
            TAKI -> ViewTools.getDrawable(context, R.drawable.contacts_bg_taki)
            MITSUHA -> ViewTools.getDrawable(context, R.drawable.contacts_bg_mitsuha)

            else -> ColorDrawable(getMainColor(context))
        }
        return defaultBgDrawable
    }

    fun getButtonBgDrawable(context: Context): Drawable {
        return createButtonCustomBg(context)
    }

    fun createDiaryViewerInfoBg(context: Context): Drawable {
        val dp10 = dpToPixel(context.resources, 10)
        val shape = GradientDrawable()
        shape.setShape(GradientDrawable.RECTANGLE)
        shape.setColor(getThemeMainColor(context))
        shape.setCornerRadii(
            floatArrayOf(
                dp10.toFloat(),
                dp10.toFloat(),
                dp10.toFloat(),
                dp10.toFloat(),
                0f,
                0f,
                0f,
                0f
            )
        )
        return shape
    }

    fun createDiaryViewerEditBarBg(context: Context): Drawable {
        val dp10 = dpToPixel(context.resources, 10)
        val shape = GradientDrawable()
        shape.setShape(GradientDrawable.RECTANGLE)
        shape.setColor(getThemeMainColor(context))
        shape.setCornerRadii(
            floatArrayOf(
                0f,
                0f,
                0f,
                0f,
                dp10.toFloat(),
                dp10.toFloat(),
                dp10.toFloat(),
                dp10.toFloat()
            )
        )
        return shape
    }

    /**
     * Create the custom button programmatically
     *
     * @param context
     * @return
     */
    private fun createButtonCustomBg(context: Context): Drawable {
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(
            intArrayOf(android.R.attr.state_pressed),
            createCustomPressedDrawable(context)
        )
        stateListDrawable.addState(
            intArrayOf(-android.R.attr.state_enabled),
            ViewTools.getDrawable(context, R.drawable.button_bg_disable)
        )
        stateListDrawable.addState(
            intArrayOf(),
            ViewTools.getDrawable(context, R.drawable.button_bg_n)
        )
        return stateListDrawable
    }

    /**
     * The Custom button press drawable
     *
     * @param context
     * @return
     */
    private fun createCustomPressedDrawable(context: Context): Drawable {
        val padding = dpToPixel(context.resources, 5)
        val mainColorCode: Int = instance!!.getThemeMainColor(context)
        val boardColor = getColor(context, R.color.button_boarder_color)
        val gradientDrawable = GradientDrawable()
        gradientDrawable.getPadding(Rect(padding, padding, padding, padding))
        gradientDrawable.setCornerRadius(dpToPixel(context.resources, 3).toFloat())
        gradientDrawable.setStroke(dpToPixel(context.resources, 1), boardColor)
        gradientDrawable.setColor(mainColorCode)
        return gradientDrawable
    }

    /**
     * This color also is secondary color.
     *
     * @param context
     * @return
     */
    fun getThemeDarkColor(context: Context): Int {
        val darkColor: Int = when (currentTheme) {
            TAKI -> getColor(context, R.color.theme_dark_color_taki)
            MITSUHA -> getColor(context, R.color.theme_dark_color_mistuha)
            else -> getSecondaryColor(context)
        }
        return darkColor
    }

    fun getThemeMainColor(context: Context): Int {
        val mainColor: Int = when (currentTheme) {
            TAKI -> getColor(context, R.color.themeColor_taki)
            MITSUHA -> getColor(context, R.color.themeColor_mistuha)
            else -> getMainColor(context)
        }
        return mainColor
    }

    fun getThemeUserName(context: Context): String {
        val userName: String = when (currentTheme) {
            TAKI, MITSUHA -> context.getString(R.string.profile_username_default)
            else -> context.getString(R.string.your_name_is)
        }
        return userName
    }

    fun getTopicBgSavePathFile(context: Context, topicId: Long, topicType: Int): File {
        val outputFile: File
        when (topicType) {
            ITopic.TYPE_MEMO -> {
                val memoFM = FileManager(context, FileManager.MEMO_ROOT_DIR)
                outputFile = File(
                    (memoFM.dirAbsolutePath
                            + "/" + topicId
                            + "/" + CUSTOM_TOPIC_BG_FILENAME)
                )
            }

            ITopic.TYPE_CONTACTS -> {
                val contactsFM = FileManager(context, FileManager.CONTACTS_ROOT_DIR)
                outputFile = File(
                    (contactsFM.dirAbsolutePath
                            + "/" + topicId
                            + "/" + CUSTOM_TOPIC_BG_FILENAME)
                )
            }

            else -> {
                val diaryFM = FileManager(context, FileManager.DIARY_ROOT_DIR)
                outputFile = File(
                    (diaryFM.dirAbsolutePath
                            + "/" + topicId
                            + "/" + CUSTOM_TOPIC_BG_FILENAME)
                )
            }
        }
        return outputFile
    }

    val pickerStyle: Int
        get() {
            val style = R.style.Theme_MyDiary
            return style
        }

    companion object {
        const val TAKI: Int = 0
        const val MITSUHA: Int = 1
        const val CUSTOM: Int = 2

        const val CUSTOM_PROFILE_BANNER_BG_FILENAME: String = "custom_profile_banner_bg"
        const val CUSTOM_PROFILE_PICTURE_FILENAME: String = "custom_profile_picture_bg"
        const val CUSTOM_TOPIC_BG_FILENAME: String = "custom_topic_bg"


        @JvmStatic
        var instance: ThemeManager? = null
            get() {
                if (field == null) {
                    synchronized(ThemeManager::class.java) {
                        if (field == null) {
                            field = ThemeManager()
                        }
                    }
                }
                return field
            }
            private set
    }
}
