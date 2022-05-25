package com.example.trial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LongLatActivity extends AppCompatActivity {

    TextView longitudeTextView, latitudeTextView, currWeatherStatus;
    Button mapBtn;
    ImageButton weatherBtn;
    String latitude, longitude;
    LocationDetails locationDetails1 = new LocationDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_lat);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Longitude & Latitude");

        longitudeTextView = findViewById(R.id.longTV);
        latitudeTextView = findViewById(R.id.latTV);
        currWeatherStatus = findViewById(R.id.currWeatherTD);
        weatherBtn = findViewById(R.id.weatherBtn);
        mapBtn = findViewById(R.id.mapBtn);

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
                locationDetails1.setLongitude(longitudeFromDB);
                longitudeTextView.setText("Longitude : " + longitudeFromDB);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new AlertDialog.Builder(LongLatActivity.this)
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
                locationDetails1.setLatitude(latitudeFromDB);
                latitudeTextView.setText("Latitude : " + latitudeFromDB);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new AlertDialog.Builder(LongLatActivity.this)
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
                String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=53842094ffffc2c20836aad9ebcedc2d";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray obj = response.getJSONArray("weather");
                            JSONObject a = (JSONObject) obj.get(0);
                            String currWeather = a.getString("description");
                            currWeatherStatus.setText(currWeather);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LongLatActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                queue.add(request);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + latitude + "," + longitude));
                startActivity(intent);
            }
        });

    }

    private LocationDetails getLocationDetails() {
        return locationDetails1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            // back button
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
