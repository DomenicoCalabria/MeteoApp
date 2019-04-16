
package ch.supsi.dti.isin.meteoapp.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ch.supsi.dti.isin.meteoapp.utility.Weather;
import java.util.Objects;


public class WheaterNotificationsService extends IntentService {
    //nuovo meteo da notificare
    private static Weather mForecast;
    private static Weather newMForecast;

    private static Intent newIntent(Context context) {
        return new Intent(context, WheaterNotificationsService.class);
    }

    public WheaterNotificationsService() {
        super("WheaterNotificationsService");
    }

    // da chiamare (una volta) con TestService.setServiceAlarm(this, true)
    public void setServiceAlarm(Context context, boolean isOn) {
        // creo l'intent e lo impacchetto in un PendingIntent\
        //Log.v("WheaterNotificationsS", "SetServiceAlarm");
        //mContext = context;
        Intent i = WheaterNotificationsService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn)
            Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000, pi);
        else {
            Objects.requireNonNull(alarmManager).cancel(pi);
            pi.cancel();
        }
    }

    private void sendNotification() {
        //Log.v("WheaterNotificationsS", "Send Notification (With NOTIFIC)");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // in Android >= 8.f0 devo registrare il canale delle notifiche a livello di sistema (prossima slide)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // registro il canale a livello di sistema
            NotificationChannel channel = new NotificationChannel("WheaterNotificationsChannel", "Canale di notifiche sul meteo", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Canale per tenere aggiornato l'utente sulle condizioni del meteo");
            notificationManager.createNotificationChannel(channel);
        }

        // creo il contenuto della notifica
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "WheaterNotificationsChannel")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("MeteoApp: Nuova previsione")
                .setContentText(mForecast.getDesc())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(6040, mBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.v("WheaterNotificationsS", "OnHandleIntent");

        if (mForecast != null) {
            if (newMForecast != null) {
                //Log.v("WheaterNotificationsS", "OnHandleIntent (VALUES): "+mForecast+" | "+newMForecast);
                if (!newMForecast.getDesc().equals(mForecast.getDesc())) {
                    mForecast = newMForecast;
                    //Log.v("WheaterNotificationsS", "invio della notifica");
                    sendNotification();
                }
            } else {
                //Log.v("WheaterNotificationsS", "invio della notifica");
                sendNotification();
            }
        } else {
            //Log.v("WheaterNotificationsS", "onHandleIntent (NULL VALUES): " + mForecast + " | " + newMForecast);
            sendNotification("initMSG");
        }
    }

    private void sendNotification(String initMSG) {
        //Log.v("WheaterNotificationsS", "sendNotification (INIT)");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // in Android >= 8.f0 devo registrare il canale delle notifiche a livello di sistema (prossima slide)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // registro il canale a livello di sistema
            NotificationChannel channel = new NotificationChannel("WheaterNotificationsChannel", "Canale di notifiche sul meteo", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Canale per tenere aggiornato l'utente sulle condizioni del meteo");
            notificationManager.createNotificationChannel(channel);
        }

        // creo il contenuto della notifica
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "WheaterNotificationsChannel")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("MeteoApp: Inizialilzzazione")
                .setContentText(initMSG)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(6040, mBuilder.build());
    }

    public void setNewmForecast(Weather newLocalWeather){
        //Log.v("WheaterNotificationsS", "setNewmForecast: "+mForecast+" | "+newMForecast);
        if(mForecast == null)
            mForecast = newLocalWeather;
        else
            newMForecast = newLocalWeather;
    }
}
