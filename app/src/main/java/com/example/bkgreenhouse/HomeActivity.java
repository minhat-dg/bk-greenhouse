package com.example.bkgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.bkgreenhouse.databinding.ActivityHomeBinding;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    String KEY = "aio_sVox31ArO0laWWiQ0rD8V1cSl5x3";
    String LED_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-led/data/retain";
    String LED_POST_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-led/data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new GetBtn().execute(LED_GET_URL);

        binding.btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBtn();
            }
        });
    }

    private void toggleBtn() {
        if (binding.btnPush.getText() == "On") {
            new PostBtn("0").execute(LED_POST_URL);
        }
        else {
            new PostBtn("1").execute(LED_POST_URL);
        }
    }

    private void setBtn(String substring) {
        if (substring.equals("1"))
            binding.btnPush.setText("On");
        else
            binding.btnPush.setText("Off");
    }

    class GetBtn extends AsyncTask<String, String, String>{
        OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).connectTimeout(15, TimeUnit.SECONDS)
                .build();

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
            setBtn(s.substring(0,1));
            super.onPostExecute(s);
        }
    }

    class PostBtn extends AsyncTask<String, String, Void>{
        OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).connectTimeout(15, TimeUnit.SECONDS)
                .build();
        String value;
        PostBtn(String value){
            this.value = value;
        }
        @Override
        protected Void doInBackground(String... strings) {
            String json = "{\"datum\":{\"value\":\""+ value +"\"}}";

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
            Request request = new Request.Builder()
                    .url(strings[0])
                    .addHeader("X-AIO-Key","aio_Juvs623hQrLI7NCNgarHhflzP8Od")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            setBtn(value);
            super.onPostExecute(unused);
        }
    }
}