package deepaksood.in.pcsmaassignment4.broadcastpackage;

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

import java.util.ArrayList;
import java.util.List;

import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatUserObject;
import deepaksood.in.pcsmaassignment4.tabfragments.ContactsFragment;
import deepaksood.in.pcsmaassignment4.tabfragments.ContactsListAdapter;

public class BroadcastActivity extends AppCompatActivity {

    private static final String TAG = BroadcastActivity.class.getSimpleName();

    ListView list;
    BroadcastAdapter adapter = null;

    Button broadcastButton;
    EditText broadcastMessage;

    public static List<ChatUserObject> chatUserObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        Toolbar toolbar = (Toolbar) findViewById(R.id.broadcast_toolbar);
        toolbar.setTitle("Broadcast Message");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = (ListView) findViewById(R.id.lv_select_contacts_container);
        broadcastButton = (Button) findViewById(R.id.btn_broadcast_send);
        broadcastMessage = (EditText) findViewById(R.id.et_broadcast_message);

        chatUserObjects = new ArrayList<>();
        chatUserObjects = ContactsFragment.chatUserObjects;

        for(ChatUserObject i: chatUserObjects) {
            Log.v(TAG,"i: "+i.getChatUserDisplayName());
        }

        adapter = new BroadcastAdapter(this,R.layout.broadcast_contacts_item, chatUserObjects);
        list.setAdapter(adapter);

        broadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ChatUserObject> selectedList = adapter.chatUserObjects;
                List<String> broadcastList = new ArrayList<>();
                for(int i=0;i<selectedList.size();i++){
                    ChatUserObject object = selectedList.get(i);
                    if(object.isSelected()){
                        broadcastList.add(object.getChatuserMobileNum());
                    }
                }

                publishToBroadcastList(broadcastList);

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

    public void publishToBroadcastList(List<String> broadcastList) {
        if(broadcastMessage.getText() != null && !broadcastMessage.getText().toString().equals("") && !broadcastMessage.getText().toString().equals(" ")) {
            for(String number: broadcastList) {
                publishBroadcastMessage(number, broadcastMessage.getText().toString());
            }
            broadcastMessage.setText("");
        }
        else {
            Toast.makeText(BroadcastActivity.this, "Please enter broadcast message", Toast.LENGTH_SHORT).show();
        }
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

    public void publishBroadcastMessage(String publish_queue, String message) {
        Log.v(TAG,"Number: "+publish_queue+" Message: "+message);

    }
}
