package com.alana.wheretonext.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //NotificationService notificationService = new NotificationService("NotificationService", context);
        //notificationService.sendPushNotif();

        Intent notifIntent = new Intent(context, NotificationService.class);
        context.startService(notifIntent);
    }
}
