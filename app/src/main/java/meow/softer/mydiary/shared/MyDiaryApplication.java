package meow.softer.mydiary.shared;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MyDiaryApplication extends Application {
    boolean hasPassword = false;
    /*
    * try to fix the locale
    * */
    public static Locale mLocale;


    @Override
    public void onCreate() {
        super.onCreate();
        //Use Fresco
        Set<RequestListener> listeners = new HashSet<>();
        listeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setRequestListeners(listeners)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);

        //To fix bug : spinner bg is dark when mode is night.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Check password
        if (SPFManager.getPassword(this).equals("")) {
            hasPassword = false;
        } else {
            hasPassword = true;
        }

        //init Theme & language
        initTheme();
        setLocaleLanguage();
    }

    private void initTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.setCurrentTheme(SPFManager.getTheme(this));
    }

    private void setLocaleLanguage() {
        switch (SPFManager.getLocalLanguageCode(this)) {
            case 1:
                mLocale = Locale.ENGLISH;
                break;
            case 2:
                mLocale = Locale.CHINESE;
                break;
            case 3:
                mLocale = new Locale("bn","");
                break;

            // 0 = default = language of system
            default:
                mLocale = Locale.getDefault();
                break;
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));

    }
    private Context updateBaseContextLocale(Context context){
        if(mLocale==null){
            mLocale=Locale.getDefault();
        }
        Locale.setDefault(mLocale);
        Configuration configuration=context.getResources().getConfiguration();
        configuration.setLocale(mLocale);
        return context.createConfigurationContext(configuration);
    }


    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

}
