package deepaksood.in.pcsmaassignment4.addgrouppackage;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.broadcastpackage.BroadcastAdapter;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatUserObject;
import deepaksood.in.pcsmaassignment4.tabfragments.ContactsFragment;

public class AddGroup extends AppCompatActivity {

    private static final String TAG = AddGroup.class.getSimpleName();

    ListView list;
    AddGroupAdapter adapter = null;

    Button addGroupButton;
    EditText groupName;

    private String profileNumber = "";
    private String userNumber;

    private int numOfSelectedUsers=0;

    public static List<ChatUserObject> chatUserObjects;

    List<String> broadcastList;
    private String groupNameText;

    GroupObject groupObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.broadcast_toolbar);
        toolbar.setTitle("Add Group");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileNumber = getIntent().getStringExtra("PROFILE_NUMBER");
        Log.v(TAG,"ProfileNumber: "+profileNumber);

        list = (ListView) findViewById(R.id.lv_group_list_container);
        addGroupButton = (Button) findViewById(R.id.btn_add_group);
        groupName = (EditText) findViewById(R.id.et_group_name);

        chatUserObjects = new ArrayList<>();
        chatUserObjects = ContactsFragment.chatUserObjects;

        for(ChatUserObject i: chatUserObjects) {
            Log.v(TAG,"i: "+i.getChatUserDisplayName());
        }

        adapter = new AddGroupAdapter(this,R.layout.broadcast_contacts_item, chatUserObjects);
        list.setAdapter(adapter);

        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ChatUserObject> selectedList = adapter.chatUserObjects;
                Log.v(TAG, "number of Selected users: " + selectedList.size());
                broadcastList = new ArrayList<>();
                for (int i = 0; i < selectedList.size(); i++) {
                    ChatUserObject object = selectedList.get(i);
                    if (object.isSelected()) {
                        broadcastList.add(object.getChatuserMobileNum());
                        Log.v(TAG, "added: " + object.getChatuserMobileNum());
                        numOfSelectedUsers++;
                    }
                }
                if (numOfSelectedUsers < 3) {
                    Toast.makeText(AddGroup.this, "Please add atleast 2 members to the group", Toast.LENGTH_SHORT).show();
                } else {
                    if (groupName.getText() != null && !groupName.getText().toString().equals("") && !groupName.getText().toString().equals(" ")) {
                        groupNameText = groupName.getText().toString();
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                        Toast.makeText(AddGroup.this, "Group Added", Toast.LENGTH_SHORT).show();
                        addGroupNameToDatabase();
                        onBackPressed();
                    } else {
                        Toast.makeText(AddGroup.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void addGroupNameToDatabase() {
        groupObject = new GroupObject();
        groupObject.setGroupName(groupNameText);

        Set<String> contactList = new HashSet<>(broadcastList);
        groupObject.setMobileNumArr(contactList);

        groupObject.setNumOfMembers(numOfSelectedUsers);

        groupObject.setGroupPhotoUrl("https://drive.google.com/uc?id=0B1jHFoEHN0zfWXFuNzFUdTNYVHc");
        groupObject.setGroupCoverUrl("https://drive.google.com/uc?id=0B1jHFoEHN0zfdDUwemhnenZlanc");

        groupObject.setGroupOwner("1234567890");

        new db().execute();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class db extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            CognitoCachingCredentialsProvider credentialsProvider;

            credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-1:9420ebde-0680-48b5-a18f-886d70725554", // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);

            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            if(mapper != null) {
                mapper.save(groupObject);
                Log.v(TAG,"groupName: "+groupObject.getGroupName());
                Log.v(TAG,"owner: "+groupObject.getGroupOwner());
                Log.v(TAG,"numOfMembers: "+groupObject.getNumOfMembers());
                Log.v(TAG,"photo: "+groupObject.getGroupPhotoUrl());
                Log.v(TAG,"cover: "+groupObject.getGroupCoverUrl());
                for(String i: groupObject.getMobileNumArr()) {
                    Log.v(TAG,"member: "+i);
                }
            }

            else
                Log.v(TAG,"not saved");

            return "Executed";
        }
    }

}
