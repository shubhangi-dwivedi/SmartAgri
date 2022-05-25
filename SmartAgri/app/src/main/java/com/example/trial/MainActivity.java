package com.example.trial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {

    LinearLayout temperatureLayout, soilLayout, humidityLayout, locationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("HOME");

        temperatureLayout = findViewById(R.id.Temperature);
        soilLayout = findViewById(R.id.soil);
        humidityLayout = findViewById(R.id.humidity);
        locationLayout = findViewById(R.id.longlat);

        temperatureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TempActivity.class));
            }
        });
        soilLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SoilMoistureActivity.class));
            }
        });

        humidityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HumidityActivity.class));
            }
        });

        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LongLatActivity.class));
            }
        });
    }
}

