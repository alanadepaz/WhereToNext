package com.alana.wheretonext.service;

import static android.provider.Settings.System.getString;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alana.wheretonext.MainActivity;
import com.alana.wheretonext.R;

public class NotificationService extends IntentService {

    public static final String CHANNEL_ID = "WhereToNext_NotifChannel";
    private Context context;

    public NotificationService() {
        super("NotificationService");
        // Required empty constructor
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationService(String name, Context context) {
        super(name);

        this.context = context;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sendPushNotif();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WhereToNext_NotifChannel";
            String description = "Notification channel for WhereToNext";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendPushNotif() {
        createNotificationChannel();

        // Create an explicit intent for an Activity in your app
        Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_wtn_round)
                .setContentTitle("Continue Exploring Countries!")
                .setContentText("Still interested in exploring new languages and countries? Come back to take the next steps towards your next travel journey!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Still interested in exploring new languages and countries? Come back to take the next steps towards your next travel journey!"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // notificationId is a unique int for each notification defined
        int notificationId = 1;

        notificationManager.notify(notificationId, builder.build());
    }
}
