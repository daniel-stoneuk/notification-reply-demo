package com.danielstone.notificationreply;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.RemoteInput;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

import static com.danielstone.notificationreply.AlarmReceiver.ID;
import static com.danielstone.notificationreply.AlarmReceiver.KEY_TEXT_REPLY;

public class MainActivity extends AppCompatActivity {

    public static final int ALARM_INTENT_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // When they reply this activity will launch - check if a message was sent with it
        CharSequence text = getMessageText(getIntent());
        if (text != null) {
            // We have a reply
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Cancel the notification
            if (notificationManager != null) {
                notificationManager.cancel(ID);
            }
            // Now you can add to your database here and even direct the user to the database if you
            // like
            Toast.makeText(this, "Text: " + text, Toast.LENGTH_SHORT).show();
        }


        // Schedule the alarm by cancelling - it doesnt matter if it is already scheduled
        // we will cancel first then schedule again
        Calendar calendar = Calendar.getInstance();
        // Set the time we want it to appear
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        // If time is in the past (i.e. earlier today) then add a day to it
        long time = calendar.getTimeInMillis();
        if (time < System.currentTimeMillis()) time += 24 * 60 * 60 * 1000;

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                ALARM_INTENT_REQUEST_CODE,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (am != null) {
            am.cancel(pendingIntent);
            am.setRepeating(AlarmManager.RTC_WAKEUP,
                    time,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            // Key from our alarm receiver
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }

}