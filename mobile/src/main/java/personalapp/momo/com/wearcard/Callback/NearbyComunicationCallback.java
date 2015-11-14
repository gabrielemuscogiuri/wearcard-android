package personalapp.momo.com.wearcard.Callback;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by YassIne on 14/11/2015.
 */
public class NearbyComunicationCallback implements ResultCallback<Status> {
    private static final String TAG = NearbyComunicationCallback.class.getName();
    private final String method;
    private final Runnable ruOnSuccess;

    public NearbyComunicationCallback(String method){
        this(method, null);
    }

    public NearbyComunicationCallback(String method, Runnable run){
        this.method = method;
        this.ruOnSuccess = run;

    }

    @Override
    public void onResult(Status status) {
        if(status.isSuccess()){
            Log.i(TAG, method + " success.");
            if()
        }

    }
}
