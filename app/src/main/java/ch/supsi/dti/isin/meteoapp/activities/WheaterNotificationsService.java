package ch.supsi.dti.isin.meteoapp.activities;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Objects;

public class WheaterNotificationsService extends IntentService {

    public WheaterNotificationsService(){
        super("WheaterNotificationsService");

    }

    public WheaterNotificationsService(String name) {
        super(name);
    }

    // da chiamare (una volta) con TestService.setServiceAlarm(this, true)
    public static void setServiceAlarm(Context context, boolean isOn) {
        // creo l'intent e lo impacchetto in un PendingIntent
        Intent i = WheaterNotificationsService.newIntent(context);
        i.putExtra("nome","");
        i.putExtra("newMeteo","");
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn)
            Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    60000, pi); // 60'000 ms = 1 minuto
        else {
            Objects.requireNonNull(alarmManager).cancel(pi);
            pi.cancel();
        }
    }

    private static Intent newIntent(Context context) {
        return null;
    }

    private void sendNotification(String nome, String newMeteo) {
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
                .setContentTitle("Meteo in "+nome)
                .setContentText(newMeteo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = (PendingIntent) PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        Objects.requireNonNull(mNotificationManager).notify(0, mBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification(intent.getStringExtra("nome"),intent.getStringExtra("newMeteo"));
    }
}
