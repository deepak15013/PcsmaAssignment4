package deepaksood.in.pcsmaassignment4.chatpackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.servicepackage.RabbitMqService;
import deepaksood.in.pcsmaassignment4.tabfragments.ContactsFragment;

public class ChatOneToOne extends AppCompatActivity {

    private static final String TAG = ChatOneToOne.class.getSimpleName();

    String userNumber;
    int position;
    EditText etSend;
    Button btnSend;
//    ScrollView scChat;

    Thread publishThread;
    private BlockingDeque queue = new LinkedBlockingDeque();
    private String profileNumber = "";

    ImageView toolbarContactPic;
    TextView toolbarDisplayName;
    TextView toolbarPhoneNumber;

    private ChatAdapter adapter;
    ListView messageContainer;

    ChatUserObject chatUserObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_one_to_one);

        Bundle bundle = getIntent().getExtras();
        profileNumber = bundle.getString("PROFILE_NUMBER");
        chatUserObject = (ChatUserObject) getIntent().getSerializableExtra("CHAT_USER_OBJECT");
        position = bundle.getInt("POSITION");
        Log.v(TAG,"Position clicked: "+position);
        Log.v(TAG,"profileNumber: "+profileNumber);
        userNumber = chatUserObject.getChatuserMobileNum();

        for(ChatMessage i : chatUserObject.getChatMessages()) {
            Log.v(TAG,"Ichat Message: "+i.getMessage());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarContactPic = (ImageView) findViewById(R.id.iv_toolbar_contact_pic);
        toolbarDisplayName = (TextView) findViewById(R.id.tv_toolbar_display_name);
        toolbarPhoneNumber = (TextView) findViewById(R.id.tv_toolbar_number);

        toolbarDisplayName.setText(chatUserObject.getChatUserDisplayName());
        toolbarPhoneNumber.setText(chatUserObject.getChatuserMobileNum());
        Picasso.with(getApplicationContext()).load(chatUserObject.getChatUserPhotoUrl()).into(toolbarContactPic);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//        chatContent = (TextView) findViewById(R.id.chat_content);
        etSend = (EditText) findViewById(R.id.et_send);
        btnSend = (Button) findViewById(R.id.btn_send);
//        scChat = (ScrollView) findViewById(R.id.sv_chat);

        messageContainer = (ListView) findViewById(R.id.message_container);

        loadChatHistory();

        setUpConnectionFactory();
        publishToAMQP();
        setUpPubButton();

        registerReceiver(broadcastReceiver, new IntentFilter(RabbitMqService.BROADCAST_ACTION));

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("MESSAGE");

            String sender="";
            String pureMessage="";
            boolean temp=true;
            for(String token : message.split(":")) {
                Log.v(TAG,"token: "+token);
                if(temp) {
                    sender = token;
                    temp = false;
                }
                else {
                    pureMessage = token;
                }
            }
            Log.v(TAG,"pure: "+pureMessage);

            printChat(pureMessage, false);

        }
    };


    private void loadChatHistory(){

        adapter = new ChatAdapter(ChatOneToOne.this, new ArrayList<ChatMessage>());
        messageContainer.setAdapter(adapter);

        for(int i=0; i<chatUserObject.getChatMessages().size(); i++) {
            ChatMessage message = chatUserObject.getChatMessages().get(i);
            displayMessage(message);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        ContactsFragment.chatUserObjects.set(position, chatUserObject);
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
            Log.v(TAG,"ConnectionClosed ChatsOneToOne");



        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.putExtra("CHAT_USER_OBJECT_BACK",chatUserObject);
        setResult(1, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.v(TAG,"OnPause chat one to one");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG,"OnDestroy chat one to one");
        super.onDestroy();
        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    ConnectionFactory factory = new ConnectionFactory();
    public void setUpConnectionFactory() {
        factory.setAutomaticRecoveryEnabled(false);
        factory.setHost("52.207.235.200");
        factory.setUsername("deepak");
        factory.setPassword("deepak");

    }

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

    void setUpPubButton() {

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(etSend.getText() != null && !etSend.getText().toString().equals("") && !etSend.getText().toString().equals(" ")) {
                    printChat(etSend.getText().toString(),true);
                    publishMessage(etSend.getText().toString());
                    etSend.setText("");
                }
            }
        });
    }

    public void printChat(String msg, boolean meOrNot) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(msg);
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(meOrNot);

        displayMessage(chatMessage);

        chatUserObject.getChatMessages().add(chatMessage);

        for(ChatMessage i : chatUserObject.getChatMessages()) {
            Log.v(TAG,"chatMessage: "+i.getMessage());
        }

    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messageContainer.setSelection(messageContainer.getCount() - 1);
    }

    void publishMessage(String message) {
        try {
            Log.d("", "[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
