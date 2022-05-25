package com.example.trial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class SoilMoistureActivity extends AppCompatActivity {
    TextView soilMoist;
    //DatabaseReference db;
    ProgressBar soilMoistPB;

    ProgressBar humPB;
    GraphView graphView;
    ArrayList<Float> arr = new ArrayList(5);
    private LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_moisture);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Soil Moisture");

        soilMoist = findViewById(R.id.soilMoistAmount);
        soilMoistPB = findViewById(R.id.soilMoistPB);

        graphView = findViewById(R.id.idGraphView);
        graphView.setTitle("Soil Moisture");
        graphView.setTitleColor(R.color.purple_200);
        graphView.setTitleTextSize(80);

        DatabaseReference messagesReference = FirebaseDatabase.getInstance().getReference().child("soilMoisture");
        Query listQuery = messagesReference.orderByChild("soilMoisture").limitToLast(5);
        listQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double k = 0.0;
                arr.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    arr.add(Float.parseFloat(snapshot.getValue().toString()));
                    k = k + Float.parseFloat(snapshot.getValue().toString());
                }
                k = k / 5;
                graphView.removeAllSeries();
                series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                        new DataPoint(0, arr.get(0)),
                        new DataPoint(1, arr.get(1)),
                        new DataPoint(2, arr.get(2)),
                        new DataPoint(3, arr.get(3)),
                        new DataPoint(4, arr.get(4)),
                });
                graphView.addSeries(series);
                soilMoist.setText(k + " kg/kg");

                soilMoistPB.setVisibility(View.GONE);
                soilMoist.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                soilMoistPB.setVisibility(View.GONE);
                humPB.setVisibility(View.GONE);
                new AlertDialog.Builder(SoilMoistureActivity.this)
                        .setTitle("Oops!")
                        .setMessage("Something went wrong!")
                        .setNegativeButton("Cancel", null)
                        .setIcon(R.drawable.ic_alert)
                        .show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

