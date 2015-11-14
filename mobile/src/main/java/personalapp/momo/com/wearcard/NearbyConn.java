package personalapp.momo.com.wearcard;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;

import java.io.IOException;

import personalapp.momo.com.wearcard.Models.BusinessCard;

/**
 * Created by YassIne on 14/11/2015.
 */
public class NearbyConn implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Connections.ConnectionRequestListener, Connections.MessageListener, Connections.EndpointDiscoveryListener {

    private static final String TAG = NearbyConn.class.getName();
    private Context mContext;
    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private static final long CONNECTION_TIME_OUT = 10000L;
    private static final int REQUEST_RESOLVE_ERROR = 11011;

    private Message cardToSend;

    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_BLUETOOTH, ConnectivityManager.TYPE_ETHERNET};

    private boolean mIsHost;
    private boolean mIsConnected;

    private boolean mRisolving;

    public NearbyConn(Context context, Activity activity) {
        mActivity = activity;
        mContext = context;
        mRisolving = false;




        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.MESSAGES_API)
                .build();
    }

    private byte[] serializeCard(BusinessCard bcard) throws IOException {
        return Serializer.serialize(bcard);
    }


    public void publish(BusinessCard bcard) throws IOException {

        if(!mGoogleApiClient.isConnected()){
            if(!mGoogleApiClient.isConnecting())
                mGoogleApiClient.connect();
        }
        byte[] businessCardByte = serializeCard(bcard);
        cardToSend = new Message(businessCardByte);
        Nearby.Messages.publish(mGoogleApiClient, cardToSend).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if(status.isSuccess()){
                    System.out.println("success");
                }
                else{
                    handleUnsuccessfulResult(status);
                }
            }
        });
    }

    private void handleUnsuccessfulResult(Status status) {
        if(status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN){
            if(!mRisolving){
                try{
                    mRisolving = true;
                    status.startResolutionForResult(getActivity(),REQUEST_RESOLVE_ERROR);
                }catch(IntentSender.SendIntentException e){
                    e.printStackTrace();
                }
            }
        }else {
            if (status.getStatusCode() == ConnectionResult.NETWORK_ERROR) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "No connectivity, cannot proceed. Fix in 'Settings' and try again.",
                        Toast.LENGTH_LONG).show();
            } else {
                // To keep things simple, pop a toast for all other error messages.
                Toast.makeText(getActivity().getApplicationContext(), "Unsuccessful: " +
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
            Nearby.Messages.unpublish(mGoogleApiClient,cardToSend)
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

    public boolean sendCard(BusinessCard cardTosend){
        if(!isConnectedToNetwork())
            return false;
        String name = "Nearby Advertising";
        return true;
    }
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionRequest(String s, String s1, String s2, byte[] bytes) {

    }

    @Override
    public void onEndpointFound(String s, String s1, String s2, String s3) {

    }

    @Override
    public void onEndpointLost(String s) {

    }

    @Override
    public void onMessageReceived(String s, byte[] bytes, boolean b) {

    }

    @Override
    public void onDisconnected(String s) {

    }

    public Context getContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public boolean isHost() {
        return mIsHost;
    }

    public void setHost(boolean mIsHost) {
        this.mIsHost = mIsHost;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public void setConnected(boolean mIsConnected) {
        this.mIsConnected = mIsConnected;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }
}
