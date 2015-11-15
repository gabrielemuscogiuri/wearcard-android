package personalapp.momo.com.wearcard;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by YassIne on 15/11/2015.
 */
public class ShakeListener extends WearableListenerService {

    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Toast.makeText(getApplicationContext(),"ricevuto uno stronzo", Toast.LENGTH_LONG).show();

    }
}
