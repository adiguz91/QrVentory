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

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.Interval;
import org.joda.time.Period;

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
    Event event2 = null;
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

        JodaTimeAndroid.init(this);

        events = new ArrayList<Event>();
        event = new Event();

        Calendar cal = Calendar.getInstance();
        String dateInString = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(cal.getTime());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = null;
        try {
            currentDate = formatter.parse(dateInString);
        } catch(ParseException e) {
                e.printStackTrace();
        }

        String reParsedDate = event.DateToStringParser(currentDate);

        String dtStart = "2016-06-28 18:00:00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date endDate = null;
        try {
            endDate = format.parse(dtStart);
        } catch(ParseException e) {
            e.printStackTrace();
        }

        event.setEndDate(endDate);
        event.setDescription("Description");
        event.setStartDate(currentDate);
        event.setId(0);
        event.setImageURL("");
        event.setName("Messeplatz");

        event2 = new Event();
        event2.setEndDate(endDate);
        event2.setDescription("Description2");
        event2.setStartDate(currentDate);
        event2.setId(1);
        event2.setImageURL("");
        event2.setName("Messeplatz2");

        events.add(event);
        events.add(event2);

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

        for (int count = 0; count < events.size(); count++) {
            Event currentEvent = events.get(count);
            String startDate = currentEvent.DateToStringParser(currentEvent.getStartDate());
            String endDate = currentEvent.DateToStringParser(currentEvent.getEndDate());
            int eid = (int) currentEvent.getId();

            Period period = printDifference(currentEvent.getStartDate(), currentEvent.getEndDate());
            StringBuilder sb = new StringBuilder();
            sb.append(period.getDays());
            sb.append(period.getHours());
            sb.append(period.getMinutes());
            sb.append(period.getMillis());
            String timeDiff = sb.toString();

            Notification.Builder builder = new Notification.Builder(NotificationAlarmServiceActivity.this);
            builder.setSmallIcon(android.R.drawable.stat_sys_download) //android.R.drawable.stat_sys_download
                    .setContentTitle(timeDiff + " " + currentEvent.getName() + " " + startDate + " " + endDate)
                    .setContentIntent(pendingNotificationIntent);

            Notification notification = builder.getNotification();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            mManager.notify(eid, notification);
        }
        /*Notification.Builder builder = new Notification.Builder(NotificationAlarmServiceActivity.this);
        builder.setSmallIcon(android.R.drawable.stat_sys_download) //android.R.drawable.stat_sys_download
                .setContentTitle("ContentTitle")
                .setContentIntent(pendingNotificationIntent);

        Notification notification = builder.getNotification();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mManager.notify(0, notification);*/
    }

    public Period printDifference(Date startDate, Date endDate){

        //milliseconds
        /*long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, (int) elapsedDays);
        cal.set(Calendar.HOUR_OF_DAY, (int) elapsedHours);
        cal.set(Calendar.MINUTE, (int) elapsedMinutes);
        cal.set(Calendar.SECOND, (int) elapsedSeconds);

        return cal.getTime();*/

        Interval interval = new Interval(startDate.getTime(), endDate.getTime());
        Period period = interval.toPeriod();

        return period;
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
