package personalapp.momo.com.wearcard;

import android.app.Application;

import personalapp.momo.com.wearcard.Models.BusinessCard;

/**
 * Created by YassIne on 15/11/2015.
 */
public class MyApp extends Application {

    BusinessCard mFoundBusinessCard;

    public BusinessCard getmFoundBusinessCard() {
        return mFoundBusinessCard;
    }

    public void setmFoundBusinessCard(BusinessCard mFoundBusinessCard) {
        this.mFoundBusinessCard = mFoundBusinessCard;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFoundBusinessCard = null;
    }
}
