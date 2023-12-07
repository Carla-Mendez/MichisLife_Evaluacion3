package com.stomas.michislifever2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Detalle extends AppCompatActivity {

    private static  String mqttHost = "tcp://michis456.cloud.shiftr.io:1883";
    private static String IdUsuario = "AppAndroid";
    private static String Topico = "Mensaje";
    private static String User = "michis456";
    private static String Pass = "frwFS4Cy3PnRfy5Z";
    //Variables
    private TextView textView;
    private EditText editTextMessage;
    private Button buttonSendMessage;
    //Liberia MQTT
    private MqttClient mqttCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        //Variables
        textView =findViewById(R.id.textView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);

        try {
            //Creacion de un Cliente MQTT
            mqttCliente = new MqttClient(mqttHost,IdUsuario,null);
            MqttConnectOptions options=new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());

            mqttCliente.connect(options);
            Toast.makeText(this, "Aplicacion conectada al Servidor MQTT", Toast.LENGTH_SHORT).show();

            mqttCliente.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("Mqtt","Conexion Perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    runOnUiThread(()-> textView.setText(payload));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("Mqtt","Entrega Completa");
                }
            });

            mqttCliente.subscribe(Topico);
            buttonSendMessage.setOnClickListener( v -> {
                String message = editTextMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    editTextMessage.getText().clear();
                } else {
                    Toast.makeText(this,"Ingrese un Mensaje", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttCliente.publish(Topico, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            mqttCliente.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
