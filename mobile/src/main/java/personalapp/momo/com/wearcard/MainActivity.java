package personalapp.momo.com.wearcard;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import personalapp.momo.com.wearcard.Adapter.BusinessListAdapter;
import personalapp.momo.com.wearcard.Models.BusinessCard;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();
    private Context mContext;
    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private static final long CONNECTION_TIME_OUT = 10000L;
    private static final int REQUEST_RESOLVE_ERROR = 11011;

    private static final Gson gson = new Gson();


    Button mPush;

    private Message cardToSend;

    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_BLUETOOTH, ConnectivityManager.TYPE_ETHERNET};

    private boolean mIsHost;
    private boolean mIsConnected;

    private boolean mRisolving;


    BusinessListAdapter mBCardListAdapter;
    ArrayList<BusinessCard> mBCardList;
    BusinessCard mBusinessCard;

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPush = (Button) findViewById(R.id.button_publish);
        mListView = (ListView) findViewById(R.id.cardbusiness_list);

        mBusinessCard = new BusinessCard();

        mBusinessCard.setNome("mohamed");
        mBusinessCard.setCognome("eddaakouri");
        mBusinessCard.setEmail("prova@cazzo.it");
        mBusinessCard.setID("12");
        mBusinessCard.setmNumero("347312044");
        mBusinessCard.setmOccupazione("studente");
        mBusinessCard.setThumbnail(null);

        mPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                publish();
            }
        });

        mBCardList = new ArrayList<>();
        //--------------------------------
        // Business Card list adapter
        //--------------------------------
        mBCardListAdapter = new BusinessListAdapter(this,mBCardList);

        mListView.setAdapter(mBCardListAdapter);


    }

    public void onStart(){
        super.onStart();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.MESSAGES_API).addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            // Using Nearby is battery intensive. To preserve battery, stop subscribing or
            // publishing when the fragment is inactive.
            unsubscribe();
            unpublish();
            mGoogleApiClient.disconnect();
            Wearable.MessageApi.removeListener(mGoogleApiClient,this);
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("onConnected --> Connected");
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private byte[] serializeCard(BusinessCard bcard) throws IOException {
        return Serializer.serialize(bcard);
    }


    public void publish() {

        if(!mGoogleApiClient.isConnected()){
            if(!mGoogleApiClient.isConnecting())
                mGoogleApiClient.connect();
        }

        cardToSend = bCardTNearbyToMessage(mBusinessCard);
        if(mGoogleApiClient.isConnected()){
            System.out.println("google api is connected");
            Nearby.Messages.publish(mGoogleApiClient, cardToSend).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        System.out.println("message sended");
                    } else {
                        System.out.println("tipo di errore trovato -----------> " + status.toString());
                        handleUnsuccessfulResult(status);
                    }
                }
            });
        }
    }

    private void handleUnsuccessfulResult(Status status) {
        if(status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN){
            if(!mRisolving){
                try{
                    mRisolving = true;
                    status.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
                }catch(IntentSender.SendIntentException e){
                    e.printStackTrace();
                }
            }
        }else {
            if (status.getStatusCode() == ConnectionResult.NETWORK_ERROR) {
                Toast.makeText(getApplicationContext(),
                        "No connectivity, cannot proceed. Fix in 'Settings' and try again.",
                        Toast.LENGTH_LONG).show();
            } else {
                // To keep things simple, pop a toast for all other error messages.
                Toast.makeText(getApplicationContext(), "Unsuccessful: " +
                        status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    public void connect(){
        mGoogleApiClient.connect();
    }

    public void disconnect(){
        if ( mGoogleApiClient != null && mGoogleApiClient.isConnected() ){
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isConnectedToNetwork(){
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        for(int networkType : NETWORK_TYPES){
            if(info != null && info.isConnectedOrConnecting()){
                return true;
            }
        }
        return false;
    }

    private void unpublish() {
        System.out.println(TAG + " trying to unpublish");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.unpublish(mGoogleApiClient, cardToSend)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                System.out.println(TAG + " unpublished successfully");

                            } else {
                                System.out.println(TAG + " could not unpublish");
                                handleUnsuccessfulResult(status);
                            }
                        }
                    });
        }
    }

    private void subscribe() {
        System.out.println(TAG + " trying to subscribe");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                System.out.println(TAG + " subscribed successfully");
                            } else {
                                System.out.print(TAG + " could not subscribe");
                                handleUnsuccessfulResult(status);
                            }
                        }
                    });
        }
    }

    /**
     * Ends the subscription to messages from nearby devices. If successful, resets state. If not
     * successful, attempts to resolve any error related to Nearby permissions by
     * displaying an opt-in dialog.
     */
    private void unsubscribe() {
        System.out.println(TAG + " trying to unsubscribe");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                System.out.println(TAG + " unsubscribed successfully");

                            } else {
                                System.out.println(TAG + " could not unsubscribe");
                                handleUnsuccessfulResult(status);
                            }
                        }
                    });
        }
    }

    MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onFound(final Message message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //try {
                    Toast.makeText(getApplicationContext(), "Messaggio Ricevuto " + message.getContent().toString(), Toast.LENGTH_LONG).show();
                    System.out.println("Messaggio Ricevuto --> " + message.getContent());
                    BusinessCard foundBusinessCard = fromNearbyMessageToBusinessCard(message);
                    updateList(foundBusinessCard);
                }
            });

        }

        public void updateList(BusinessCard bcard){
            mBCardList.clear();
            mBCardList.add(bcard);
            mBCardListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLost(Message message) {
            super.onLost(message);
        }
    };

    private void decriptMessage(Message message) {
        String decriptedMex = null;
        try {
            decriptedMex = (String) Serializer.deserialize(message.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(this,"messaggio decriptato ---> " + decriptedMex, Toast.LENGTH_LONG).show();
    }

    public void deserializeMessage(Message message) throws IOException, ClassNotFoundException {
        BusinessCard mFoundCard = (BusinessCard) Serializer.deserialize(message.getContent());
        mBCardList.add(mFoundCard);
        mBCardListAdapter.notifyDataSetChanged();
    }

    public static Message bCardTNearbyToMessage(BusinessCard bCard) {
        return new Message(gson.toJson(bCard).toString().getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Creates a {@code DeviceMessage} object from the string used to construct the payload to a
     * {@code Nearby} {@code Message}.
     */
    public static BusinessCard fromNearbyMessageToBusinessCard(Message message) {
        String nearbyMessageString = new String(message.getContent()).trim();
        String moreCoolMessage = new String(nearbyMessageString.getBytes(Charset.forName("UTF-8")));
        JsonParser parser = new JsonParser();
        JsonObject jobject = parser.parse(moreCoolMessage).getAsJsonObject();
        BusinessCard actualCard = new BusinessCard();
        actualCard.setNome(jobject.get("mNome").getAsString());
        actualCard.setCognome(jobject.get("mCognome").getAsString());
        actualCard.setID(jobject.get("ID").getAsString());
        actualCard.setEmail(jobject.get("mEmail").getAsString());
        actualCard.setmNumero(jobject.get("mNumero").getAsString());
        actualCard.setmOccupazione(jobject.get("mOccupazione").getAsString());

        System.out.println(actualCard.toString());
        return actualCard;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Toast.makeText( this, "Wear onMessageReceived", Toast.LENGTH_SHORT ).show();
        publish();
    }
}
