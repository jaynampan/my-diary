package meow.softer.mydiary.setting;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static meow.softer.mydiary.shared.PermissionHelper.REQUEST_WRITE_ES_PERMISSION;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.ColorTools;
import meow.softer.mydiary.shared.FileManager;
import meow.softer.mydiary.shared.PermissionHelper;
import meow.softer.mydiary.shared.SPFManager;
import meow.softer.mydiary.shared.ScreenHelper;
import meow.softer.mydiary.shared.ThemeManager;
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,
        SettingColorPickerFragment.colorPickerCallback, AdapterView.OnItemSelectedListener {
    /**
     * Theme
     */
    private ThemeManager themeManager;
    //For spinner first run
    private boolean isThemeFirstRun = true;
    private boolean isLanguageFirstRun = true;
    //Because the default profile bg is color ,
    //so we should keep main color for replace when main color was changed.
    private int tempMainColorCode;
    /**
     * Profile
     */
    private String profileBgFileName = "";
    private boolean isAddNewProfileBg = false;
    /**
     * File
     */
    private FileManager tempFileManager;
    private final static int SELECT_PROFILE_BG = 0;

    /**
     * UI
     */
    private Spinner SP_setting_theme, SP_setting_language;
    private ImageView IV_setting_profile_bg, IV_setting_theme_main_color, IV_setting_theme_dark_color;
    private Button But_setting_theme_default_bg, But_setting_theme_default, But_setting_theme_apply;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //For set status bar
        ChinaPhoneHelper.setStatusBar(this, true);


        themeManager = ThemeManager.getInstance();
        //Create fileManager for get temp folder
        tempFileManager = new FileManager(this, FileManager.TEMP_DIR);
        tempFileManager.clearDir();

        SP_setting_theme = (Spinner) findViewById(R.id.SP_setting_theme);
        IV_setting_profile_bg = (ImageView) findViewById(R.id.IV_setting_profile_bg);
        IV_setting_theme_main_color = (ImageView) findViewById(R.id.IV_setting_theme_main_color);
        IV_setting_theme_dark_color = (ImageView) findViewById(R.id.IV_setting_theme_dark_color);
        But_setting_theme_default_bg = (Button) findViewById(R.id.But_setting_theme_default_bg);
        But_setting_theme_default = (Button) findViewById(R.id.But_setting_theme_default);
        But_setting_theme_apply = (Button) findViewById(R.id.But_setting_theme_apply);
        But_setting_theme_apply.setOnClickListener(this);

        SP_setting_language = (Spinner) findViewById(R.id.SP_setting_language);


        initSpinner();
        initTheme(themeManager.getCurrentTheme());
        initLanguage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PROFILE_BG) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    //Compute the bg size
                    int bgWidth = ScreenHelper.getScreenWidth(this);
                    int bgHeight = getResources().getDimensionPixelOffset(R.dimen.top_bar_height);
                    UCrop.Options options = new UCrop.Options();
                    options.setToolbarColor(ThemeManager.getInstance().getThemeMainColor(this));
                    options.setStatusBarColor(ThemeManager.getInstance().getThemeDarkColor(this));
                    UCrop.of(data.getData(), Uri.fromFile(new File(tempFileManager.getDir() + "/" + FileManager.createRandomFileName())))
                            .withMaxResultSize(bgWidth, bgHeight)
                            .withOptions(options)
                            .withAspectRatio(bgWidth, bgHeight)
                            .start(this);
                } else {
                    Toast.makeText(this, getString(R.string.toast_photo_intent_error), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri resultUri = UCrop.getOutput(data);
                    IV_setting_profile_bg.setImageBitmap(BitmapFactory.decodeFile(resultUri.getPath()));
                    profileBgFileName = FileManager.getFileNameByUri(this, resultUri);
                    isAddNewProfileBg = true;
                } else {
                    Toast.makeText(this, getString(R.string.toast_crop_profile_banner_fail), Toast.LENGTH_LONG).show();
                    //sample error
                    // final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_ES_PERMISSION) {
            if (grantResults.length > 0
                    && PermissionHelper.checkAllPermissionResult(grantResults)) {
                FileManager.startBrowseImageFile(this, SELECT_PROFILE_BG);
            } else {
                PermissionHelper.showAddPhotoDialog(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Revert current theme
        themeManager.setCurrentTheme(SPFManager.getTheme(this));
    }

    private void initLanguage() {
        if (SPFManager.getLocalLanguageCode(this) != -1) {
            SP_setting_language.setSelection(SPFManager.getLocalLanguageCode(this));
        }
    }

    private void initTheme(int themeId) {
        if (themeId == ThemeManager.CUSTOM) {
            IV_setting_profile_bg.setOnClickListener(this);
            IV_setting_theme_main_color.setOnClickListener(this);
            IV_setting_theme_dark_color.setOnClickListener(this);

            But_setting_theme_default_bg.setOnClickListener(this);
            But_setting_theme_default_bg.setEnabled(true);
            But_setting_theme_default.setOnClickListener(this);
            But_setting_theme_default.setEnabled(true);

            IV_setting_profile_bg.setImageBitmap(null);
        } else {
            IV_setting_profile_bg.setOnClickListener(null);
            IV_setting_theme_main_color.setOnClickListener(null);
            IV_setting_theme_dark_color.setOnClickListener(null);

            But_setting_theme_default_bg.setOnClickListener(null);
            But_setting_theme_default_bg.setEnabled(false);
            But_setting_theme_default.setOnClickListener(null);
            But_setting_theme_default.setEnabled(false);
        }
        //Save the temp Main Color Code
        tempMainColorCode = themeManager.getThemeMainColor(this);
        IV_setting_profile_bg.setImageDrawable(themeManager.getProfileBgDrawable(this));
        setThemeColor();
    }

    private void setThemeColor() {
        IV_setting_theme_main_color.setImageDrawable(
                new ColorDrawable(themeManager.getThemeMainColor(this)));
        IV_setting_theme_dark_color.setImageDrawable(
                new ColorDrawable(themeManager.getThemeDarkColor(this)));
    }

    private void initSpinner() {
        //Theme Spinner
        ArrayAdapter themeAdapter = new ArrayAdapter(this, R.layout.spinner_simple_text,
                getResources().getStringArray(R.array.theme_list));
        SP_setting_theme.setAdapter(themeAdapter);
        SP_setting_theme.setSelection(themeManager.getCurrentTheme());
        SP_setting_theme.setOnItemSelectedListener(this);

        //Language spinner
        ArrayAdapter languageAdapter = new ArrayAdapter(this, R.layout.spinner_simple_text,
                getResources().getStringArray(R.array.language_list));
        SP_setting_language.setAdapter(languageAdapter);
        SP_setting_language.setSelection(SPFManager.getLocalLanguageCode(this));
        SP_setting_language.setOnItemSelectedListener(this);
    }

    private void applySetting(boolean killProcess) {
        //Restart App
        Intent i = this.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(this.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

        if (killProcess) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } else {
            this.finish();
        }
    }

    @Override
    public void onColorChange(int colorCode, int viewId) {
        switch (viewId) {
            case R.id.IV_setting_theme_main_color:
                tempMainColorCode = colorCode;
                IV_setting_theme_main_color.setImageDrawable(new ColorDrawable(tempMainColorCode));
                if (IV_setting_profile_bg.getDrawable() instanceof ColorDrawable) {
                    IV_setting_profile_bg.setImageDrawable(
                            new ColorDrawable(tempMainColorCode));
                }
                break;
            case R.id.IV_setting_theme_dark_color:
                IV_setting_theme_dark_color.setImageDrawable(new ColorDrawable(colorCode));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_setting_theme_default_bg:
                IV_setting_profile_bg.setImageDrawable(new ColorDrawable(tempMainColorCode));
                profileBgFileName = "";
                isAddNewProfileBg = true;
                break;
            case R.id.IV_setting_profile_bg:
                if (PermissionHelper.checkPermission(this, REQUEST_WRITE_ES_PERMISSION)) {
                    FileManager.startBrowseImageFile(this, SELECT_PROFILE_BG);
                }
                break;
            case R.id.But_setting_theme_default:
                IV_setting_theme_main_color.setImageDrawable(new ColorDrawable(ColorTools.getColor(this,
                        R.color.themeColor_custom_default)));
                IV_setting_theme_dark_color.setImageDrawable(new ColorDrawable(ColorTools.getColor(this,
                        R.color.theme_dark_color_custom_default)));
                //Also revert the tempMainColor & profile bg
                tempMainColorCode = ColorTools.getColor(this, R.color.themeColor_custom_default);
                if (IV_setting_profile_bg.getDrawable() instanceof ColorDrawable) {
                    IV_setting_profile_bg.setImageDrawable(new ColorDrawable(tempMainColorCode));
                }
                break;
            case R.id.But_setting_theme_apply:
                //Save custom theme value
                if (themeManager.getCurrentTheme() == ThemeManager.CUSTOM) {
                    //Check is add new profile
                    if (isAddNewProfileBg) {
                        //For checking new profile bg is image or color.
                        boolean hasCustomProfileBannerBg = false;
                        FileManager settingFM = new FileManager(this, FileManager.SETTING_DIR);
                        if (!"".equals(profileBgFileName)) {
                            try {
                                //Copy the profile into setting dir
                                FileManager.copy(new File(tempFileManager.getDirAbsolutePath() + "/" + profileBgFileName),
                                        new File(settingFM.getDirAbsolutePath() + "/" + ThemeManager.CUSTOM_PROFILE_BANNER_BG_FILENAME));
                                hasCustomProfileBannerBg = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, getString(R.string.toast_save_profile_banner_fail), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        } else {
                            new File(settingFM.getDirAbsolutePath() + "/" + ThemeManager.CUSTOM_PROFILE_BANNER_BG_FILENAME).delete();
                        }
                        SPFManager.setCustomProfileBannerBg(this, hasCustomProfileBannerBg);
                    }
                    //Save new color
                    SPFManager.setMainColor(this,
                            ((ColorDrawable) IV_setting_theme_main_color.getDrawable()).getColor());
                    SPFManager.setSecondaryColor(this,
                            ((ColorDrawable) IV_setting_theme_dark_color.getDrawable()).getColor());
                }
                //Save new theme style
                themeManager.saveTheme(SettingActivity.this, SP_setting_theme.getSelectedItemPosition());
                //Send Toast
                Toast.makeText(this, getString(R.string.toast_change_theme), Toast.LENGTH_SHORT).show();
                applySetting(false);
                break;
            case R.id.IV_setting_theme_main_color:
                SettingColorPickerFragment mainColorPickerFragment
                        = SettingColorPickerFragment.newInstance(themeManager.getThemeMainColor(this), R.id.IV_setting_theme_main_color);
                mainColorPickerFragment.show(getSupportFragmentManager(), "mainColorPickerFragment");
                break;
            case R.id.IV_setting_theme_dark_color:
                SettingColorPickerFragment secColorPickerFragment =
                        SettingColorPickerFragment.newInstance(themeManager.getThemeDarkColor(this), R.id.IV_setting_theme_dark_color);
                secColorPickerFragment.show(getSupportFragmentManager(), "secColorPickerFragment");
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.SP_setting_theme:
                if (!isThemeFirstRun) {
                    //Temp set currentTheme .
                    //If it doesn't apply , revert it on onDestroy .
                    themeManager.setCurrentTheme(position);
                    initTheme(position);
                } else {
                    //First time do nothing
                    isThemeFirstRun = false;
                }
                break;
            case R.id.SP_setting_language:
                if (!isLanguageFirstRun) {
                    SPFManager.setLocalLanguageCode(this, position);
                    applySetting(true);
                } else {
                    //First time do nothing
                    isLanguageFirstRun = false;
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }
}
