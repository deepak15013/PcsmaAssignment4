package deepaksood.in.pcsmaassignment4.addgrouppackage;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatUserObject;

/**
 * Created by deepak on 19/4/16.
 */
public class AddGroupAdapter extends ArrayAdapter<ChatUserObject> {
    private static final String TAG = AddGroupAdapter.class.getSimpleName();

    private final Activity context;
    public final List<ChatUserObject> chatUserObjects;

    public AddGroupAdapter(Activity context, int textViewResourceId, List<ChatUserObject> chatUserObjects) {
        super(context, textViewResourceId, chatUserObjects);
        this.context = context;
        this.chatUserObjects = chatUserObjects;
    }


    private class ViewHolder {
        CheckBox name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = context.getLayoutInflater();
            convertView = vi.inflate(R.layout.broadcast_contacts_item, null);

            TextView txtTitle = (TextView) convertView.findViewById(R.id.contacts_name);
            TextView txtPhone = (TextView) convertView.findViewById(R.id.tv_contacts_phone);
            ImageView imageViewPhoto = (ImageView) convertView.findViewById(R.id.iv_contact_pic);

            txtTitle.setText(chatUserObjects.get(position).getChatUserDisplayName());
            txtPhone.setText(chatUserObjects.get(position).getChatuserMobileNum());
            if(chatUserObjects.get(position).getChatUserPhotoUrl() != null) {
                Picasso.with(getContext()).load(chatUserObjects.get(position).getChatUserPhotoUrl()).into(imageViewPhoto);
            }

            holder = new ViewHolder();
            holder.name = (CheckBox) convertView.findViewById(R.id.cb_broadcast);
            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    ChatUserObject country = (ChatUserObject) cb.getTag();
                    country.setSelected(cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatUserObject country = chatUserObjects.get(position);
        holder.name.setChecked(country.isSelected());
        holder.name.setTag(country);

        return convertView;

    }
}
