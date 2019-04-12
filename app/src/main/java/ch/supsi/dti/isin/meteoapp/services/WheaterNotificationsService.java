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
    private Weather mForecast;
    private Weather newMForecast;
    private Context mContext;

    private static Intent newIntent(Context context) {
        return new Intent(context, WheaterNotificationsService.class);
    }

    public WheaterNotificationsService() {
        super("WheaterNotificationsService");
    }

    // da chiamare (una volta) con TestService.setServiceAlarm(this, true)
    public void setServiceAlarm(Context context, boolean isOn) {
        // creo l'intent e lo impacchetto in un PendingIntent\
        Log.v("WheaterNotificationsS", "SetServiceAlarm");
        mContext = context;
        Intent i = WheaterNotificationsService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn)
            Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 500, pi);
        else {
            Objects.requireNonNull(alarmManager).cancel(pi);
            pi.cancel();
        }
    }

    private void sendNotification() {
        Log.v("WheaterNotificationsS", "Send Notification");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // in Android >= 8.f0 devo registrare il canale delle notifiche a livello di sistema (prossima slide)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("WheaterNotificationsChannel", "Canale di notifiche sul meteo", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Canale per tenere aggiornato l'utente sulle condizioni del meteo");

            notificationManager.createNotificationChannel(channel);
            // registro il canale a livello di sistema
            //notificationManager = getSystemService(NotificationManager.class);
            //Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        // creo il contenuto della notifica
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "WheaterNotificationsChannel")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("MeteoApp: Nuova previsione")
                .setContentText(mForecast.getDesc())
                //.setContentText("notifica")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(6040, mBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("WheaterNotificationsS", "OnHandleIntent");

        //if(mContext != null)
        sendNotification();

        /*if(mForecast != null && newMForecast != null){
            if(!newMForecast.getDesc().equals(mForecast.getDesc())){
                mForecast = newMForecast;
                Log.v("cisiamo", "invio della notifica");
                sendNotification();
            }
        }*/
    }

    public void setNewmForecast(Weather newLocalWeather){
        if(mForecast == null)
            this.mForecast = newLocalWeather;
        else
            this.newMForecast = newLocalWeather;
    }
}
