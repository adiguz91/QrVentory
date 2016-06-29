package com.example.is2.test2qrventory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.example.is2.test2qrventory.MainActivity;
import com.example.is2.test2qrventory.connection.EventAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.User;
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
public class NotificationAlarmServiceActivity extends Service implements VolleyResponseListener {
    private NotificationManager mManager;
    Event event = null;
    Event event2 = null;
    List<Event> events = new ArrayList<>();
    User user = null;
    PendingIntent pendingNotificationIntent = null;

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


        /*events = new ArrayList<Event>();
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
        events.add(event2);*/

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


        try {

            user = intent.getParcelableExtra("user");
            //String test = user.getFirstname();

            /*mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(this.getApplicationContext(), TabbedEventSingleActivity.class);
            intent1.putExtra("user", user);

            //Notification notification = new Notification(R.drawable.ic_launcher,"This is a test message!", System.currentTimeMillis());
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);*/
            //notification.flags |= Notification.FLAG_AUTO_CANCEL;
            //notification.setLatestEventInfo(this.getApplicationContext(), "AlarmManagerDemo", "This is a test message!", pendingNotificationIntent);

            EventAccess eventAccess = new EventAccess(user.getApiKey());
            eventAccess.getAllEvents(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onError(String message) {

    }

    @Override
    public void onResponse(Object response) {
        if (response != null) {
            events.clear();
            events.addAll((List<Event>) response);

            for (int count = 0; count < events.size(); count++) {
                Event currentEvent = events.get(count);

                if (currentEvent.getStatus() == 1) {
                    mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
                    Intent intent1 = new Intent(this.getApplicationContext(), TabbedEventSingleActivity.class);
                    intent1.putExtra("user", user);
                    intent1.putExtra("event", currentEvent);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.setAction(Long.toString(System.currentTimeMillis()));

                    pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_ONE_SHOT); //PendingIntent.FLAG_UPDATE_CURRENT

                    String startDate = currentEvent.DateToStringParser(currentEvent.getStartDate());
                    String endDate = currentEvent.DateToStringParser(currentEvent.getEndDate());
                    int eid = (int) currentEvent.getId();

                    Calendar cal = Calendar.getInstance();
                    String currentDateString = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(cal.getTime());

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date currentDate = null;
                    try {
                        currentDate = formatter.parse(currentDateString);
                    } catch(ParseException e) {
                        e.printStackTrace();
                    }

                    Period period = null;
                    StringBuilder sb = new StringBuilder();
                    try {
                        period = printDifference(currentDate, currentEvent.getEndDate()); //currentDate

                        sb.append("Event will expire in ");
                        if (period.getDays() != 0) {
                            sb.append("Days: " + period.getDays() + " ");
                        }
                        sb.append("Hours: " + period.getHours() + " ");
                        if (period.getDays() == 0) {
                            sb.append("Minutes: " + period.getMinutes() + " ");
                        }

                    } catch (IllegalArgumentException e) {
                        sb.append("The event has expired. Check your items.");
                    }

                    String timeDiff = sb.toString();

                    Notification.Builder builder = new Notification.Builder(NotificationAlarmServiceActivity.this);
                    builder.setSmallIcon(android.R.drawable.stat_sys_download) //android.R.drawable.stat_sys_download
                            .setContentTitle(currentEvent.getName()) //timeDiff + "\n" + currentEvent.getName() + "\n" + startDate + "\n" + endDate
                            .setContentIntent(pendingNotificationIntent)
                            .setContentText(timeDiff);
                    //.setStyle(new Notification.BigTextStyle().bigText(timeDiff));

                    Notification notification = builder.getNotification();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;

                    mManager.notify(eid, notification);
                } else if (currentEvent.getStatus() == 0) {
                    Calendar cal = Calendar.getInstance();
                    String currentDateString = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(cal.getTime());

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date currentDate = null;
                    try {
                        currentDate = formatter.parse(currentDateString);
                    } catch(ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        Period period = printDifference(currentEvent.getStartDate(), currentDate);

                        String message = "Event is overdue. Click here for starting.";

                        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
                        Intent intent1 = new Intent(this.getApplicationContext(), TabbedEventSingleActivity.class);
                        intent1.putExtra("user", user);
                        intent1.putExtra("event", currentEvent);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent1.setAction(Long.toString(System.currentTimeMillis()));

                        pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_ONE_SHOT); //PendingInte

                        Notification.Builder builder = new Notification.Builder(NotificationAlarmServiceActivity.this);
                        builder.setSmallIcon(android.R.drawable.stat_sys_download) //android.R.drawable.stat_sys_download
                                .setContentTitle(currentEvent.getName()) //timeDiff + "\n" + currentEvent.getName() + "\n" + startDate + "\n" + endDate
                                .setContentIntent(pendingNotificationIntent)
                                .setContentText(message);

                        Notification notification = builder.getNotification();
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;

                        int eid = (int) currentEvent.getId();
                        mManager.notify(eid, notification);
                    } catch(IllegalArgumentException e) {
                        //do nothing
                    }
                }
            }
        }

    }
}
