package com.alana.wheretonext.ui.settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alana.wheretonext.MainActivity;
import com.alana.wheretonext.R;
import com.alana.wheretonext.exceptions.UserException;
import com.alana.wheretonext.service.UserService;

import java.io.File;

public class EditProfileSettingsActivity extends AppCompatActivity {

    public static final String TAG = "EditProfileSettingsActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int SETTINGS_FRAGMENT = 1;

    private File photoFile;
    public String photoFileName = "photo.jpg";

    private UserService userService = new UserService();
    private TextView tvEditProfile;
    private TextView tvEditUsername;
    private TextView tvEditEmail;
    private TextView tvEditPassword;
    private EditText etEditUsername;
    private EditText etEditEmail;
    private EditText etEditPassword;
    private Button btnSetProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_settings);

        tvEditProfile = findViewById(R.id.tvEditProfile);
        tvEditUsername = findViewById(R.id.tvEditUsername);
        tvEditEmail = findViewById(R.id.tvEditEmail);
        tvEditPassword = findViewById(R.id.tvEditPassword);
        etEditUsername = findViewById(R.id.etEditUsername);
        etEditEmail = findViewById(R.id.etEditEmail);
        etEditPassword = findViewById(R.id.etEditPassword);
        btnSetProfilePic = findViewById(R.id.btnSetProfilePic);

        btnSetProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                //ivProfileImage.setImageBitmap(takenImage);

                try {
                    userService.setProfileImage(photoFile);
                } catch (UserException e) {
                    e.printStackTrace();
                }

            } else { // Result was a failure
                Toast.makeText(getApplicationContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent settingsIntent = new Intent(this, MainActivity.class);
        settingsIntent.putExtra("fragmentToLoad", SETTINGS_FRAGMENT);
        startActivity(settingsIntent);
    }
}