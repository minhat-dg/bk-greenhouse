package com.example.bkgreenhouse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import org.json.JSONException;
import org.json.JSONObject;

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
    public CustomProgressDialog dialog;
    ApiUrl apiUrl = new ApiUrl();
    MQTTSingleton mqtt = MQTTSingleton.getInstance(HomeActivity.this);
    String KEY = "";

    String opLT = "lt", opGT = "gt";
    String pump1on = "2", pump1off = "3", pump2on = "4", pump2off = "5", ledOn = "1", ledOff = "2", buzzerOn = "6", buzzerOff = "7";
    int everyIdx = 0, periodIdx = 0;

    String topicTemp = "luongcao2202/feeds/bbc-temp";
    String topicHumid = "luongcao2202/feeds/bbc-humi";
    String topicLed = "luongcao2202/feeds/bbc-led";
    String topicPump1 = "luongcao2202/feeds/bbc-pump";
    String topicPump2 = "luongcao2202/feeds/bbc-pump-2";
    String topicMoisture = "luongcao2202/feeds/bbc-moisture";
    String topicBuzzer = "luongcao2202/feeds/bbc-buzzer";

    ItemAdapter periodAdapter, everyAdapter, hours, minutes;

    BottomSheetDialog bottomSheetDialog;
    Spinner spinnerPeriod, spinnerEvery, spinnerStartOur, spinnerStartMinute;
    CheckBox cbAutoMode, cbScheduleMode, cbWarn;
    Button btnSaveSchedule, btnSaveSensor;
    RelativeLayout blockOption;
    TextView timeUnit;
    EditText tempFrom, tempTo, humidFrom, humidTo, moistureFrom, moistureTo;
    LinearLayout startTimeBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new CustomProgressDialog(HomeActivity.this);
        loadData();
        subscribeMQTT();

        binding.btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnLight.isChecked()) {
                    mqtt.publish(topicLed, ledOn);
                } else {
                    mqtt.publish(topicLed, ledOff);
                }
            }
        });

        binding.btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnWater.isChecked()) {
                    mqtt.publish(topicPump1, pump1on);
                } else {
                    mqtt.publish(topicPump1, pump1off);
                }
            }
        });

        binding.btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnTemp.isChecked()) {
                    mqtt.publish(topicPump2, pump2on);
                } else {
                    mqtt.publish(topicPump2, pump2off);
                }
            }
        });

        binding.btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createScheduleDialog();
            }
        });

        binding.btnSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSensorDialog();
            }
        });

        binding.btnBuzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.btnBuzzer.isChecked()){
                    mqtt.publish(topicBuzzer,  buzzerOn);
                } else {
                    mqtt.publish(topicBuzzer, buzzerOff);
                }
            }
        });

        binding.btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AnalyzeActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loadData() {
        dialog.show();
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        KEY = preferences.getString("key", "");
        new GetData("temp").execute(apiUrl.getTEMP_GET_URL());
        new GetData("humi").execute(apiUrl.getHUMID_GET_URL());
        new GetData("moisture").execute(apiUrl.getMOISTURE_GET_URL());
        new GetData("led").execute(apiUrl.getLED_GET_URL());
        new GetData("pump").execute(apiUrl.getPUMP1_GET_URL());
        new GetData("pump-2").execute(apiUrl.getPUMP2_GET_URL());
        new GetData("buzzer").execute(apiUrl.getBUZZER_GET_URL());
    }

    private void subscribeMQTT() {
        mqtt.subscribe(topicLed, HomeActivity.this);
        mqtt.subscribe(topicPump1, HomeActivity.this);
        mqtt.subscribe(topicPump2, HomeActivity.this);
        mqtt.subscribe(topicHumid, HomeActivity.this);
        mqtt.subscribe(topicTemp, HomeActivity.this);
        mqtt.subscribe(topicBuzzer, HomeActivity.this);
    }


    public void setView(String substring, String name) {
        switch (name) {
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
            case "buzzer":
                toggleBtnBuzzer(substring);
                dialog.hide();
                break;
        }
    }

    private void toggleBtnBuzzer(String value) {
        binding.btnBuzzer.setChecked(value.equals(buzzerOn));
    }

    private void toggleBtnPump2(String value) {
        binding.btnTemp.setChecked(value.equals(pump2on));
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
        binding.btnWater.setChecked(value.equals(pump1on));
    }

    private void toggleBtnLight(String value) {
        binding.btnLight.setChecked(value.equals(ledOn));
    }

    private void createSensorDialog() {
        bottomSheetDialog = new BottomSheetDialog(HomeActivity.this, R.style.BottomSheetTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.sensor_popup, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        tempFrom = bottomSheetView.findViewById(R.id.et_temp_from);
        tempTo = bottomSheetView.findViewById(R.id.et_temp_to);
        humidFrom = bottomSheetView.findViewById(R.id.et_humid_from);
        humidTo = bottomSheetView.findViewById(R.id.et_humid_to);
        moistureFrom = bottomSheetView.findViewById(R.id.et_moisture_from);
        moistureTo = bottomSheetView.findViewById(R.id.et_moisture_to);
        loadSensorSetting();

        btnSaveSensor = bottomSheetView.findViewById(R.id.btn_save_sensor);
        btnSaveSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidData();
                bottomSheetDialog.hide();
                updateActions();
            }
        });
    }

    private void updateActions() {
        SharedPreferences preferences = getSharedPreferences("waterMode", MODE_PRIVATE);
        String mode = preferences.getString("mode", "");
        if(mode.equals("true")){
            createAutoAction();
        } else {
            createScheduleAction();
        }
    }

    private void checkValidData() {
        String temp_from = tempFrom.getText().toString();
        String temp_to = tempTo.getText().toString();
        String humid_from = humidFrom.getText().toString();
        String humid_to = humidTo.getText().toString();
        String moisture_from = moistureFrom.getText().toString();
        String moisture_to = moistureTo.getText().toString();
        if (temp_from.isEmpty()) {
            tempFrom.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (temp_to.isEmpty()) {
            tempTo.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (humid_from.isEmpty()) {
            humidFrom.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (humid_to.isEmpty()) {
            humidTo.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (moisture_from.isEmpty()) {
            moistureFrom.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (moisture_to.isEmpty()) {
            moistureTo.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        saveSensor(temp_from, temp_to, humid_from, humid_to, moisture_from, moisture_to);
    }

    private void saveSensor(String temp_from, String temp_to, String humid_from, String humid_to, String moisture_from, String moisture_to) {
        SharedPreferences preferences = getSharedPreferences("sensor", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("is_init", "true");
        editor.putString("temp_from", temp_from);
        editor.putString("temp_to", temp_to);
        editor.putString("humid_from", humid_from);
        editor.putString("humid_to", humid_to);
        editor.putString("moisture_from", moisture_from);
        editor.putString("moisture_to", moisture_to);
        editor.apply();
    }

    private void loadSensorSetting() {
        SharedPreferences preferences = getSharedPreferences("sensor", MODE_PRIVATE);
        String isInit = preferences.getString("is_init", "");
        if (isInit.equals("true")) {
            tempFrom.setText(preferences.getString("temp_from", ""));
            tempTo.setText(preferences.getString("temp_to", ""));
            humidFrom.setText(preferences.getString("humid_from", ""));
            humidTo.setText(preferences.getString("humid_to", ""));
            moistureFrom.setText(preferences.getString("moisture_from", ""));
            moistureTo.setText(preferences.getString("moisture_to", ""));
        }
    }

    public void createScheduleDialog() {
        bottomSheetDialog = new BottomSheetDialog(HomeActivity.this, R.style.BottomSheetTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.schedule_popup, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        timeUnit = bottomSheetView.findViewById(R.id.tv_time_unit);
        spinnerPeriod = bottomSheetView.findViewById(R.id.spinner_schedule);
        spinnerEvery = bottomSheetView.findViewById(R.id.spinner_every);
        spinnerStartOur = bottomSheetView.findViewById(R.id.spinner_start_hour);
        spinnerStartMinute = bottomSheetView.findViewById(R.id.spinner_start_minute);
        startTimeBlock = bottomSheetView.findViewById(R.id.schedule_popup_line5);
        cbAutoMode = bottomSheetView.findViewById(R.id.cb_auto);
        cbScheduleMode = bottomSheetView.findViewById(R.id.cb_schedule);
        cbWarn = bottomSheetView.findViewById(R.id.cb_warn);
        blockOption = bottomSheetView.findViewById(R.id.blockOption);
        btnSaveSchedule = bottomSheetView.findViewById(R.id.btn_save_schedule);

        initSpinner();
        loadWaterMode();

        cbAutoMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setWaterMode(compoundButton.isChecked());
            }
        });

        cbScheduleMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setWaterMode(!compoundButton.isChecked());
            }
        });

        btnSaveSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSchedule(cbAutoMode.isChecked());
                createAction(cbAutoMode.isChecked());
                bottomSheetDialog.hide();
            }
        });
    }

    private void createAction(boolean checked) {
        if (checked) {
            createAutoAction();
        } else {
            createScheduleAction();
        }
    }

    private void createScheduleAction() {
        deleteAllActions();
        String unit = timeUnit.getText().toString();
        Log.d("http", unit);
        SharedPreferences preferences = getSharedPreferences("sensor", MODE_PRIVATE);
        String moistureTo = preferences.getString("moisture_to", "");
        String moistureFrom = preferences.getString("moisture_from", "");
        new PostData(apiUrl.getCreateScheduleActionBody(apiUrl.getFEED_PUMP1(), minutes.getItem(spinnerStartMinute.getSelectedItemPosition()).getName(), hours.getItem(spinnerStartOur.getSelectedItemPosition()).getName(), everyAdapter.getItem(spinnerEvery.getSelectedItemPosition()).getName(), pump1on, unit), KEY, "pump1_on").execute(apiUrl.getCREATE_ACTION());
        if (cbWarn.isChecked()) {
            new PostData(apiUrl.getCreateReactiveActionBody(opLT, apiUrl.getFEED_MOISTURE(), moistureFrom, apiUrl.getFEED_BUZZER(), buzzerOn), KEY, "buzzer_on").execute(apiUrl.getCREATE_ACTION());
            new PostData(apiUrl.getCreateReactiveActionBody(opGT, apiUrl.getFEED_MOISTURE(), moistureFrom, apiUrl.getFEED_BUZZER(), buzzerOff), KEY, "buzzer_off").execute(apiUrl.getCREATE_ACTION());
        }
        new PostData(apiUrl.getCreateReactiveActionBody(opGT, apiUrl.getFEED_MOISTURE(), moistureTo,apiUrl.getFEED_PUMP1(), pump1off), KEY, "pump1_off").execute(apiUrl.getCREATE_ACTION());

        String tempFrom = preferences.getString("temp_from", "");
        String tempTo = preferences.getString("temp_to", "");
        new PostData(apiUrl.getCreateReactiveActionBody(opLT, apiUrl.getFEED_TEMP(), tempFrom, apiUrl.getFEED_LED(), ledOn), KEY, "led_on").execute(apiUrl.getCREATE_ACTION());
        new PostData(apiUrl.getCreateReactiveActionBody(opGT, apiUrl.getFEED_TEMP(), tempTo, apiUrl.getFEED_LED(), ledOff), KEY, "led_off").execute(apiUrl.getCREATE_ACTION());

        String humidFrom = preferences.getString("humid_from", "");
        String humidTo = preferences.getString("humid_to", "");
        new PostData(apiUrl.getCreateReactiveActionBody(opLT, apiUrl.getFEED_HUMID(), humidFrom, apiUrl.getFEED_PUMP2(), pump2on), KEY, "pump2_on").execute(apiUrl.getCREATE_ACTION());
        new PostData(apiUrl.getCreateReactiveActionBody(opGT, apiUrl.getFEED_HUMID(), humidTo, apiUrl.getFEED_PUMP2(), pump2off), KEY, "pump2_off").execute(apiUrl.getCREATE_ACTION());
    }

    private void createAutoAction() {
        deleteAllActions();
        SharedPreferences preferences = getSharedPreferences("sensor", MODE_PRIVATE);
        String moistureFrom = preferences.getString("moisture_from", "");
        String moistureTo = preferences.getString("moisture_to", "");
        new PostData(apiUrl.getCreateReactiveActionBody(opLT, apiUrl.getFEED_MOISTURE(), moistureFrom, apiUrl.getFEED_PUMP1(), pump1on), KEY, "pump1_on").execute(apiUrl.getCREATE_ACTION());
        new PostData(apiUrl.getCreateReactiveActionBody(opGT, apiUrl.getFEED_MOISTURE(), moistureTo,apiUrl.getFEED_PUMP1(), pump1off), KEY, "pump1_off").execute(apiUrl.getCREATE_ACTION());

        String tempFrom = preferences.getString("temp_from", "");
        String tempTo = preferences.getString("temp_to", "");
        new PostData(apiUrl.getCreateReactiveActionBody(opLT, apiUrl.getFEED_TEMP(), tempFrom, apiUrl.getFEED_LED(), ledOn), KEY, "led_on").execute(apiUrl.getCREATE_ACTION());
        new PostData(apiUrl.getCreateReactiveActionBody(opGT, apiUrl.getFEED_TEMP(), tempTo, apiUrl.getFEED_LED(), ledOff), KEY, "led_off").execute(apiUrl.getCREATE_ACTION());

        String humidFrom = preferences.getString("humid_from", "");
        String humidTo = preferences.getString("humid_to", "");
        new PostData(apiUrl.getCreateReactiveActionBody(opLT, apiUrl.getFEED_HUMID(), humidFrom, apiUrl.getFEED_PUMP2(), pump2on), KEY, "pump2_on").execute(apiUrl.getCREATE_ACTION());
        new PostData(apiUrl.getCreateReactiveActionBody(opGT, apiUrl.getFEED_HUMID(), humidTo, apiUrl.getFEED_PUMP2(), pump2off), KEY, "pump2_off").execute(apiUrl.getCREATE_ACTION());
    }

    private void deleteAllActions() {
        SharedPreferences preferences = getSharedPreferences("actions", MODE_PRIVATE);
        String pump1on = preferences.getString("pump1_on","");
        if(pump1on.equals(""))
            return;
        new DeleteData(pump1on, "pump1_on").execute(apiUrl.getDELETE_ACTION());
        String pump1off =preferences.getString("pump1_off","");
        new DeleteData(pump1off, "pump1_off").execute(apiUrl.getDELETE_ACTION());
        String pump2on =preferences.getString("pump2_on","");
        new DeleteData(pump2on, "pump2_on").execute(apiUrl.getDELETE_ACTION());
        String pump2off =preferences.getString("pump2_off","");
        new DeleteData(pump2off, "pump2_off").execute(apiUrl.getDELETE_ACTION());
        String ledOn =preferences.getString("led_on","");
        new DeleteData(ledOn, "led_on").execute(apiUrl.getDELETE_ACTION());
        String ledOff =preferences.getString("led_off","");
        new DeleteData(ledOff, "led_off").execute(apiUrl.getDELETE_ACTION());
        String buzzerOn = preferences.getString("buzzer_on","");
        new DeleteData(buzzerOn, "buzzer_on").execute(apiUrl.getDELETE_ACTION());
        String buzzerOff = preferences.getString("buzzer_off","");
        new DeleteData(buzzerOff, "buzzer_off").execute(apiUrl.getDELETE_ACTION());
    }

    private void saveActionId(String name, String id){
        SharedPreferences preferences = getSharedPreferences("actions", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, id);
        editor.apply();
    }

    private void saveSchedule(boolean checked) {
        //save mode
        SharedPreferences preferences = getSharedPreferences("waterMode", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (!checked) {
            editor.putString("mode", "false");
            editor.putString("period", String.valueOf(spinnerPeriod.getSelectedItemPosition()));
            editor.putString("every", String.valueOf(spinnerEvery.getSelectedItemPosition()));
            if (spinnerStartOur != null){
                editor.putString("start_hour", String.valueOf(spinnerStartOur.getSelectedItemPosition()));
                editor.putString("start_minute", String.valueOf(spinnerStartMinute.getSelectedItemPosition()));
            }
            editor.putBoolean("warn", cbWarn.isChecked());
        } else {
            editor.putString("mode", "true");
        }
        editor.apply();
    }

    private void setWaterMode(boolean mode) {
        //True is auto, False is schedule
        if (mode) {
            cbAutoMode.setChecked(true);
            cbScheduleMode.setChecked(false);
            blockOption.setVisibility(View.GONE);
        } else {
            cbAutoMode.setChecked(false);
            cbScheduleMode.setChecked(true);
            blockOption.setVisibility(View.VISIBLE);
        }
    }

    private void loadWaterMode() {
        SharedPreferences preferences = getSharedPreferences("waterMode", MODE_PRIVATE);
        String mode = preferences.getString("mode", "");
        setWaterMode(mode.equals("true"));
        if (mode.equals("false")) {
            periodIdx = Integer.parseInt(preferences.getString("period", ""));
            everyIdx = Integer.parseInt(preferences.getString("every", ""));
            int startHourIdx = Integer.parseInt(preferences.getString("start_hour", ""));
            int startMinuteIdx = Integer.parseInt(preferences.getString("start_minute", ""));
            boolean warn = preferences.getBoolean("warn", false);
            spinnerPeriod.setSelection(periodIdx);
            spinnerEvery.setSelection(everyIdx);
            spinnerStartOur.setSelection(startHourIdx);
            spinnerStartMinute.setSelection(startMinuteIdx);
            cbWarn.setChecked(warn);
        }
    }

    private void initSpinner() {
        periodAdapter = new ItemAdapter(this, R.layout.item_spiner_selected, getListPeriod());
        spinnerPeriod.setAdapter(periodAdapter);
        final String[] unit = {""};
        spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String period = periodAdapter.getItem(i).getName();
                unit[0] = period.substring(period.indexOf(' ') + 1);
                if (unit[0].equals("tuần")){
                    timeUnit.setText(unit[0]);
                    timeUnit.setVisibility(View.INVISIBLE);
                } else {
                    timeUnit.setText(unit[0]);
                }
                if(unit[0].equals("giờ") || unit[0].equals("phút")){
                    startTimeBlock.setVisibility(View.GONE);
                }
                else {
                    startTimeBlock.setVisibility(View.VISIBLE);
                }
                everyAdapter = new ItemAdapter(getApplicationContext(), R.layout.item_spiner_selected, getListEvery(unit[0]));
                spinnerEvery.setAdapter(everyAdapter);
                if (period.equals(periodAdapter.getItem(periodIdx).getName())){
                    spinnerEvery.setSelection(everyIdx);
                } else {
                    spinnerEvery.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        hours = new ItemAdapter(this, R.layout.item_spiner_selected, getListOur());
        minutes = new ItemAdapter(this, R.layout.item_spiner_selected, getListMinute());
        spinnerStartOur.setAdapter(hours);
        spinnerStartMinute.setAdapter(minutes);
    }

    private List<SpinnerItem> getListMinute() {
        List<SpinnerItem> list = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            list.add(new SpinnerItem(String.valueOf(i)));
        }
        return list;
    }

    private List<SpinnerItem> getListOur() {
        List<SpinnerItem> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            list.add(new SpinnerItem(String.valueOf(i)));
        }
        return list;
    }

    private List<SpinnerItem> getListEvery(String unit) {
        List<SpinnerItem> list = new ArrayList<>();
        switch (unit) {
            case "phút":
                for (int i = 30; i <= 60; i++) {
                    list.add(new SpinnerItem(String.valueOf(i)));
                }
                break;
            case "giờ":
                for (int i = 1; i <= 24; i++) {
                    list.add(new SpinnerItem(String.valueOf(i)));
                }
                break;
            case "ngày":
                for (int i = 1; i <= 30; i++) {
                    list.add(new SpinnerItem(String.valueOf(i)));
                }
                break;
            case "tuần":
                list.add(new SpinnerItem("Thứ hai"));
                list.add(new SpinnerItem("Thứ ba"));
                list.add(new SpinnerItem("Thứ tư"));
                list.add(new SpinnerItem("Thứ năm"));
                list.add(new SpinnerItem("Thứ sáu"));
                list.add(new SpinnerItem("Thứ bảy"));
                list.add(new SpinnerItem("Chủ nhật"));
                break;
            default:
                list.add(new SpinnerItem("0"));
        }
        return list;
    }

    private List<SpinnerItem> getListPeriod() {
        List<SpinnerItem> list = new ArrayList<>();
        list.add(new SpinnerItem("Hàng phút"));
        list.add(new SpinnerItem("Hàng giờ"));
        list.add(new SpinnerItem("Hàng ngày"));
        list.add(new SpinnerItem("Hàng tuần"));
        return list;
    }

    class GetData extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        String name;

        GetData(String name) {
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
            setView(s.substring(0, s.indexOf(",")), name);
            super.onPostExecute(s);
        }
    }

    class PostData extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        String json;
        String KEY;
        String name;
        public PostData(String json, String KEY, String name) {
            this.json = json;
            this.KEY = KEY;
            this.name = name;
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
            Request request = new Request.Builder()
                    .url(strings[0])
                    .addHeader("X-AIO-Key", KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
            Log.d("http", json);
            try {
                Response response = client.newCall(request).execute();
                JSONObject jObj = new JSONObject(response.body().string());
                return jObj.getString("id");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            saveActionId(name, s);
        }
    }

    class DeleteData extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        String id;
        String name;
        public DeleteData(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0] + id;
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("X-AIO-Key", KEY)
                    .addHeader("Content-Type", "application/json")
                    .delete()
                    .build();

            try {
                Response response = client.newCall(request).execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            saveActionId(name, "");
        }
    }
}