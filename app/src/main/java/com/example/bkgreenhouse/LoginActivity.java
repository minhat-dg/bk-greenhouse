package com.example.bkgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
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
    private Boolean remember;
    public CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new CustomProgressDialog(LoginActivity.this);

        loadLoginInfor();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLogin();
            }
        });

        binding.cbRemeber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    remember = true;
                } else {
                    remember = false;
                }
            }
        });
    }

    private void loadLoginInfor() {
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true")){
            String username = preferences.getString("username", "");
            String key = preferences.getString("key", "");
            binding.etUsername.setText(username);
            binding.etKey.setText(key);
            binding.cbRemeber.setChecked(true);
            remember = true;
        } else {
            remember = false;
        }
    }

    private void clickLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String key = binding.etKey.getText().toString().trim();
        if(!checkValidData(username, key))
            return;
        dialog.show();
        rememberLogin(username, key, remember);
        connect(username, key);
    }

    private void rememberLogin(String username , String key, Boolean remember) {
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (remember){
            editor.putString("remember", "true");
            editor.putString("username", username);
            editor.putString("key", key);
        } else {
            editor.putString("remember", "false");
        }
        editor.apply();
    }

    private void connect(String username, String key) {
        MQTTSingleton mqtt = MQTTSingleton.getInstance(LoginActivity.this, LoginActivity.this);
        mqtt.setUsername(username);
        mqtt.setKey(key);
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