package com.example.is2.test2qrventory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.is2.test2qrventory.MainActivity;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.notification.NotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Kevin on 27.06.2016.
 */
public class NotificationAlarmServiceActivity extends Service {
    private NotificationManager mManager;
    Event event = null;
    List<Event> events = null;

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();

        events = new ArrayList<Event>();
        event = new Event();

        /*Calendar cal = Calendar.getInstance();
        String dateInString = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(cal.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate = null;
        try {
            parsedDate = formatter.parse(dateInString);
        } catch(ParseException e) {
                e.printStackTrace();
        }

        event.setStartDate(parsedDate);*/



        /*Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());*/

    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);

        //Notification notification = new Notification(R.drawable.ic_launcher,"This is a test message!", System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //notification.setLatestEventInfo(this.getApplicationContext(), "AlarmManagerDemo", "This is a test message!", pendingNotificationIntent);

        Notification.Builder builder = new Notification.Builder(NotificationAlarmServiceActivity.this);
        builder.setSmallIcon(android.R.drawable.stat_sys_download) //android.R.drawable.stat_sys_download
                .setContentTitle("ContentTitle")
                .setContentIntent(pendingNotificationIntent);

        Notification notification = builder.getNotification();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mManager.notify(0, notification);
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
