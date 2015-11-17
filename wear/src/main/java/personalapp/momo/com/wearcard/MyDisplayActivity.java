package personalapp.momo.com.wearcard;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.text.TextUtils;
import android.util.FloatMath;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class MyDisplayActivity extends Activity implements SensorEventListener {
    private static final String MESSAGE = "Hello Wear!";

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long mShakeTime = 0;
    private long mRotationTime = 0;
    private float mPrevGForce = 1;

    private GoogleApiClient client;
    private String nodeId;

    private static final float SHAKE_THRESHOLD = 1f;
    private static final int SHAKE_WAIT_TIME_MS = 500;
    private static final float ROTATION_THRESHOLD = 2.0f;
    private static final int ROTATION_WAIT_TIME_MS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        initApi();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    /**
     * Initializes the GoogleApiClient and gets the Node ID of the connected device.
     */
    private void initApi() {
        client = getGoogleApiClient(this);
        client.connect();
        retrieveDeviceNode();
    }

    /**
     * Returns a GoogleApiClient that can access the Wear API.
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
            }
        }).start();
    }

    /**
     * Sends a message to the connected mobile device, telling it to show a Toast.
     */
    private void sendToast() {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("im inside run");
                    Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE, null);
                }
            }).start();
        }
    }

    @Override
    protected void onPause() {
        if(client.isConnected()){
            client.disconnect();
        }
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(client.isConnected()){
            client.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }


        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectShake(event);
        }
        else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            detectRotation(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void detectShake(SensorEvent event) {
        long now = System.currentTimeMillis();

        if((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now;

            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            float delta = gForce - mPrevGForce;
            // Change background color if gForce exceeds threshold;
            // otherwise, reset the color
            if(delta > SHAKE_THRESHOLD) {
                sendToast();
            }
            mPrevGForce = gForce;
        }
    }

    private void detectRotation(SensorEvent event) {
        long now = System.currentTimeMillis();

        if((now - mRotationTime) > ROTATION_WAIT_TIME_MS) {
            mRotationTime = now;

            // Change background color if rate of rotation around any
            // axis and in any direction exceeds threshold;
            // otherwise, reset the color
            if(Math.abs(event.values[0]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[1]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[2]) > ROTATION_THRESHOLD) {
                Toast.makeText(this, "Hand rotation detected", Toast.LENGTH_LONG).show();
            }
            else {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


}
