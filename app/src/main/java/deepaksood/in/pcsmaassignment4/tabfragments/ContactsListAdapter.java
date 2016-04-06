package deepaksood.in.pcsmaassignment4.tabfragments;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import deepaksood.in.pcsmaassignment4.R;

/**
 * Created by deepak on 5/4/16.
 */
public class ContactsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] web;
    private final String[] imageId;

    public ContactsListAdapter(Activity context, String[] web, String[] imageId) {
        super(context, R.layout.contacts_item, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.contacts_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.contacts_name);
        TextView txtPhone = (TextView) rowView.findViewById(R.id.contacts_phone);

        txtTitle.setText(web[position]);
        txtPhone.setText(imageId[position]);

        return rowView;
    }
}
