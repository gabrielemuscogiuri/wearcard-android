package personalapp.momo.com.wearcard;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class MyPostNotificationReceiver extends BroadcastReceiver {
    public static final String CONTENT_KEY = "contentText";

    public MyPostNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent displayIntent = new Intent(context, MyDisplayActivity.class);
        String text = intent.getStringExtra(CONTENT_KEY);
        Bitmap bitmap = Bitmap.createBitmap(320,320,Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.RED);

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(text)
                .extend(new Notification.WearableExtender().setBackground(bitmap)
                        .setDisplayIntent(PendingIntent.getActivity(context, 0, displayIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT)))
                .build();
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, notification);

        Toast.makeText(context, context.getString(R.string.notification_posted), Toast.LENGTH_SHORT).show();
    }
}
