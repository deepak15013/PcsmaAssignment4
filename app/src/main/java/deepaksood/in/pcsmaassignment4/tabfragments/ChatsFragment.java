package deepaksood.in.pcsmaassignment4.tabfragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import deepaksood.in.pcsmaassignment4.MainActivity;
import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatMessage;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatUserObject;
import deepaksood.in.pcsmaassignment4.servicepackage.RabbitMqService;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    public static final String TAG = ChatsFragment.class.getSimpleName();

    ListView chatList;


    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(RabbitMqService.BROADCAST_ACTION));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(TAG,"Creating view Chats Fragment");
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        chatList = (ListView) view.findViewById(R.id.chat_list);

        chatList.setAdapter(chatAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("MESSAGE");
            Log.v(TAG,"message got here: "+message);

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
            Log.v(TAG,"message: "+pureMessage);
            Log.v(TAG,"sender: "+sender);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(pureMessage);
            chatMessage.setMe(false);
            chatMessage.setDate("today");

            for(int k=0; k< ContactsFragment.chatUserObjects.size();k++) {
                if(ContactsFragment.chatUserObjects.get(k).getChatuserMobileNum().equals(sender)) {
                    Log.v(TAG,"found: "+ContactsFragment.chatUserObjects.get(k).getChatuserMobileNum());
                    ContactsFragment.chatUserObjects.get(k).chatMessages.add(chatMessage);
                    break;
                }
            }

            for(ChatUserObject i: ContactsFragment.chatUserObjects) {
                Log.v(TAG,"i: "+i.getChatUserDisplayName());
                for(ChatMessage j: i.getChatMessages()) {
                    Log.v(TAG,"j: "+j.getMessage());
                }
            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");
    }

    @Override
    public void onDestroy() {
        Log.v(TAG,"onDestroy Contacts Fragment");
        super.onDestroy();
        if(broadcastReceiver != null)
            getActivity().unregisterReceiver(broadcastReceiver);
    }

}
