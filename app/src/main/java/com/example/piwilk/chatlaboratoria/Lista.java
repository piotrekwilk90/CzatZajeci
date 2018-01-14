package com.example.piwilk.chatlaboratoria;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventObject;

public class Lista extends AppCompatActivity {

    String nick;
    String ip;
    Toolbar nickTextView;
    EventObject mqttMessage;

    ListView ListaChart;
    EditText MSG;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListaChart = (ListView) findViewById(R.id.ListaLista);
        MSG = (EditText) findViewById(R.id.Message);
        new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT();
            }
        }).start();




        nickTextView = (Toolbar) findViewById(R.id.toolbar);

        nick = getIntent().getStringExtra(MainActivity.NICK);
        ip = getIntent().getStringExtra(MainActivity.IP);


        setSupportActionBar(nickTextView);
        getSupportActionBar().setTitle(getIntent().getStringExtra(MainActivity.NICK));


        //w metodzie onCreate obslugujemy dodwanie wiadomosci do listy
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        ListaChart.setAdapter(adapter);







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MqttMessage message = new MqttMessage(MSG.getText().toString().getBytes());
                message.setQos(0);
                try{
                    sampleClient.publish(nick, message);
                }catch (MqttException e){
                    e.printStackTrace();
                }




                Message msg = myHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("NICK", nick);

             
                b.putString("MSG", MSG.getText().toString());
                msg.setData(b);
                myHandler.sendMessage(msg);

            }
        });
    }



    private static class MyHandler extends Handler {
        private final WeakReference<Lista> sActivity;
        MyHandler(Lista activity){
            sActivity = new WeakReference<Lista>(activity);
        }
        public void handleMessage(Message msg) {
            Lista activity = sActivity.get();
            activity.listItems.add("["+msg.getData().getString("NICK") + "]" +
                    msg.getData().getString("MSG"));
            activity.adapter.notifyDataSetChanged();
            activity.ListaChart.setSelection(activity.listItems.size()-1);
        }
    }
    Handler myHandler = new MyHandler(this);


    public void MSGClick(View view){

        Cleartext(MSG);

    }

    void Cleartext(EditText t){

        t.setText("");
    }

    MqttClient sampleClient=null;
    private void startMQTT(){
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String broker = "tcp://"+ip+":1883";
            clientId = nick;
            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", nick);
                    b.putString("MSG", mqttMessage.toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
                //TODO
            });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe("#");
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        if (sampleClient != null) {
            try {
                sampleClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }




    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        Message msg = myHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("NICK", nick);
        b.putString("MSG", mqttMessage.toString());
        msg.setData(b);
        myHandler.sendMessage(msg);
    }








}
