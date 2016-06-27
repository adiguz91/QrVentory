package com.example.is2.test2qrventory.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.is2.test2qrventory.NotificationAlarmServiceActivity;
import com.example.is2.test2qrventory.model.User;

/**
 * Created by Kevin on 27.06.2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        User user = intent.getParcelableExtra("user");
        Intent service1 = new Intent(context, NotificationAlarmServiceActivity.class);
        service1.putExtra("user", user);
        context.startService(service1);

    }
}
