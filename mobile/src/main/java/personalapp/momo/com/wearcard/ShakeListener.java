package personalapp.momo.com.wearcard;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by YassIne on 15/11/2015.
 */
public class ShakeListener extends WearableListenerService {

    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";
/*
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

         * Receive the message from wear

        if (messageEvent.getPath().equals(HELLO_WORLD_WEAR_PATH)) {
            Intent startIntent = new Intent(this, MyActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }

    }*/
}
