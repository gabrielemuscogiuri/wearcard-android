package personalapp.momo.com.wearcard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
            v = vi.inflate(R.layout.card_item, null);
        }

        BusinessCard card = getItem(position);

        if (card != null) {
            TextView cardNome = (TextView) v.findViewById(R.id.card_tv_nome);
            TextView cardCognome = (TextView) v.findViewById(R.id.card_tv_cognome);
            ImageView thumbnail = (ImageView) v.findViewById(R.id.card_tv_thumbnail);

            if (cardNome != null) {
                cardNome.setText(card.getNome());
            }

            if (cardCognome != null) {
                cardCognome.setText(card.getCognome());
            }

            if (thumbnail != null) {
                thumbnail.setImageBitmap(card.getThumbnail());
            }
        }
        return v;
    }


}
