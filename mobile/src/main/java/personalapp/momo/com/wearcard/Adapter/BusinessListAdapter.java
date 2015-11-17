package personalapp.momo.com.wearcard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import personalapp.momo.com.wearcard.CustomImageView;
import personalapp.momo.com.wearcard.Models.BusinessCard;
import personalapp.momo.com.wearcard.R;

/**
 * Created by YassIne on 14/11/2015.
 */
public class BusinessListAdapter extends ArrayAdapter<BusinessCard> {
    private Context mContext;
    private ArrayList<BusinessCard> mCardList = new ArrayList<>();

    public BusinessListAdapter(Context context, ArrayList<BusinessCard> cardList){
        super(context,0,cardList);
        mContext = context;
        mCardList = cardList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BusinessCard card = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_item, parent,false);
        }

        if (card != null) {
            TextView cardNome = (TextView) convertView.findViewById(R.id.card_tv_nome);
            TextView cardCognome = (TextView) convertView.findViewById(R.id.card_tv_cognome);
            TextView occupazione = (TextView) convertView.findViewById(R.id.tv_occupazione);
            CircleImageView thumbnail = (CircleImageView) convertView.findViewById(R.id.profile_image);

            if (cardNome != null) {
                cardNome.setText(card.getNome());
            }

            if (cardCognome != null) {
                cardCognome.setText(card.getCognome());
            }

            if (occupazione != null) {
                occupazione.setText(card.getmOccupazione());
            }

            if (thumbnail != null) {
                thumbnail.setImageBitmap(card.getThumbnail());
            }
        }
        return convertView;
    }


}
