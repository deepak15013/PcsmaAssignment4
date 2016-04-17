package deepaksood.in.pcsmaassignment4.ChatPackage;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import deepaksood.in.pcsmaassignment4.MainActivity;
import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.tabfragments.ContactsFragment;

public class ChatOneToOne extends AppCompatActivity {

    private static final String TAG = ChatOneToOne.class.getSimpleName();

    String userNumber;

    TextView chatContent;
    EditText etSend;
    Button btnSend;
//    ScrollView scChat;

    Thread subscribeThread;
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
//        userNumber = bundle.getString("USER_NUMBER");
        profileNumber = bundle.getString("PROFILE_NUMBER");
        chatUserObject = (ChatUserObject) getIntent().getSerializableExtra("CHAT_USER_OBJECT");
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

        loadDummyHistory();

        setUpConnectionFactory();
        publishToAMQP();
        setUpPubButton();

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = msg.getData().getString("msg");
                Date now = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
                Log.v(TAG,"messageReceived-----------: "+message);
                //chatContent.append(ft.format(now) + ' ' + message + '\n');
//                scChat.fullScroll(View.FOCUS_DOWN);
            }
        };
        subscribe(incomingMessageHandler);

    }

    private void loadDummyHistory(){

        /*ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        ChatMessage msg1 = new ChatMessage();
        msg1.setId(2);
        msg1.setMe(false);
        msg1.setMessage("How r u doing???");
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);*/

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
    }

    ConnectionFactory factory = new ConnectionFactory();
    public void setUpConnectionFactory() {
        factory.setAutomaticRecoveryEnabled(false);

        /*try {
            factory.setUri("amqp://pmkrlkkw:GB1jKxGoJX8ya_vywroGbvsdP3SQqFhI@fox.rmq.cloudamqp.com/pmkrlkkw");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }*/
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
                            String message = "from:"+profileNumber +" "+ queue.takeFirst().toString()+" " + "to:"+userNumber;
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
                    printChat();
//                    chatContent.append(profileNumber+": "+etSend.getText().toString()+"\n");
//                    scChat.fullScroll(View.FOCUS_DOWN);
                    publishMessage(etSend.getText().toString());
                    etSend.setText("");
                }
            }
        });
    }

    public void printChat() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(etSend.getText().toString());
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(true);

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

    Channel subChannel;
    Connection subConnection;
    void subscribe(final Handler handler)
    {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        if(subConnection == null)
                            subConnection = factory.newConnection();
                        subChannel = subConnection.createChannel();
                        subChannel.basicQos(1);
                        Log.v(TAG,"userNumber inside subsribe: "+profileNumber);
                        AMQP.Queue.DeclareOk q = subChannel.queueDeclare(profileNumber,true,true,false,null);
                        subChannel.queueBind(q.getQueue(), "amq.fanout", "chat");
                        QueueingConsumer consumer = new QueueingConsumer(subChannel);
                        subChannel.basicConsume(q.getQueue(), true, consumer);

                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            String message = new String(delivery.getBody());
                            Log.d("","[r] " + message);
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", message);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e1) {
                        Log.d("", "Connection broken sub: " + e1.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.start();
    }

}
