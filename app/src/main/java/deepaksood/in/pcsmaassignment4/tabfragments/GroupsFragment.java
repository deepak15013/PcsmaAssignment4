package deepaksood.in.pcsmaassignment4.tabfragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.List;

import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.UserObject;
import deepaksood.in.pcsmaassignment4.addgrouppackage.GroupObject;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatUserObject;
import deepaksood.in.pcsmaassignment4.groupchat.GroupChatActivity;


public class GroupsFragment extends Fragment {

    private static final String TAG = GroupsFragment.class.getSimpleName();

    ListView list;
    GroupsListAdapter groupsListAdapter;

    CognitoCachingCredentialsProvider credentialsProvider;
    PaginatedScanList<GroupObject> result;

    public static List<GroupItemObject> groupItemObjects;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        groupItemObjects = new ArrayList<>();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");
        groupItemObjects.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");
        new db().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        groupsListAdapter = new GroupsListAdapter(getActivity(), groupItemObjects);

        list = (ListView) view.findViewById(R.id.groups_list_container);
        list.setAdapter(groupsListAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GroupChatActivity.class);
                intent.putExtra("GROUP_NAME",groupItemObjects.get(position).getGroupName());
                intent.putExtra("OWNER_NAME",groupItemObjects.get(position).getOwnerName());
                intent.putExtra("PHOTO_URL",groupItemObjects.get(position).getPhotoUrl());
                startActivity(intent);
            }
        });

        return view;
    }

    private class db extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            credentialsProvider = new CognitoCachingCredentialsProvider(
                    getContext(),
                    "us-east-1:9420ebde-0680-48b5-a18f-886d70725554", // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);

            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            try{
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                result = mapper.scan(GroupObject.class, scanExpression);
                for(GroupObject i: result) {
                    GroupItemObject groupItemObject = new GroupItemObject(i.getGroupName(),i.getGroupOwner(),i.getGroupPhotoUrl());
                    groupItemObjects.add(groupItemObject);
                    Log.v(TAG,"name: "+groupItemObject.getGroupName());
                }
            } catch (Exception e) {
                Log.v(TAG,"Exception e: "+e);
            }


            Log.v(TAG,"size: "+groupItemObjects.size());
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v(TAG,"onPostExecute");

            groupsListAdapter.notifyDataSetChanged();

            super.onPostExecute(s);

        }
    }

}