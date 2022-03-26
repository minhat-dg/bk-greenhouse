package com.example.bkgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.bkgreenhouse.databinding.ActivityHomeBinding;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    ApiUrl apiUrl = new ApiUrl();
    MQTTSingleton mqtt = MQTTSingleton.getInstance(HomeActivity.this);

    String topicTemp = "luongcao2202/feeds/bbc-temp";
    String topicHumid = "luongcao2202/feeds/bbc-humi";
    String topicLed = "luongcao2202/feeds/bbc-led";
    String topicPump = "luongcao2202/feeds/bbc-pump";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadData();
        subscribeMQTT();

        binding.btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnLight.isChecked()){
                    mqtt.publish(topicLed, "1");
                } else {
                    mqtt.publish(topicLed, "0");
                }
            }
        });

        binding.btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnWater.isChecked()){
                    mqtt.publish(topicPump, "2");
                } else {
                    mqtt.publish(topicPump, "3");
                }
            }
        });
    }
    private void loadData() {
        new GetData("led").execute(apiUrl.getLED_GET_URL());
        new GetData("pump").execute(apiUrl.getPUMP_GET_URL());
        new GetData("temp").execute(apiUrl.getTEMP_GET_URL());
        new GetData("humi").execute(apiUrl.getHUMID_GET_URL());
    }

    private void subscribeMQTT() {
        mqtt.subscribe(topicLed, HomeActivity.this);
        mqtt.subscribe(topicPump, HomeActivity.this);
        mqtt.subscribe(topicHumid, HomeActivity.this);
        mqtt.subscribe(topicTemp, HomeActivity.this);
    }


    public void setView(String substring, String name) {
        switch (name){
            case "led":
                toggleBtnLight(substring);
                break;
            case "pump":
                toggleBtnPump(substring);
                break;
            case "temp":
                setTemp(substring);
                break;
            case "humi":
                setHumid(substring);
                break;
        }
    }

    private void setHumid(String value) {
        binding.tvHumidValue.setText(value);
    }

    private void setTemp(String value) {
        binding.tvTempValue.setText(value);
    }

    private void toggleBtnPump(String value) {
        if (value.equals("2")) {
            binding.btnWater.setChecked(true);
        } else {
            binding.btnWater.setChecked(false);
        }
    }

    private void toggleBtnLight(String value) {
        if (value.equals("1")) {
            binding.btnLight.setChecked(true);
        }
        else{
            binding.btnLight.setChecked(false);
        }
    }


    class GetData extends AsyncTask<String, String, String>{
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        String name;
        GetData(String name){
            this.name = name;
        }
        @Override
        protected String doInBackground(String... strings) {
            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            setView(s.substring(0,s.indexOf(",")), name);
            super.onPostExecute(s);
        }
    }
}