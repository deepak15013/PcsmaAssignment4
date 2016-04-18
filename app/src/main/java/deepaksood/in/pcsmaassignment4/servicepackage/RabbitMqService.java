package deepaksood.in.pcsmaassignment4.servicepackage;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import deepaksood.in.pcsmaassignment4.MainActivity;

public class RabbitMqService extends Service {

    private static final String TAG = RabbitMqService.class.getSimpleName();
    public static final String BROADCAST_ACTION = "deepaksood.in.pcsmaassignment.getmessage";

    Thread subscribeThread;

    private static String USER_QUEUE="";

    Intent intent;

    public RabbitMqService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        USER_QUEUE = intent.getStringExtra("USER_QUEUE");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG,"OnCreate");

        intent = new Intent(BROADCAST_ACTION);

        setUpConnectionFactory();
        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = msg.getData().getString("msg");
                Log.v(TAG,"Mesage: "+message+"--------------------------");
                intent.putExtra("MESSAGE",message);
                sendBroadcast(intent);
            }
        };

        subscribe(incomingMessageHandler);

    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG,"OnBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.v(TAG,"onDestroy");
        try {
            subChannel.close();
            subConnection.close();
            Log.v(TAG,"ConnectionClosed Service");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    ConnectionFactory connectionFactory = new ConnectionFactory();
    public void setUpConnectionFactory() {
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(10000);
        /*try {
            connectionFactory.setUri("amqp://pmkrlkkw:GB1jKxGoJX8ya_vywroGbvsdP3SQqFhI@fox.rmq.cloudamqp.com/pmkrlkkw");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }*/
        connectionFactory.setHost("52.207.235.200");
        connectionFactory.setUsername("deepak");
        connectionFactory.setPassword("deepak");
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
                        Log.v(TAG,"Creating Connection Service");
                        if(subConnection == null) {
                            subConnection = connectionFactory.newConnection();
                        }
                        subChannel = subConnection.createChannel();
                        subChannel.basicQos(1);
                        Log.v(TAG,"USER_QUEUE: "+USER_QUEUE);
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
