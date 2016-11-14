package com.sharplabs.myclientapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by syamamura on 9/29/2016.
 *
 * Captures meeting update notification sent from server side via Firebase server
 */
public class MyMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        String title = remoteMessage.getNotification().getTitle();
//        String body = remoteMessage.getNotification().getBody();
//
//        Intent intent = new Intent(this, MeetingList.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        // build notification
//        // the addAction re-use the same intent to keep the example short
//        Notification n  = new Notification.Builder(this)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pIntent)
//                .setAutoCancel(true).build();
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, n);

        // issues broadcase to be received by MeetingList activity
        Intent updateIntent = new Intent("MyMeetingUpdate");
        updateIntent.setAction(Intent.ACTION_VIEW);
        this.sendBroadcast(updateIntent);
    }

}
