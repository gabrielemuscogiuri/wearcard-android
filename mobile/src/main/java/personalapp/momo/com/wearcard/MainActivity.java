package personalapp.momo.com.wearcard;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import personalapp.momo.com.wearcard.Adapter.BusinessListAdapter;
import personalapp.momo.com.wearcard.Models.BusinessCard;

public class MainActivity extends Activity {

    BusinessListAdapter mBCardListAdapter;
    ArrayList<BusinessCard> mBCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBCardList = new ArrayList<>();
        //--------------------------------
        // Business Card list adapter
        //--------------------------------
        mBCardListAdapter = new BusinessListAdapter(this,mBCardList);


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
}
