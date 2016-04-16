package deepaksood.in.pcsmaassignment4.tabfragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.List;

import deepaksood.in.pcsmaassignment4.ChatPackage.ChatOneToOne;
import deepaksood.in.pcsmaassignment4.MainActivity;
import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.UserObject;

public class ContactsFragment extends Fragment {

    private static final String TAG = ContactsFragment.class.getSimpleName();

    ListView list;
    ContactsListAdapter adapter;

    CognitoCachingCredentialsProvider credentialsProvider;
    PaginatedScanList<UserObject> result;

    List<String> contactsList;
    List<String> contactsName;
    List<String> contactsPhotos;

    private String profileNumber = "";

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contactsList = new ArrayList<>();
        contactsName = new ArrayList<>();
        contactsPhotos = new ArrayList<>();

        MainActivity mainActivity = (MainActivity) getActivity();
        profileNumber = mainActivity.getMobileNumText();

//        adapter = new ContactsListAdapter(getActivity(), web , mobileArray);

        new db().execute();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(TAG,"Creating view");
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        list = (ListView) view.findViewById(R.id.contacts_list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getActivity(), "pos: "+contactsList.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),ChatOneToOne.class);
                intent.putExtra("USER_NUMBER",contactsList.get(position));
                intent.putExtra("PROFILE_NUMBER",profileNumber);
                startActivity(intent);
            }
        });

        list.setAdapter(adapter);

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
                result = mapper.scan(UserObject.class, scanExpression);
                for(UserObject i: result) {
                    Log.v(TAG,"Mobile Num Retrived: "+i.getMobileNum());
                    contactsList.add(i.getMobileNum());
                    contactsName.add(i.getDisplayName());
                    contactsPhotos.add(i.getPhotoUrl());
                }
            }

            else
                Log.v(TAG,"not saved");

            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            /*for(String i : contactsList) {
                adapter.add(i);
            }*/
            Log.v(TAG,"onPostExecute");
            String[] contactsArrayNumber = new String[contactsList.size()];
            contactsArrayNumber = contactsList.toArray(contactsArrayNumber);

            String[] contactsArrayName = new String[contactsName.size()];
            contactsArrayName = contactsName.toArray(contactsArrayName);

            String[] contactsArrayPhoto = new String[contactsPhotos.size()];
            contactsArrayPhoto = contactsPhotos.toArray(contactsArrayPhoto);

            adapter = new ContactsListAdapter(getActivity(), contactsArrayName , contactsArrayNumber, contactsArrayPhoto);
            list.setAdapter(adapter);
            super.onPostExecute(s);

        }
    }

}
