package personalapp.momo.com.wearcard;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class MyDisplayActivity extends Activity{
        private GoogleApiClient mApiClient;

        private ArrayAdapter<String> mAdapter;

        private ListView mListView;
        private EditText mEditText;
        private Button mSendButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display);

            init();
            initGoogleApiClient();
        }

        private void initGoogleApiClient() {
            mApiClient = new GoogleApiClient.Builder( this )
                    .addApi( Wearable.API )
                    .build();

            mApiClient.connect();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            mApiClient.disconnect();
        }

        private void init() {

            mSendButton = (Button) findViewById( R.id.btn_send);

            mSendButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        sendMessage( "Vai con dio" );
                    }
            });
        }

        private void sendMessage( final String text ) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();
                    for (Node node : nodes.getNodes()) {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                mApiClient, node.getId(), text, null).await();
                    }
                }
            });

        }
}
