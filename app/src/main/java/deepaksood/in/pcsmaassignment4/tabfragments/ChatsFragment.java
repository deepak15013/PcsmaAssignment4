package deepaksood.in.pcsmaassignment4.tabfragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    public static final String TAG = ChatsFragment.class.getSimpleName();

    Thread subscribeThread;
    Thread publishThread;
    private BlockingDeque queue = new LinkedBlockingDeque();

    private static String USER_QUEUE="";

    TextView chatFragmentContent;
    ScrollView chatFragment;


    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.v(TAG,"onCreate Chats Fragment");

        MainActivity mainActivity = (MainActivity) getActivity();
        USER_QUEUE = mainActivity.getMobileNumText();
        Log.v(TAG,"USER_QUEUE: "+USER_QUEUE);

        setUpConnectionFactory();

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = msg.getData().getString("msg");
                Date now = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
                chatFragmentContent.append(ft.format(now) + ' ' + message + '\n');
                chatFragment.fullScroll(View.FOCUS_DOWN);
            }
        };

        subscribe(incomingMessageHandler);

    }

    @Override
    public void onDestroy() {
        try {
            subChannel.close();
            subConnection.close();
            Log.v(TAG,"ConnectionClosed ChatsFragment");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        super.onDestroy();
        Log.v(TAG,"onDestroy chats fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(TAG,"Creating view Chats Fragment");
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        chatFragmentContent = (TextView) view.findViewById(R.id.chat_fragment_content);
        chatFragment = (ScrollView) view.findViewById(R.id.sv_chat_fragment);

        return view;
    }

    ConnectionFactory connectionFactory = new ConnectionFactory();
    public void setUpConnectionFactory() {
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(10000);
        try {
            connectionFactory.setUri("amqp://pmkrlkkw:GB1jKxGoJX8ya_vywroGbvsdP3SQqFhI@fox.rmq.cloudamqp.com/pmkrlkkw");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    Connection subConnection;
    Channel subChannel;
    void subscribe(final Handler handler)
    {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Log.v(TAG,"Creating Connection ChatsFragment");
                        if(subConnection == null) {
                            subConnection = connectionFactory.newConnection();
                        }
                        subChannel = subConnection.createChannel();
                        subChannel.basicQos(1);
                        AMQP.Queue.DeclareOk q = subChannel.queueDeclare(USER_QUEUE,true,true,false,null);
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
