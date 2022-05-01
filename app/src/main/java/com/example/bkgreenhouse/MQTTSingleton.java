package com.example.bkgreenhouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MQTTSingleton {
    private static MQTTSingleton instance = null;
    private static MqttAndroidClient client;
    private static Context mContext;
    private static LoginActivity mActivity;
    private static String username, key;

    public static MQTTSingleton getInstance(Context context, LoginActivity activity){
        if(instance == null){
            synchronized(MQTTSingleton.class){
                if(instance == null){
                    mContext = context;
                    mActivity = activity;
                    instance = new MQTTSingleton();
                }
            }
        }
        return instance;
    }
    public static MQTTSingleton getInstance(Context context){
        if(instance == null){
            synchronized(MQTTSingleton.class){
                if(instance == null){
                    mContext = context;
                    instance = new MQTTSingleton();
                }
            }
        }
        return instance;
    }

    private MQTTSingleton() {
        this.client = new MqttAndroidClient(mContext, "tcp://io.adafruit.com:1883", "ABC");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void connect(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(this.username);
        options.setPassword(this.key.toCharArray());
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    SharedPreferences preferences = mContext.getSharedPreferences("sensor", Context.MODE_PRIVATE);
                    String isInit = preferences.getString("is_init", "");
                    if(isInit.equals("true")){
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        mContext.startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(mContext, SetSensorActivity.class);
                        mContext.startActivity(intent);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("mqtt", "onFailure");
                    mActivity.dialog.hide();
                    Toast.makeText(mContext, "Đăng nhập thất bại! Vui lòng kiểm tra username và active key.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic, HomeActivity instance){
        try {
            client.subscribe(topic, 0);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String name = topic.substring(topic.indexOf('-')+1);
                    instance.setView(new String(message.getPayload()), name);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String payload){
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
}
