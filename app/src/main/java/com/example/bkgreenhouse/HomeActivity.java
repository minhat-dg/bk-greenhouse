package com.example.bkgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

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
    String KEY = "aio_Juvs623hQrLI7NCNgarHhflzP8Od";
    ApiUrl apiUrl = new ApiUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadData();

        binding.btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("HomeAc", "CLICK");
                if (binding.btnLight.isChecked()){
                    Log.d("HomeAc", "checked");
                    new PostData("1","LED").execute(apiUrl.getLED_POST_URL());
                } else {
                    Log.d("HomeAc", "not checked");
                    new PostData("0", "LED").execute(apiUrl.getLED_POST_URL());
                }
            }
        });

        binding.btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnWater.isChecked()){
                    Log.d("HomeAc", "checked");
                    new PostData("2","PUMP").execute(apiUrl.getPUMP_POST_URL());
                } else {
                    Log.d("HomeAc", "not checked");
                    new PostData("3", "PUMP").execute(apiUrl.getPUMP_POST_URL());
                }
            }
        });
    }

    private void loadData() {
        loadBtnStt();
        loadDashboard();
    }

    private void refresh(int millisecond) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadDashboard();
            }
        };
        handler.postDelayed(runnable, millisecond);
    }

    private void loadBtnStt() {
        new GetData("LED").execute(apiUrl.getLED_GET_URL());
        new GetData("PUMP").execute(apiUrl.getPUMP_GET_URL());
    }

    private void loadDashboard() {
        new GetData("TEMP").execute(apiUrl.getTEMP_GET_URL());
        new GetData("HUMID").execute(apiUrl.getHUMID_GET_URL());
        refresh(3000);
    }

    private void setView(String substring, String name) {
        switch (name){
            case "LED":
                toggleBtnLight(substring);
                break;
            case "PUMP":
                toggleBtnPump(substring);
                break;
            case "TEMP":
                setTemp(substring);
                break;
            case "HUMID":
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
        if (value.equals("1"))
            binding.btnLight.setChecked(true);
        else
            binding.btnLight.setChecked(false);
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

    class PostData extends AsyncTask<String, String, Void>{
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        String value;
        String name;
        PostData(String value, String name){
            this.value = value;
            this.name = name;
        }
        @Override
        protected Void doInBackground(String... strings) {
            String json = "{\"datum\":{\"value\":\""+ value +"\"}}";

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
            Request request = new Request.Builder()
                    .url(strings[0])
                    .addHeader("X-AIO-Key", KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.d("HomeAc", response.body().string());
                Log.d("HomeAc", response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            setView(value, name);
            super.onPostExecute(unused);
        }
    }
}