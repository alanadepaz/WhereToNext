package com.alana.wheretonext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieDrawable;
import com.alana.wheretonext.service.UserService;
import com.alana.wheretonext.ui.login.LoginActivity;
import com.alana.wheretonext.ui.map.MapFragment;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseFile;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;

    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvEmail;

    private UserService userService = new UserService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        ParseFile image = userService.getProfileImage();

        if (image != null) {
            Glide.with(MainActivity.this)
                    .load(image.getUrl())
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).commit();
                break;
            case R.id.nav_logout:
                userService.logoutUser();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
        }
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

    public Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }
}