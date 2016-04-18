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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import deepaksood.in.pcsmaassignment4.MainActivity;
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

    private String profileNumber = "";
    private String userNumber;

    public static List<ChatUserObject> chatUserObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        Toolbar toolbar = (Toolbar) findViewById(R.id.broadcast_toolbar);
        toolbar.setTitle("Broadcast Message");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileNumber = getIntent().getStringExtra("PROFILE_NUMBER");
        Log.v(TAG,"ProfileNumber: "+profileNumber);

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

        setUpConnectionFactory();

    }

    public void publishToBroadcastList(List<String> broadcastList) {
        if(broadcastMessage.getText() != null && !broadcastMessage.getText().toString().equals("") && !broadcastMessage.getText().toString().equals(" ")) {
            for(String number: broadcastList) {
                userNumber = number;
                publishBroadcastMessage(number, broadcastMessage.getText().toString());
            }
            broadcastMessage.setText("");
            Toast.makeText(BroadcastActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
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
        try {
            if(publishThread.isAlive()) {
                Log.v(TAG,"thread running");
            }
            if(pubChannel != null) {
                if(pubChannel.isOpen())
                    pubChannel.close();
            }
            if(pubConnection != null) {
                if(pubConnection.isOpen())
                    pubConnection.close();
            }
            publishThread.interrupt();
            if(!publishThread.isAlive()) {
                Log.v(TAG,"thread stopped");
            }
            else {
                Log.v(TAG,"not stopped");
            }
            Log.v(TAG,"ConnectionClosed BroadcastActivity");



        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        finish();
    }

    public void publishBroadcastMessage(String publish_queue, String message) {
        Log.v(TAG,"Number: "+publish_queue+" Message: "+message);
        publishToAMQP();
        publishMessage(message);
    }

    ConnectionFactory factory = new ConnectionFactory();
    public void setUpConnectionFactory() {
        factory.setAutomaticRecoveryEnabled(false);
        factory.setHost("52.207.235.200");
        factory.setUsername("deepak");
        factory.setPassword("deepak");

    }

    void publishMessage(String message) {
        try {
            Log.d("", "[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private BlockingDeque queue = new LinkedBlockingDeque();
    Thread publishThread;
    Connection pubConnection;
    Channel pubChannel;
    public void publishToAMQP()
    {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Log.v(TAG,"ConnectionCreated ChatOOneToOne");
                        if(pubConnection == null)
                            pubConnection = factory.newConnection();
                        pubChannel = pubConnection.createChannel();
                        pubChannel.confirmSelect();

                        while (true) {
                            String message = profileNumber +":"+ queue.takeFirst().toString();
                            Log.v(TAG,"Message: "+message);
                            try{
                                Log.v(TAG,"publish UserNumber: "+userNumber);
                                //pubChannel.queueDeclare(userNumber, true, true, false, null);
//                                pubChannel.basicPublish("amq.fanout", userNumber, null, message.getBytes()); //for all queues
//                                Log.d("", "[s] " + message);
                                pubChannel.basicPublish("", userNumber, null, message.getBytes());
                                pubChannel.waitForConfirmsOrDie();
                            } catch (Exception e){
                                Log.d("","[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.d("", "Connection broken pub: " + e.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
