package com.alana.wheretonext.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.alana.wheretonext.R;
import com.alana.wheretonext.data.models.FavoritePhrase;
import com.alana.wheretonext.service.NotificationService;

public class NotificationSettingsActivity extends AppCompatActivity {

    private Switch notifSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        notifSwitch = findViewById(R.id.notifSwitch);

        SharedPreferences sharedPrefs = getSharedPreferences("com.alana.wheretonext", MODE_PRIVATE);
        notifSwitch.setChecked(sharedPrefs.getBoolean("NotifSwitch", true));

        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences("com.alana.wheretonext", MODE_PRIVATE).edit();
                    editor.putBoolean("NotifSwitch", true);
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Push notifications turned on.", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("com.alana.wheretonext", MODE_PRIVATE).edit();
                    editor.putBoolean("NotifSwitch", false);
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Push notifications turned off.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}