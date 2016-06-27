package com.example.is2.test2qrventory.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.is2.test2qrventory.NotificationAlarmServiceActivity;

/**
 * Created by Kevin on 27.06.2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, NotificationAlarmServiceActivity.class);
        context.startService(service1);

    }
}
