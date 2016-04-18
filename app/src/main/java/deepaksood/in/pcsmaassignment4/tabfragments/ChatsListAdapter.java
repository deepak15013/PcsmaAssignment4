package deepaksood.in.pcsmaassignment4.tabfragments;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatUserObject;

/**
 * Created by deepak on 18/4/16.
 */
public class ChatsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<ChatUserObject> chatUserObjects;

    public ChatsListAdapter(Activity context, List<ChatUserObject> chatUserObjects) {
        this.context = context;
        this.chatUserObjects = chatUserObjects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.chat_list_item, null, true);
        TextView chatName = (TextView) rowView.findViewById(R.id.chat_contact_name);
        TextView lastChat = (TextView) rowView.findViewById(R.id.tv_last_chat);
        ImageView imageViewPhoto = (ImageView) rowView.findViewById(R.id.iv_chat_contact_pic);

        chatName.setText(chatUserObjects.get(position).getChatUserDisplayName());
        lastChat.setText(chatUserObjects.get(position).getChatuserMobileNum());

        if(chatUserObjects.get(position).getChatUserPhotoUrl() != null) {
            Picasso.with(getContext()).load(chatUserObjects.get(position).getChatUserPhotoUrl()).into(imageViewPhoto);
        }

        return rowView;
    }
}
