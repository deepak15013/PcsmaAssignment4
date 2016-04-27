package deepaksood.in.pcsmaassignment4.tabfragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class GroupsFragment extends Fragment {

    private static final String TAG = GroupsFragment.class.getSimpleName();

    ListView list;
    GroupsListAdapter groupsListAdapter;

    CognitoCachingCredentialsProvider credentialsProvider;
    PaginatedScanList<GroupObject> result;

    public List<GroupItemObject> groupItemObjects;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        groupItemObjects = new ArrayList<>();
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

            if(mapper != null) {
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                result = mapper.scan(GroupObject.class, scanExpression);
                for(GroupObject i: result) {
                    GroupItemObject groupItemObject = new GroupItemObject(i.getGroupName(),i.getGroupOwner(),i.getGroupPhotoUrl());
                    groupItemObjects.add(groupItemObject);
                    Log.v(TAG,"name: "+groupItemObject.getGroupName());
                }
            }

            else
                Log.v(TAG,"not saved");

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