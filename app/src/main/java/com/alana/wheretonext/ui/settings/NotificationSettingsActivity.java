package com.alana.wheretonext.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import com.alana.wheretonext.R;

public class NotificationSettingsActivity extends AppCompatActivity {

    private Switch notifSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        notifSwitch = findViewById(R.id.notifSwitch);

        // TODO: Update this so that notifications only show if the user wants them
        SharedPreferences sharedPreferences = getSharedPreferences("com.alana.wheretonext", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NotifSwitch", notifSwitch.isChecked());
        editor.apply();
    }
}