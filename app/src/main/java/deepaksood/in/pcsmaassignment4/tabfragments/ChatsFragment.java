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
import android.widget.AdapterView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import deepaksood.in.pcsmaassignment4.MainActivity;
import deepaksood.in.pcsmaassignment4.R;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatMessage;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatOneToOne;
import deepaksood.in.pcsmaassignment4.chatpackage.ChatUserObject;
import deepaksood.in.pcsmaassignment4.servicepackage.RabbitMqService;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    public static final String TAG = ChatsFragment.class.getSimpleName();

    ListView chatList;

    ChatsListAdapter chatAdapter;

    private String profileNumber = "";
    private int lastObjectPosition;
    boolean alreadyPresent = false;

    public static List<ChatUserObject> chatUserObjects;
//    List<String> contactsName;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG,"onCreate");
        super.onCreate(savedInstanceState);

//        contactsName = new ArrayList<>();
        chatUserObjects = new ArrayList<>();

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(RabbitMqService.BROADCAST_ACTION));

//        String[] contactsArrayName = new String[contactsName.size()];
//        contactsArrayName = contactsName.toArray(contactsArrayName);

        chatAdapter = new ChatsListAdapter(getActivity(), chatUserObjects);

        MainActivity mainActivity = (MainActivity) getActivity();
        profileNumber = mainActivity.getMobileNumText();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(TAG,"Creating view Chats Fragment");
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        chatList = (ListView) view.findViewById(R.id.chat_list);

        chatList.setAdapter(chatAdapter);

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastObjectPosition = position;
                Log.v(TAG,"itemClicked: "+position);
                Intent intent = new Intent(getActivity(),ChatOneToOne.class);
                intent.putExtra("CHAT_USER_OBJECT",chatUserObjects.get(position));
//                intent.putExtra("USER_NUMBER",contactsList.get(position));
                intent.putExtra("PROFILE_NUMBER",profileNumber);
                intent.putExtra("POSITION",position);
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {
            Bundle bundle = data.getExtras();
            if(bundle != null) {
                ChatUserObject chatUserObject = (ChatUserObject) bundle.getSerializable("CHAT_USER_OBJECT_BACK");
                if(chatUserObject != null) {
                    Log.v(TAG,"chatUserObject: "+chatUserObject);
                    Log.v(TAG,"onj: "+chatUserObject.getChatUserDisplayName());
                    ContactsFragment.chatUserObjects.set(lastObjectPosition, chatUserObject);
                }
                else {
                    Log.v(TAG,"nullChatUserObject");
                }

            }
            //Log.v(TAG,"chatUserObject: "+chatUserObject.getChatMessages().get(0));
        }
        else{
            Log.v(TAG,"onActivityResult cancelled");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("MESSAGE");

            String sender="";
            String pureMessage="";
            boolean temp=true;
            for(String token : message.split(":")) {
                if(temp) {
                    sender = token;
                    temp = false;
                }
                else {
                    pureMessage = token;
                }
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(pureMessage);
            chatMessage.setMe(false);
            chatMessage.setDate("today");

            for(int k=0; k< ContactsFragment.chatUserObjects.size();k++) {
                if(ContactsFragment.chatUserObjects.get(k).getChatuserMobileNum().equals(sender)) {
                    Log.v(TAG,"found: "+ContactsFragment.chatUserObjects.get(k).getChatuserMobileNum());
                    ContactsFragment.chatUserObjects.get(k).chatMessages.add(chatMessage);
                    Log.v(TAG,"size: "+chatUserObjects.size());

                    for(int l=0;l<chatUserObjects.size();l++) {
                        if(ContactsFragment.chatUserObjects.get(k).equals(chatUserObjects.get(l))) {
                            alreadyPresent = true;
                        }
                    }

                    if(!alreadyPresent) {
                        chatUserObjects.add(ContactsFragment.chatUserObjects.get(k));
                        chatAdapter.add(message);
                        chatAdapter.notifyDataSetChanged();
                    }
                    else {
                        chatAdapter.notifyDataSetChanged();
                    }

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
