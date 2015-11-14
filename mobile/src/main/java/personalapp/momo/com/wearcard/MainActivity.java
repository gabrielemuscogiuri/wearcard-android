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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;

import java.io.IOException;
import java.util.ArrayList;

import personalapp.momo.com.wearcard.Adapter.BusinessListAdapter;
import personalapp.momo.com.wearcard.Models.BusinessCard;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();
    private Context mContext;
    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private static final long CONNECTION_TIME_OUT = 10000L;
    private static final int REQUEST_RESOLVE_ERROR = 11011;

    Button mPush;

    private Message cardToSend;

    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_BLUETOOTH, ConnectivityManager.TYPE_ETHERNET};

    private boolean mIsHost;
    private boolean mIsConnected;

    private boolean mRisolving;


    BusinessListAdapter mBCardListAdapter;
    ArrayList<BusinessCard> mBCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPush = (Button) findViewById(R.id.button_publish);

        mPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                publish("prova a publicare");
            }
        });
        
        mBCardList = new ArrayList<>();
        //--------------------------------
        // Business Card list adapter
        //--------------------------------
        mBCardListAdapter = new BusinessListAdapter(this,mBCardList);


    }

    public void onStart(){
        super.onStart();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.MESSAGES_API)
                .build();

    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            // Using Nearby is battery intensive. To preserve battery, stop subscribing or
            // publishing when the fragment is inactive.
            unsubscribe();
            unpublish();

            mGoogleApiClient.disconnect();
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


    public void publish(String message) {

        if(!mGoogleApiClient.isConnected()){
            if(!mGoogleApiClient.isConnecting())
                mGoogleApiClient.connect();
        }
        //byte[] businessCardByte = serializeCard(bcard);
        byte[] businessCardByte = message.getBytes();
        cardToSend = new Message(businessCardByte);
        if(mGoogleApiClient.isConnected()){
            System.out.println("google api is connected");
            Nearby.Messages.publish(mGoogleApiClient, cardToSend).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        System.out.println("success");
                    } else {
                        System.out.println("tipo di errore trovato ----------->" + status.toString());
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
                        decriptMessage(message);
                    //} catch (IOException e) {
                      //  e.printStackTrace();
                   // } catch (ClassNotFoundException e) {
                   //     e.printStackTrace();
                   // }
                }
            });

        }

        @Override
        public void onLost(Message message) {
            super.onLost(message);
        }
    };

    private void decriptMessage(Message message) {
        String decriptedMex = message.toString();
        System.out.println(decriptedMex);
    }

    public void deserializeMessage(Message message) throws IOException, ClassNotFoundException {
        BusinessCard mFoundCard = (BusinessCard) Serializer.deserialize(message.getContent());
        mBCardList.add(mFoundCard);
        mBCardListAdapter.notifyDataSetChanged();
    }

}
