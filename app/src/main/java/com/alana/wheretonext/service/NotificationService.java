package com.alana.wheretonext.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alana.wheretonext.MainActivity;
import com.alana.wheretonext.R;

public class NotificationService extends JobIntentService {

    public static final String CHANNEL_ID = "WhereToNext_NotifChannel";
    public static final int NOTIFICATION_ID = 1;

    public NotificationService() {
        // Required empty constructor
    }
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, NOTIFICATION_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        createNotificationChannel();
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

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
