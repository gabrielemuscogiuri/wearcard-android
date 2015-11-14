package personalapp.momo.com.wearcard;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.connection.Connections;

import java.sql.Connection;

/**
 * Created by YassIne on 14/11/2015.
 */
public class NearbyConn implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Connections.ConnectionRequestListener, Connections.MessageListener, Connections.EndpointDiscoveryListener {
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;


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
}
