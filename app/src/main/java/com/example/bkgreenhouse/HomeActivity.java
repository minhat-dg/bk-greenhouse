package com.example.bkgreenhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bkgreenhouse.databinding.ActivityHomeBinding;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    String KEY = "aio_sVox31ArO0laWWiQ0rD8V1cSl5x3";
    OkHttpClient client;
    String URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-led/data/retain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = new OkHttpClient.Builder().build();

        binding.btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request request = new Request.Builder()
                        .url(URL)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d("HomeAc", "Fail");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.d("HomeAc", "Ok");
                        Log.d("HomeAc", response.body().string());
                    }
                });

            }
        });
    }

    private void fail() {
        Toast.makeText(HomeActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
    }

    private void success() {
        Toast.makeText(HomeActivity.this, "SUCCESS!", Toast.LENGTH_SHORT).show();
    }

}