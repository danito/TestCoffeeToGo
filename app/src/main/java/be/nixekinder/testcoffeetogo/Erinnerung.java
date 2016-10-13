package be.nixekinder.testcoffeetogo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by dan on 12.10.16.
 */

public class Erinnerung extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        notifi(context, "Es wartet eine neue Belohnung auf Dich");

    }

    public void notifi(Context context, String Nachricht){
        final Intent notificationIntent = new Intent(context, MainActivity.class);
        final PendingIntent pi = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new
                NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("KaffeeClicker")
                .setOngoing(true)
                .setContentIntent(pi)
                .setContentText(Nachricht);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1,mBuilder.build());
    }
}
