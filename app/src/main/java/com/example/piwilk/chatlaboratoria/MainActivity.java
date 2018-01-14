package com.example.piwilk.chatlaboratoria;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    public static String IP="ip";
    public static String NICK="nick";
    EditText nickEditText;
    EditText ipEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nickEditText = (EditText) findViewById(R.id.ETNick);
        ipEditText = (EditText) findViewById(R.id.o);

        


    }





    public void StartClick(View view){
        Intent intent = new Intent(MainActivity.this, Lista.class);
        intent.putExtra(IP, ipEditText.getText().toString());
        intent.putExtra(NICK, nickEditText.getText().toString());
        startActivity(intent);

    }

    public void NickClick(View view){

       Cleartext(nickEditText);

    }

    public void ServerClick(View view){

    Cleartext(ipEditText);

    }

    void Cleartext(EditText t){

        t.setText("");
    }

    MqttClient sampleClient=null;




}
