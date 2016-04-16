package deepaksood.in.pcsmaassignment4.tabfragments;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import deepaksood.in.pcsmaassignment4.R;

/**
 * Created by deepak on 5/4/16.
 */
public class ContactsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] name;
    private final String[] number;
    private final String[] photo;

    public ContactsListAdapter(Activity context, String[] name, String[] number, String[] photo) {
        super(context, R.layout.contacts_item, name);
        this.context = context;
        this.name = name;
        this.number = number;
        this.photo = photo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.contacts_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.contacts_name);
        TextView txtPhone = (TextView) rowView.findViewById(R.id.contacts_phone);
        ImageView imageViewPhoto = (ImageView) rowView.findViewById(R.id.iv_contact_pic);

        txtTitle.setText(name[position]);
        txtPhone.setText(number[position]);

        if(photo[position] != null) {
            Picasso.with(getContext()).load(photo[position]).into(imageViewPhoto);
        }

        return rowView;
    }
}
