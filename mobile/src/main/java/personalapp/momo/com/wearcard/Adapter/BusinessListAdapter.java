package personalapp.momo.com.wearcard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import personalapp.momo.com.wearcard.Models.BusinessCard;
import personalapp.momo.com.wearcard.R;

/**
 * Created by YassIne on 14/11/2015.
 */
public class BusinessListAdapter extends ArrayAdapter<BusinessCard> {
    private Context mContext;
    private ArrayList<BusinessCard> mCardList;

    public BusinessListAdapter(Context context, ArrayList<BusinessCard> cardList){
        super(context,0,cardList);
        mContext = context;
        mCardList = cardList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            //v = vi.inflate(R.layout.itemlistrow, null);
        }

        BusinessCard card = getItem(position);

        if (card != null) {
            /*
            TextView tt1 = (TextView) v.findViewById(R.id.id);
            TextView tt2 = (TextView) v.findViewById(R.id.categoryId);
            TextView tt3 = (TextView) v.findViewById(R.id.description);

            if (tt1 != null) {
                tt1.setText(card.getId());
            }

            if (tt2 != null) {
                tt2.setText(card.getCategory().getId());
            }

            if (tt3 != null) {
                tt3.setText(card.getDescription());
            }
            */
        }
        return v;
    }


}
