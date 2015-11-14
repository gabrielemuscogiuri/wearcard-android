package personalapp.momo.com.wearcard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.messages.Message;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;

import personalapp.momo.com.wearcard.Models.BusinessCard;

/**
 * Created by YassIne on 14/11/2015.
 */
public class NearbyConn implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Connections.ConnectionRequestListener, Connections.MessageListener, Connections.EndpointDiscoveryListener {

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private static final long CONNECTION_TIME_OUT = 10000L;

    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_BLUETOOTH, ConnectivityManager.TYPE_ETHERNET};

    private boolean mIsHost;
    private boolean mIsConnected;


    public NearbyConn(Context context){
        mContext = context;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    public void publishMessage(BusinessCard bcard) throws IOException {
        byte[] businessCardByte = Serializer.serialize(bcard);
        Message cardToSend = new Message(businessCardByte);
        Nearby.Messages.publish(mGoogleApiClient, cardToSend).setResultCallback(new ErrorCheckingCallback());
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

    public Context getmContext() {
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
}
