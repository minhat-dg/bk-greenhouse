package com.example.bkgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.bkgreenhouse.databinding.ActivitySetSensorBinding;

public class SetSensorActivity extends AppCompatActivity {
    ActivitySetSensorBinding binding;
    private String tempFrom, tempTo, humidFrom, humidTo, moistureFrom, moistureTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetSensorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.btnSaveSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validData();
            }
        });
    }

    private void validData() {
        tempFrom = binding.etTempFrom.getText().toString();
        tempTo = binding.etTempTo.getText().toString();
        humidFrom = binding.etHumidFrom.getText().toString();
        humidTo = binding.etHumidTo.getText().toString();
        moistureFrom = binding.etMoistureFrom.getText().toString();
        moistureTo = binding.etMoistureTo.getText().toString();

        if(tempFrom.isEmpty()){
            binding.etTempFrom.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if(tempTo.isEmpty()){
            binding.etTempTo.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if(humidFrom.isEmpty()){
            binding.etHumidFrom.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if(humidTo.isEmpty()){
            binding.etHumidTo.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if(moistureFrom.isEmpty()){
            binding.etMoistureFrom.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if(moistureTo.isEmpty()){
            binding.etMoistureTo.setError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        saveData();
    }

    private void saveData() {
        SharedPreferences preferences = getSharedPreferences("sensor", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("is_init", "true");
        editor.putString("temp_from", tempFrom);
        editor.putString("temp_to", tempTo);
        editor.putString("humid_from", humidFrom);
        editor.putString("humid_to", humidTo);
        editor.putString("moisture_from", moistureFrom);
        editor.putString("moisture_to", moistureTo);
        editor.apply();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}