package com.example.trial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

public class TempActivity extends AppCompatActivity {

    //change number of points to be taken for average calculation here
    private final int numberOfDataPoints = 5;

    ImageButton weatherBtn;
    TextView temp, currTemp, minTemp, maxTemp;
    DatabaseReference db;
    ProgressBar tempPB;
    GraphView graphView;
    ArrayList<Float> dataPoints = new ArrayList(numberOfDataPoints);
    private LineGraphSeries<DataPoint> series;
    private LocationDetails locationDetails = new LocationDetails();
    String longitude, latitude;
    LocationDetails locationDetails2 = new LocationDetails();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Temperature and Weather");

        temp = findViewById(R.id.TempTV);
        tempPB = findViewById(R.id.tempPB);
        weatherBtn = findViewById(R.id.weatherConditionBtn);
        currTemp = findViewById(R.id.currTempTD);
        minTemp = findViewById(R.id.minTempTD);
        maxTemp = findViewById(R.id.maxTempTD);

        graphView = findViewById(R.id.idGraphView);
        graphView.setTitle("Temperature");
        graphView.setTitleColor(R.color.purple_200);
        graphView.setTitleTextSize(80);

        DatabaseReference messagesReference = FirebaseDatabase.getInstance().getReference().child("temperature");
        Query listQuery = messagesReference.orderByChild("temperature").limitToLast(numberOfDataPoints);
        listQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double sumOfDataPoints = 0.0;
                dataPoints.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    dataPoints.add(Float.parseFloat(snapshot.getValue().toString()));
                    sumOfDataPoints = sumOfDataPoints + Float.parseFloat(snapshot.getValue().toString());
                }
                sumOfDataPoints = sumOfDataPoints / numberOfDataPoints;
                graphView.removeAllSeries();
                series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                        new DataPoint(0, dataPoints.get(0)),
                        new DataPoint(1, dataPoints.get(1)),
                        new DataPoint(2, dataPoints.get(2)),
                        new DataPoint(3, dataPoints.get(3)),
                        new DataPoint(4, dataPoints.get(4)),
                });
                graphView.addSeries(series);

                String str = sumOfDataPoints + "\u00B0" + "C";
                temp.setText(str);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tempPB.setVisibility(View.GONE);
                new AlertDialog.Builder(TempActivity.this)
                        .setTitle("Oops!")
                        .setMessage("Something went wrong!")
                        .setNegativeButton("Cancel", null)
                        .setIcon(R.drawable.ic_alert)
                        .show();
            }
        });

        //current temp.
        DatabaseReference messagesReference1 = FirebaseDatabase.getInstance().getReference().child("longitude");
        Query listQuery1 = messagesReference1.orderByChild("longitude").limitToLast(1);

        DatabaseReference messagesReference2 = FirebaseDatabase.getInstance().getReference().child("latitude");
        Query listQuery2 = messagesReference2.orderByChild("latitude").limitToLast(1);


        listQuery1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String longitudeFromDB = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    longitudeFromDB = snapshot.getValue().toString();
                }
                longitude = longitudeFromDB;
                locationDetails2.setLongitude(longitudeFromDB);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new AlertDialog.Builder(TempActivity.this)
                        .setTitle("Oops!")
                        .setMessage("Something went wrong!")
                        .setNegativeButton("Cancel", null)
                        .setIcon(R.drawable.ic_alert)
                        .show();
            }
        });

        listQuery2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String latitudeFromDB = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    latitudeFromDB = snapshot.getValue().toString();
                }
                latitude = latitudeFromDB;
                locationDetails2.setLatitude(latitudeFromDB);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new AlertDialog.Builder(TempActivity.this)
                        .setTitle("Oops!")
                        .setMessage("Something went wrong!")
                        .setNegativeButton("Cancel", null)
                        .setIcon(R.drawable.ic_alert)
                        .show();
            }
        });


        //calling api
        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String apiKey = "53842094ffffc2c20836aad9ebcedc2d";

                String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=53842094ffffc2c20836aad9ebcedc2d";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("main");

                            String currTemperature = jsonObject.getString("temp");
                            String minTemperature = jsonObject.getString("temp_min");
                            String maxTemperature = jsonObject.getString("temp_max");

                            Double currTempVal = Double.parseDouble(currTemperature) - 273.15;
                            Double minTempVal = Double.parseDouble(minTemperature) - 273.15;
                            Double maxTempVal = Double.parseDouble(maxTemperature) - 273.15;


                            currTemp.setText(currTempVal.toString().substring(0, 5) + "\u00B0" + "C");
                            minTemp.setText(minTempVal.toString().substring(0, 5) + "\u00B0" + "C");
                            maxTemp.setText(maxTempVal.toString().substring(0, 5) + "\u00B0" + "C");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(TempActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                queue.add(request);

            }
        });
        tempPB.setVisibility(View.GONE);
        temp.setVisibility(View.VISIBLE);

    }


    private LocationDetails getLocationDetails() {
        return locationDetails2;
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
