package com.ithub.groceryshop.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ithub.groceryshop.R;
import com.ithub.groceryshop.UserHomePageActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    Icon icon_1;
    private NotificationManager notificationManager;
    Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification());
    }

    private void showNotification(RemoteMessage.Notification notification) {
        // for get current time
        DateFormat df = new SimpleDateFormat("h:mm a");
        String date = df.format(Calendar.getInstance().getTime());

        Intent intent = new Intent(context.getApplicationContext(), UserHomePageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 3232, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            icon_1 = Icon.createWithResource(context, R.mipmap.ic_launcher);
        }
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Notification notification_f = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
            notification_f = new Notification.Builder(context)

                    .setContentTitle("New Product is arrived...! Please checked.")
                    .setSubText(date)
                    .setChannelId("channel_id")
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon_1)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();

            notificationManager.notify(1313, notification_f);
        } else {
            notification_f = new Notification.Builder(context)

                    .setContentTitle("New Product is arrived...! Please checked.")
                    .setSubText(date)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();

            notificationManager.notify(1313, notification_f);
        }
    }
}
