package com.example.bkgreenhouse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bkgreenhouse.databinding.ActivityHomeBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
import java.util.ArrayList;
import java.util.List;
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
    String topicPump1 = "luongcao2202/feeds/bbc-pump";
    String topicPump2 = "luongcao2202/feeds/bbc-pump-2";

    BottomSheetDialog bottomSheetDialog;
    Spinner spinnerPeriod, spinnerEvery, spinnerStartOur, spinnerStartMinute,  spinnerEndHour, spinnerEndMinute, spinnerWarn;
    TextView timeUnit;

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
                    mqtt.publish(topicPump1, "2");
                } else {
                    mqtt.publish(topicPump1, "3");
                }
            }
        });

        binding.btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnTemp.isChecked()){
                    mqtt.publish(topicPump2, "4");
                } else {
                    mqtt.publish(topicPump2, "5");
                }
            }
        });

        binding.btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createScheduleDialog();
            }
        });
    }

    private void loadData() {
        new GetData("led").execute(apiUrl.getLED_GET_URL());
        new GetData("pump").execute(apiUrl.getPUMP1_GET_URL());
        new GetData("pump-2").execute(apiUrl.getPUMP2_GET_URL());
        new GetData("temp").execute(apiUrl.getTEMP_GET_URL());
        new GetData("humi").execute(apiUrl.getHUMID_GET_URL());
        new GetData("moisture").execute(apiUrl.getMOISTURE_GET_URL());
    }

    private void subscribeMQTT() {
        mqtt.subscribe(topicLed, HomeActivity.this);
        mqtt.subscribe(topicPump1, HomeActivity.this);
        mqtt.subscribe(topicPump2, HomeActivity.this);
        mqtt.subscribe(topicHumid, HomeActivity.this);
        mqtt.subscribe(topicTemp, HomeActivity.this);
    }


    public void setView(String substring, String name) {
        switch (name){
            case "led":
                toggleBtnLight(substring);
                break;
            case "pump":
                toggleBtnPump1(substring);
                break;
            case "temp":
                setTemp(substring);
                break;
            case "humi":
                setHumid(substring);
                break;
            case "moisture":
                setMoisture(substring);
                break;
            case "pump-2":
                toggleBtnPump2(substring);
                break;
        }
    }

    private void toggleBtnPump2(String value) {
        if (value.equals("4")) {
            binding.btnTemp.setChecked(true);
        } else {
            binding.btnTemp.setChecked(false);
        }
    }

    private void setMoisture(String value) {
        binding.tvMoisterValue.setText(value);
    }

    private void setHumid(String value) {
        binding.tvHumidValue.setText(value);
    }

    private void setTemp(String value) {
        binding.tvTempValue.setText(value);
    }

    private void toggleBtnPump1(String value) {
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

    public void createScheduleDialog(){
        bottomSheetDialog = new BottomSheetDialog(HomeActivity.this, R.style.BottomSheetTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.schedule_popup, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        timeUnit = bottomSheetView.findViewById(R.id.tv_time_unit);
        spinnerPeriod = bottomSheetView.findViewById(R.id.spinner_schedule);
        spinnerEvery = bottomSheetView.findViewById(R.id.spinner_every);
        spinnerStartOur = bottomSheetView.findViewById(R.id.spinner_start_hour);
        spinnerStartMinute = bottomSheetView.findViewById(R.id.spinner_start_minute);
        spinnerEndHour = bottomSheetView.findViewById(R.id.spinner_end_hour);
        spinnerEndMinute = bottomSheetView.findViewById(R.id.spinner_end_minute);
        spinnerWarn =  bottomSheetView.findViewById(R.id.spinner_warn);
        initSpinner();
    }

    private void initSpinner() {
        final ItemAdapter periodAdapter = new ItemAdapter(this, R.layout.item_spiner_selected, getListPeriod());
        final String[] unit = {""};
        spinnerPeriod.setAdapter(periodAdapter);
        spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String period = periodAdapter.getItem(i).getName();
                unit[0] = period.substring(period.indexOf(' ')+1);
                timeUnit.setText(unit[0]);
                final ItemAdapter everyAdapter = new ItemAdapter(getApplicationContext(), R.layout.item_spiner_selected, getListEvery(unit[0]));
                spinnerEvery.setAdapter(everyAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ItemAdapter hours = new ItemAdapter(this, R.layout.item_spiner_selected, getListOur());
        ItemAdapter minutes = new ItemAdapter(this, R.layout.item_spiner_selected, getListMinute());
        spinnerStartOur.setAdapter(hours);
        spinnerStartMinute.setAdapter(minutes);
        spinnerEndHour.setAdapter(hours);
        spinnerEndMinute.setAdapter(minutes);
        spinnerWarn.setAdapter(minutes);
    }

    private List<SpinnerItem> getListMinute() {
        List<SpinnerItem> list = new ArrayList<>();
        for (int i = 0; i < 60; i++){
            list.add(new SpinnerItem(String.valueOf(i)));
        }
        return list;
    }

    private List<SpinnerItem> getListOur() {
        List<SpinnerItem> list = new ArrayList<>();
        for (int i = 0; i < 24; i++){
            list.add(new SpinnerItem(String.valueOf(i)));
        }
        return list;
    }

    private List<SpinnerItem> getListEvery(String unit) {
        List<SpinnerItem> list = new ArrayList<>();
        switch (unit){
            case "phút":
                for (int i = 30; i <= 60; i++){
                    list.add(new SpinnerItem(String.valueOf(i)));
                }
                break;
            case "giờ":
                for (int i = 1; i <= 24; i++){
                    list.add(new SpinnerItem(String.valueOf(i)));
                }
                break;
            case "ngày":
                for (int i = 1; i <= 7; i++){
                    list.add(new SpinnerItem(String.valueOf(i)));
                }
                break;
            case "tuần":
                for (int i = 1; i <= 50; i++){
                    list.add(new SpinnerItem(String.valueOf(i)));
                }
                break;
            case "tháng":
                for (int i = 1; i <= 12; i++){
                    list.add(new SpinnerItem(String.valueOf(i)));
                }
                break;
            default:
                list.add(new SpinnerItem("0"));
        }
        Log.d("spinner", list.get(0).getName());
        Log.d("spinner", unit);
        return list;
    }

    private List<SpinnerItem> getListPeriod() {
        List<SpinnerItem> list = new ArrayList<>();
        list.add(new SpinnerItem("Hàng phút"));
        list.add(new SpinnerItem("Hàng giờ"));
        list.add(new SpinnerItem("Hàng ngày"));
        list.add(new SpinnerItem("Hàng tuần"));
        list.add(new SpinnerItem("Hàng tháng"));

        return list;
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