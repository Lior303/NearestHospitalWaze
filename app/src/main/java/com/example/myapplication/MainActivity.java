package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonIOException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button findHospital;
    private Button getHelp;
    private Button about;
    private static final String URL = "http://10.200.201.111:5000/";
    private static String coords[] = new String[2]; // first lat than lon
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        about = findViewById(R.id.about);
        getHelp = findViewById(R.id.getHelp);

        getHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Nearest Hospital with Waze");
                alertDialog.setMessage("This application find the nearest hospital (in terms of ETA) from the user's" +
                        " current location. Than, the application starts Waze navigation towards this hospital.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Nearest Hospital with Waze");
                alertDialog.setMessage("This application was created by Lior Vaknin and Mordy Dabah, " +
                        "as part of the course Geolocation and IoT.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        findHospital = findViewById(R.id.findHospital);
        findHospital.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                requestQueue = Volley.newRequestQueue(MainActivity.this);
                Toast.makeText(MainActivity.this, "Start calculating...", Toast.LENGTH_LONG).show();

                if (checkCoarseLocationPermission()){
                    main_code();
                }
                else{
                    Toast.makeText(MainActivity.this, "Course location permission is not granted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void http_get(String URL) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Debug", response.toString());
                        try{

                            //
                            // get the coordinates of the nearest hospital
                            coords[0] = response.getString("lat");
                            coords[1] = response.getString("lon");

                            //
                            // start waze
                            startWaze();
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Debug", error.toString());
                    }
                }
        );
        objectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {}
        });
        requestQueue.add(objectRequest);
    }

    private void startWaze(){
        String uri = "waze://?ll=" + coords[0] + ", " + coords[1] + "&navigate=yes";
        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
            return false;
        }
        return true;
    }

    private void main_code(){

        findHospital.setEnabled(false);

        //
        // get current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            coords[0] = Double.toString(location.getLatitude());
                            coords[1] = Double.toString(location.getLongitude());

                            //
                            // post to server
                            String post_url = URL + "?lat=" + coords[0] + "&lon=" + coords[1];
                            http_get(post_url);
                        }
                    }
                });
    }
}
