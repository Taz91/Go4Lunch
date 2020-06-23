package com.syc.go4lunch.utils;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.net.ConnectivityManagerCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static androidx.core.content.ContextCompat.getSystemService;

public class Utils {


    /**
     * convert string date with old format and return the new format
     * @param pDate
     * @param pOldFormat example : "yyyy-MM-dd"
     * @param pNewFormat example : "dd/MM/yyyy"
     * @return
     */
    public static String convertDate(String pDate, String pOldFormat, String pNewFormat){
        SimpleDateFormat oldFormatDate = new SimpleDateFormat(pOldFormat);
        SimpleDateFormat newFormatDate = new SimpleDateFormat(pNewFormat);
        pDate = pDate.substring(0,10);
        try {
            pDate = newFormatDate.format(oldFormatDate.parse(pDate));
        }
        catch(Exception e){
            pDate = newFormatDate.format(new Date());
        }finally {
            return pDate;
        }
    }

    /**
     * @return Calendar, target date launch notification
     */
    public static Long buildDelayDuration(){
        Calendar dCalDateDebut = Calendar.getInstance();
        Calendar dCalDateFin = Calendar.getInstance();
        dCalDateFin.set(Calendar.HOUR_OF_DAY, 9);
        dCalDateFin.set(Calendar.MINUTE, 0);
        dCalDateFin.set(Calendar.SECOND, 0);
        dCalDateFin.set(Calendar.DAY_OF_MONTH, dCalDateDebut.get(Calendar.DAY_OF_MONTH));

        long diffMillis = (dCalDateFin.getTimeInMillis() - dCalDateDebut.getTimeInMillis())/60/1000;

        //Calculate difference between now and target time
        if(diffMillis < 5){
            dCalDateFin.add(Calendar.DATE,1);
        }
        diffMillis = (dCalDateFin.getTimeInMillis() - dCalDateDebut.getTimeInMillis())/60/1000;

        return diffMillis;
    }

    /**
     * to create a Periodic notification
     * @param pbGoNotif
     * @param pcontext
     */
    public static void creatNotification(boolean pbGoNotif, Context pcontext){
        //pcontext.getApplicationContext();
        WorkManager mWorkManager = WorkManager.getInstance(pcontext);

        if( pbGoNotif){
            Constraints contraintes = new Constraints.Builder ()
                    .setRequiresBatteryNotLow(true)
                    .build ();
            //.setRequiresCharging (true)
            /*
            // ================================================= One time request !!
            OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(5,TimeUnit.MINUTES)
                    .build();
            //pull unique job in queue
            mWorkManager.enqueueUniqueWork("nyt_periodic", ExistingWorkPolicy.REPLACE, mRequest);
            */

            // ================================================= Periodic request !!
            //Get delay duration in minute
            Long delay = Utils.buildDelayDuration();

            PeriodicWorkRequest mRequest = new PeriodicWorkRequest.Builder(
                    NotificationWorker.class,
                    15,
                    TimeUnit.MINUTES,
                    PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                    TimeUnit.MILLISECONDS)
                    .setInitialDelay(delay,TimeUnit.MINUTES)
                    .build();
            //pull periodic job in queue
            mWorkManager.enqueueUniquePeriodicWork("nyt_periodic", ExistingPeriodicWorkPolicy.REPLACE, mRequest);

        }else{
            //mWorkManager.cancelAllWorkByTag("nyt_channel");
            mWorkManager.cancelUniqueWork("nyt_periodic");
        }
    }

    /**
     * launch notification with options, message content number of hits
     * @param title : name of application
     * @param message : personnal message like xx actuality for you
     * @param pcontext : context of activity
     */
    public static void showNotification(String title, String message, Context pcontext) {
        //pcontext.getApplicationContext();
        NotificationManager manager = (NotificationManager) pcontext.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "nyt_channel";
        String channelName = "nyt_name";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            // Option of Notif :
            //channel.enableLights(true);
            //channel.setLightColor(Color.RED);
            //channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            //channel.enableVibration(true);
            //channel.setShowBadge( true );
            //channel.setLockscreenVisibility( Notification.VISIBILITY_PUBLIC );

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(pcontext, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true) //when user tips on, notif is delete
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                //.setSmallIcon(R.mipmap.nyt_21x21)
                ;
        manager.notify(1, builder.build());
    }

    public static boolean checkConnection(Context context){
        // Manual check internet conn. on activity start
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }




}
