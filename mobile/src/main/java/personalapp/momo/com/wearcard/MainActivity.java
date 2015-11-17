package personalapp.momo.com.wearcard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    private static final String CUSTOM_INTENT = "personalapp.momo.com.wearcard.CARD_ACCEPTED";
    private Context mContext;
    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private static final long CONNECTION_TIME_OUT = 10000L;
    private static final int REQUEST_RESOLVE_ERROR = 11011;
    private static final int NOTIFICATION_ID = 11011;
    private RelativeLayout mPresentationLayout;


    private static final Gson gson = new Gson();

    private final BroadcastReceiver myCardReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateList(mFoundBusinessCard);
            cancelNotification();
        }
    };



    private Message cardToSend = null;

    private boolean mRisolving;


    BusinessListAdapter mBCardListAdapter;
    ArrayList<BusinessCard> mBCardList;
    BusinessCard mBusinessCard;
    BusinessCard mFoundBusinessCard;

    ListView mListView;

    boolean mListVisible = false;
    boolean mPresentationVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = this.getActionBar();
        actionBar.hide();

        mListView = (ListView) findViewById(R.id.cardbusiness_list);

        mPresentationLayout = (RelativeLayout) findViewById(R.id.empty_list_background);

        mBusinessCard = new BusinessCard();

        mBusinessCard.setNome("Mohamed");
        mBusinessCard.setCognome("Eddaakouri");
        mBusinessCard.setEmail("prova@cazzo.it");
        mBusinessCard.setID("12");
        mBusinessCard.setmNumero("347312044");
        mBusinessCard.setmOccupazione("Studente Unito-Informatica");
        mBusinessCard.setThumbnail(null);

        BusinessCard bCard = new BusinessCard();

        bCard.setNome("Michele");
        bCard.setCognome("Vergnano");
        bCard.setEmail("lhoinventatoio@internet.web");
        bCard.setID("15");
        bCard.setmNumero("347312044");
        bCard.setmOccupazione("presentatore sagra salsiccia");
        bCard.setThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.mv_profilo));

        BusinessCard b2Card = new BusinessCard();

        b2Card.setNome("Gabriele");
        b2Card.setCognome("Muscogiuri");
        b2Card.setEmail("un@internet.web");
        b2Card.setID("35");
        b2Card.setmNumero("347312044");
        b2Card.setmOccupazione("developer");
        b2Card.setThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.gm_profilo));

        BusinessCard b3Card = new BusinessCard();

        b3Card.setNome("Trevor");
        b3Card.setCognome("Devalle");
        b3Card.setEmail("un@internet.web");
        b3Card.setID("35");
        b3Card.setmNumero("347312044");
        b3Card.setmOccupazione("Finlandia");
        b3Card.setThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.td_profilo));

        mBCardList = new ArrayList<>();
        //--------------------------------
        // Business Card list adapter
        //--------------------------------

        mBCardListAdapter = new BusinessListAdapter(this,mBCardList);

        mListView.setAdapter(mBCardListAdapter);

        /*
        updateList(bCard);
        updateList(b2Card);
        updateList(b3Card);
        */
        IntentFilter niFilter = new IntentFilter(CUSTOM_INTENT);
        registerReceiver(myCardReceiver, niFilter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myCardReceiver);
        cancelNotification();
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            // Using Nearby is battery intensive. To preserve battery, stop subscribing or
            // publishing when the fragment is inactive.
            unpublish();
            unsubscribe();

            Wearable.MessageApi.removeListener(mGoogleApiClient,this);
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
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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


    private void unpublish() {
        System.out.println(TAG + " trying to unpublish");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
            else{
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
            }else {
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
    }

    MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onFound(final Message message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Messaggio Ricevuto --> " + message.getContent());
                    mFoundBusinessCard = fromNearbyMessageToBusinessCard(message);
                    onBusinessCardReceived();
                }
            });
        }

        @Override
        public void onLost(Message message) {
            super.onLost(message);
        }
    };
    public void updateFoundCard(BusinessCard card) {

    }

    public void updateList(BusinessCard bcard){
        mBCardList.add(bcard);
        mBCardListAdapter.notifyDataSetChanged();
        if(mBCardList.size() > 0){
            showList(true);
        }
    }

    private void showList(boolean show){
        if(show){
            mListView.setVisibility(View.VISIBLE);
            mPresentationLayout.setVisibility(View.GONE);
        }
        else{
            mListView.setVisibility(View.GONE);
            mPresentationLayout.setVisibility(View.VISIBLE);
        }
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
        publish();
    }

    public void onBusinessCardReceived() {

        if(mBCardList.contains(mFoundBusinessCard)){
            return;
        }

        // Create an intent for the reply action
        Intent acceptIntent = new Intent(CUSTOM_INTENT);
        //acceptIntent.putExtra("action", "ok");

        PendingIntent acceptPendingIntent =
                PendingIntent.getBroadcast(this, 0, acceptIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        // Create the action
        NotificationCompat.Action acceptAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_done_white_24dp,
                        "Accetta Card", acceptPendingIntent)
                        .build();


        Bitmap bigPicture = BitmapFactory.decodeResource(getResources(), R.drawable.foto_profilo2);


        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                .setSummaryText(mFoundBusinessCard.getNome() + " " + mFoundBusinessCard.getCognome())
                .bigPicture(bigPicture);

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText("www.linkedin.it/santalfredos");

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setBackground(bigPicture)
                        .addAction(acceptAction);


        Notification standardNotification = new NotificationCompat.Builder(this)
                .setContentTitle("Contatto: " + mFoundBusinessCard.getNome() + " " + mFoundBusinessCard.getCognome())
                .setSmallIcon(R.drawable.ic_stat_logo_figo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setStyle(bigStyle)
                .setStyle(bigPictureStyle)
                .extend(wearableExtender)
                .build();


        mNotificationManager = NotificationManagerCompat.from(this);
        mNotificationManager.notify(NOTIFICATION_ID, standardNotification);

    }

    NotificationManagerCompat mNotificationManager;


    public void cancelNotification(){
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

}
