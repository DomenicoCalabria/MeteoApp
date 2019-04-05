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
import android.support.v4.app.NotificationCompat;

import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.utility.NotificationRestClient;
import ch.supsi.dti.isin.meteoapp.utility.Weather;

import java.util.Objects;

import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;

public class WheaterNotificationsService extends IntentService {

    private Location mLocation;
    private Weather mForecast;

    private static Intent newIntent(Context context) {
        return new Intent(context, WheaterNotificationsService.class);
    }

    public WheaterNotificationsService(){
        super("WheaterNotificationsService");
    }

    public WheaterNotificationsService(Location mLocation) {
        super("WheaterNotificationsService");
        this.mLocation = mLocation;
    }

    // da chiamare (una volta) con TestService.setServiceAlarm(this, true)
    public void setServiceAlarm(Context context, boolean isOn) {
        // creo l'intent e lo impacchetto in un PendingIntent
        Intent i = WheaterNotificationsService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn)
            Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000, pi); // 60'000 ms = 1 minuto
        else {
            Objects.requireNonNull(alarmManager).cancel(pi);
            pi.cancel();
        }

        NotificationRestClient openWeatherClient = new NotificationRestClient(this);
        openWeatherClient.request(mLocation.getName());
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
        Objects.requireNonNull(mNotificationManager).notify(0, mBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //lancia un nullPointerException, rivedere
        //sendNotification();
    }

    public void updateService(Weather weather){
        if(mForecast == null){
            mForecast = weather;
        }else if(!weather.getDesc().equals(mForecast.getDesc())){
            mForecast = weather;
            sendNotification();
        }
    }

    public void updateLocation(Location locFound) {
        this.mLocation = locFound;
    }
}
