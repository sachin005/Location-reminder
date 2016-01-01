package s.chavan.finalapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by Sachin on 5/15/2015.
 */
public class AlarmManagerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        BroadcastObserver broadcastObserver=new BroadcastObserver();
//        broadcastObserver.triggerObservers();

        CharSequence notify_msg="You need to get to your car!";
        CharSequence title= "Ride Finder";
        CharSequence details= "The parking time will be up in 5 minutes!";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify= new Notification(android.R.drawable.stat_notify_more,
                notify_msg,
                System.currentTimeMillis());
//        Context mycontext= context;
        Intent myintent= new Intent (context, MainActivity.class);
        PendingIntent pending= PendingIntent.getActivity(context, 0, myintent, 0); ///0's are not applicable here
        notify.setLatestEventInfo(context, title, details, pending);
        notify.defaults |= Notification.DEFAULT_SOUND;
        notify.defaults |= Notification.DEFAULT_VIBRATE;
        notify.defaults |= Notification.DEFAULT_SOUND;
        Toast.makeText(context, "show_notification", Toast.LENGTH_SHORT).show();

        notificationManager.notify(0, notify);
    }


}
