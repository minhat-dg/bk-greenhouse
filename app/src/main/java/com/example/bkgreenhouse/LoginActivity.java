package com.example.bkgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bkgreenhouse.databinding.ActivityLoginBinding;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLogin();
            }
        });
    }

    private void clickLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String key = binding.etKey.getText().toString().trim();
        if(!checkValidData(username, key))
            return;
        connect(username, key);
    }

    private void connect(String username, String key) {
        MQTTSingleton mqtt = MQTTSingleton.getInstance(LoginActivity.this);
        mqtt.setUsername("luongcao2202");
        mqtt.setKey("aio_QzXF92oO5PwuocR0SJuVGy2YqNmd");
        mqtt.connect();
    }

    private boolean checkValidData(String username, String key) {
        if(username.isEmpty()){
            binding.etUsername.setError("Vui lòng nhập username!");
            return false;
        }
        if(key.isEmpty()){
            binding.etKey.setError("Vui lòng nhập Active key!");
            return false;
        }
        return true;
    }

}