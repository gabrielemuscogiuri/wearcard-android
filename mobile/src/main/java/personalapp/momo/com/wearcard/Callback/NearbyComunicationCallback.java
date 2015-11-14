package personalapp.momo.com.wearcard.Callback;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;

/**
 * Created by YassIne on 14/11/2015.
 */
public class NearbyComunicationCallback implements ResultCallback<Status> {
    private static final String TAG = NearbyComunicationCallback.class.getName();
    private final String method;
    private final Runnable runOnSuccess;
    private boolean mResolving;

    public NearbyComunicationCallback(String method){
        this(method, null);
    }

    public NearbyComunicationCallback(String method, Runnable run){
        this.method = method;
        this.runOnSuccess = run;

    }

    @Override
    public void onResult(Status status) {
        if(status.isSuccess()){
            if(runOnSuccess != null){
                runOnSuccess.run();
            }
        }else{
            if(status.hasResolution()){
                handleUnsuccessfullResult(status);
            }
        }

    }

    private void handleUnsuccessfullResult(Status status){
        if (status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN){

        }

    }
}
