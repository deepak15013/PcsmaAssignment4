package deepaksood.in.pcsmaassignment4.addgrouppackage;

import android.content.Context;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
                List<String> broadcastList = new ArrayList<>();
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
                        for (String number : broadcastList) {
                            userNumber = number;

                        }
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                        Toast.makeText(AddGroup.this, "Group Added", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(AddGroup.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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

}
