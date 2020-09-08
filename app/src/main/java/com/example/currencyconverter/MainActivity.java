package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText input_value;
    private Spinner spinner_fromCntry,spinner_toCntry;
    private Button submit_btn, refresh_btn;
    private TextView value_displayTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_value = findViewById(R.id.input_value);
        spinner_fromCntry = findViewById(R.id.spinner_fromCntry);
        spinner_toCntry = findViewById(R.id.spinner_toCntry);
        submit_btn = findViewById(R.id.submit_btn);
        value_displayTV = findViewById(R.id.value_displayTV);
        refresh_btn = findViewById(R.id.refresh_btn);


        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            connected = true;
        }
        else
            connected = false;

        if(connected)
        {
            try{
                loadConvTypes();
            }catch(IOException e){
                e.printStackTrace();
            }

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    input_value.onEditorAction(EditorInfo.IME_ACTION_DONE);//To remove android keyboard
                    if(!input_value.getText().toString().isEmpty()){

                        String from_country = spinner_fromCntry.getSelectedItem().toString();
                        String to_country = spinner_toCntry.getSelectedItem().toString();
                        double value = Double.valueOf(input_value.getText().toString());

                        currencyConversion(from_country,to_country,value);

                    }else{
                        Toast.makeText(getApplicationContext(),"Enter value",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            refresh_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    startActivity(getIntent());
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"Please Check Internet Connectivity",Toast.LENGTH_LONG).show();
        }
    }

    private void currencyConversion(final String from_country, final String to_country, final double value) {
        String URL = "https://api.exchangeratesapi.io/latest?base="+from_country+"&symbols="+to_country;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("checkresponse", "exception == " + response);
                try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject  b = obj.getJSONObject("rates");

                        String temp = b.getString(to_country);
                        Double exchange_rate = Double.parseDouble(temp);
                        Double final_value = value*exchange_rate;
                        value_displayTV.setText(final_value.toString());

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(MainActivity.this).add(stringRequest);
    }

    private void loadConvTypes() throws IOException{
        String URL = "https://api.exchangeratesapi.io/latest";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("checkresponse", "exception == " + response);
                try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject b = obj.getJSONObject("rates");
                        Iterator keysToCopyIterator = b.keys();
                        ArrayList<String> countries_list = new ArrayList<String>();

                        while (keysToCopyIterator.hasNext()){
                            String key = (String)keysToCopyIterator.next();
                            countries_list.add(key);
                        }
                        countries_list.add("EUR");
                        Collections.sort(countries_list);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,countries_list);
                        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        spinner_fromCntry.setAdapter(adapter);

                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,countries_list);
                        adapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        spinner_toCntry.setAdapter(adapter1);

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(MainActivity.this).add(stringRequest);
    }
}