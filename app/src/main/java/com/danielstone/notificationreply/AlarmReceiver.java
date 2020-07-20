package com.danielstone.notificationreply;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

// Also added to android manifest
public class AlarmReceiver extends BroadcastReceiver {
    public static final String KEY_TEXT_REPLY = "key_text_reply";

    public static final String CHANNEL_ID = "work_notifications";
    public static final int ID = 100;
    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        String replyLabel = "Add a reason";
        // Use androidx import
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_format_list_numbered_24,
                        "Reason", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, CHANNEL_ID)
                // set an icon here
                .setSmallIcon(R.drawable.ic_baseline_format_list_numbered_24)
                .setContentTitle("Title")
                .setContentText("Reply with a reason")
                .setAutoCancel(true).setWhen(when)
                .addAction(action)
                .setContentIntent(replyPendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(ID, builder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification for work";
            String description = "Questions for work completion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
