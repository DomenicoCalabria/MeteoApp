package ch.supsi.dti.isin.meteoapp.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.utility.Weather;

import java.util.Objects;

import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;

public class WheaterNotificationsService extends IntentService {

    private Weather mForecast;
    private boolean notificActive = false;
    NotificationManagerCompat notificationManager;

    private static Intent newIntent(Context context) {
        return new Intent(context, WheaterNotificationsService.class);
    }

    public WheaterNotificationsService(){
        super("WheaterNotificationsService");
    }

    public void createNotificationChannel(Context c) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WheaterApp";//getString(R.string.notificationChannel);
            String description = "Notifiche di WheaterApp";//getString(R.string.notificationChannelDesc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = NotificationManagerCompat.from(c);
        }
    }

    // da chiamare (una volta) con TestService.setServiceAlarm(this, true)
    public void setServiceAlarm(Context context, boolean isOn) {
        // creo l'intent e lo impacchetto in un PendingIntent
        if(!notificActive) {
            //NotificationCompat.Builder msgNot = new NotificationCompat.Builder(this, "default")
            //        .setSmallIcon(android.R.drawable.ic_menu_report_image)
            //        .setContentTitle("MeteoApp: Nuova previsione")
            //        .setContentText("inizializzazione...")
            //        .setPriority(NotificationCompat.PRIORITY_DEFAULT);;
            //notificationManager.notify(0,msgNot.build());
            notificActive = true;
        }
        Intent i = WheaterNotificationsService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn)
            Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000, pi); // 60'000 ms = 1 minuto
        else {
            Objects.requireNonNull(alarmManager).cancel(pi);
            pi.cancel();
        }


    }

    private void sendNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // in Android >= 8.f0 devo registrare il canale delle notifiche a livello di sistema (prossima slide)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("WheaterNotificationsChannel", "Canale di notifiche sul meteo", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Canale per tenere aggiornato l'utente sulle condizioni del meteo");
            // registro il canale a livello di sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
        // creo il contenuto della notifica

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("MeteoApp: Nuova previsione")
                .setContentText(mForecast.getDesc())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = (PendingIntent) PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(0, mBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //lancia un nullPointerException, rivedere
        //sendNotification();
    }

    public void updateService(Weather weather){
        if(mForecast == null){
            mForecast = weather;
        }else if(!mForecast.getDesc().equals(weather.getDesc())){
            mForecast = weather;
            sendNotification();
        }
    }
}
