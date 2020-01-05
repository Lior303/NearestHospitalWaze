package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private static final String URL = "http://192.168.137.44:5000/";
    private static String coords[] = new String[2]; // first lat than lon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findHospital = findViewById(R.id.findHospital);
        findHospital.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //
                // find local position
                coords[0] =  "31.79300880432129";
                coords[1] = "34.64921951293945";

                //
                // post to server
                String post_url = URL + "?lat=" + coords[0] + "&lon=" + coords[1];
                http_get(post_url);

                //
                // get from server
                http_get(URL);

                //
                // start waze
                startWaze();
            }
        });
    }

    private void http_get(String URL) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Debug", response.toString());
                        try{
                            coords[0] = response.getString("lat");
                            coords[1] = response.getString("lon");
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
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(objectRequest);
    }

    private void get_location(){

    }

    private void startWaze(){
        String uri = "waze://?ll=" + coords[0] + ", " + coords[1] + "&navigate=yes";
        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }
}
