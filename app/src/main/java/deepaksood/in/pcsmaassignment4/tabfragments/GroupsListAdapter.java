package deepaksood.in.pcsmaassignment4.tabfragments;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import deepaksood.in.pcsmaassignment4.MainActivity;
import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.addgrouppackage.GroupObject;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatUserObject;

/**
 * Created by deepak on 27/4/16.
 */
public class GroupsListAdapter extends BaseAdapter {

    private static final String TAG = GroupsListAdapter.class.getSimpleName();

    private final Activity context;
    private final List<GroupItemObject> groupObjects;

    public GroupsListAdapter(Activity context, List<GroupItemObject> groupObjects) {
        this.context = context;
        this.groupObjects = groupObjects;
    }

    @Override
    public int getCount() {
        return groupObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return groupObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(TAG,"GRoup List Adapter");
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.groups_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.tv_group_name);
        TextView txtPhone = (TextView) rowView.findViewById(R.id.tv_owner_phone);
        ImageView imageViewPhoto = (ImageView) rowView.findViewById(R.id.iv_group_pic);

        txtTitle.setText(groupObjects.get(position).getGroupName());
        txtPhone.setText(groupObjects.get(position).getOwnerName());

        if(groupObjects.get(position).getPhotoUrl() != null) {
            Picasso.with(context).load(groupObjects.get(position).getPhotoUrl()).into(imageViewPhoto);
        }

        return rowView;
    }
}
