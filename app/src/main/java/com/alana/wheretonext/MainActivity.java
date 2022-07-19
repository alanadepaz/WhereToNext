package com.alana.wheretonext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieDrawable;
import com.alana.wheretonext.service.NotificationReceiver;
import com.alana.wheretonext.service.NotificationService;
import com.alana.wheretonext.service.UserService;
import com.alana.wheretonext.ui.login.LoginActivity;
import com.alana.wheretonext.ui.map.MapFragment;
import com.alana.wheretonext.ui.settings.SettingsFragment;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseFile;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MAP_FRAGMENT = 0;
    public static final int SETTINGS_FRAGMENT = 1;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;

    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvEmail;

    private UserService userService = new UserService();
    private NotificationService notificationService = new NotificationService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPrefs = this.getSharedPreferences("com.alana.wheretonext", Context.MODE_PRIVATE);
        Boolean notifsToggled = sharedPrefs.getBoolean("NotifSwitch", true);

        if (notifsToggled)
        {
            scheduleNotificationReceiver();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);

        View headerView = navView.getHeaderView(0);

        ivProfileImage = headerView.findViewById(R.id.ivProfileImage);
        tvUsername = headerView.findViewById(R.id.tvUsername);
        tvEmail = headerView.findViewById(R.id.tvEmail);

        tvUsername.setText(userService.getUserUsername());
        tvEmail.setText(userService.getUserEmail());

        // Default profile image
        Glide.with(MainActivity.this)
                .load(R.mipmap.default_profile_round)
                .into(ivProfileImage);

        String imageURL = userService.getProfileImageURL();

        if (imageURL != null) {
            Glide.with(MainActivity.this)
                    .load(imageURL)
                    .transform(new RoundedCorners(100))
                    .placeholder(R.mipmap.default_profile_round)
                    .error(R.mipmap.default_profile_round)
                    .into(ivProfileImage);
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navView.setNavigationItemSelectedListener(this);

        // Set default selection
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();

        // Load the right activity from the Phrases page
        if (getIntent().getExtras() != null) {
            int intentFragment = getIntent().getExtras().getInt("fragmentToLoad");

            switch (intentFragment) {
                case MAP_FRAGMENT:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new MapFragment()).commit();
                    break;
                case SETTINGS_FRAGMENT:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new SettingsFragment()).commit();
                    break;
            }
        } else {
            Button btnExitDialog = new Button(this);

            LottieDialog welcomeDialog = new LottieDialog(this)
                    .setAnimation(R.raw.yellow_passport_anim)
                    .setAnimationRepeatCount(LottieDrawable.INFINITE)
                    .setAutoPlayAnimation(true)
                    .setMessage("Explore countries of interest and learn the phrases you need to know before you go! You ready?")
                    .addActionButton(btnExitDialog);

            welcomeDialog.show();

            btnExitDialog.setText("Yes! Where to Next?");
            btnExitDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (welcomeDialog.isShowing()) {
                        welcomeDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPrefs = this.getSharedPreferences("com.alana.wheretonext", Context.MODE_PRIVATE);
        Boolean notifsToggled = sharedPrefs.getBoolean("NotifSwitch", true);

        if (notifsToggled)
        {
            scheduleNotificationReceiver();
        }
    }

    public void scheduleNotificationReceiver() {

        Intent alarmIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 49);
        calendar.set(Calendar.SECOND, 1);

        long INTERVAL_WEEK = AlarmManager.INTERVAL_DAY;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                INTERVAL_WEEK, pendingIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                break;
            case R.id.nav_logout:
                userService.logoutUser();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}