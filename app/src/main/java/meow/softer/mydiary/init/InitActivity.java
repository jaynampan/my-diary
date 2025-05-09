package meow.softer.mydiary.init;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import meow.softer.mydiary.MainActivity;
import meow.softer.mydiary.R;
import meow.softer.mydiary.security.PasswordActivity;
import meow.softer.mydiary.shared.MyDiaryApplication;

import java.util.Locale;

public class InitActivity extends AppCompatActivity implements InitTask.InitCallBack {
    private int initTime = 2200; // 2.5S
    private Handler initHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        initHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHandler.postDelayed(() -> new InitTask(InitActivity.this, InitActivity.this).execute(), initTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        initHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onInitCompiled(boolean showReleaseNote) {

        if (((MyDiaryApplication) getApplication()).isHasPassword()) {
            Intent goSecurityPageIntent = new Intent(this, PasswordActivity.class);
            goSecurityPageIntent.putExtra("password_mode", PasswordActivity.VERIFY_PASSWORD);
            goSecurityPageIntent.putExtra("showReleaseNote", showReleaseNote);
            finish();
            InitActivity.this.startActivity(goSecurityPageIntent);
        } else {
            Intent goMainPageIntent = new Intent(InitActivity.this, MainActivity.class);
            goMainPageIntent.putExtra("showReleaseNote", showReleaseNote);
            finish();
            InitActivity.this.startActivity(goMainPageIntent);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));

    }

    private Context updateBaseContextLocale(Context context) {
        Locale locale = MyDiaryApplication.mLocale;
        Log.e("Mytest", "init mLocale:" + locale);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }
}
