package com.locksmith.fpl.Dentist.DTO;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.Dentist.ui.activity.TechHomeActivity;
import com.locksmith.fpl.R;

public class MyFirebaseMessagingService1 extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    SharedPrefrence1 prefrence;
    int i = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        prefrence = SharedPrefrence1.getInstance(this);
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
        }


        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message data 1: " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Message Notification1 Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());

            if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("Chat")) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Consts1.BROADCAST);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                i = prefrence.getIntValue("Value");
                i++;
                prefrence.setIntValue("Value", i);
            }
        }


    }


    private void sendNotification(String messageBody) {

        Intent intent = new Intent(this, TechHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";


        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("FabArtist")
                .setSound(defaultSoundUri)
                /* .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))*/
                .setContentText(messageBody).setAutoCancel(true).setContentIntent(pendingIntent);
        ;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }


//e7q2YZRtQEA:APA91bGua6Fg6xP33iq-e0s7OEPDI_Rc19V7pYt_LR7u32OFwUuDlHCwP_sx1YVTUPywQGp4OlfPrD45QJThrH9Do2y1jgIq1yUputQLNATAah7IxHAIN9rMITHrMZI0zTi7yyyiXFWH

}


