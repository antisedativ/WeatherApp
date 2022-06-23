package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdvancedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);

        TextView[] desc = new TextView[4];
        desc[0] = findViewById(R.id.day1_desc_adv);
        desc[1] = findViewById(R.id.day2_desc_adv);
        desc[2] = findViewById(R.id.day3_desc_adv);
        desc[3] = findViewById(R.id.day4_desc_adv);

        ImageView[] icons = new ImageView[4];
        icons[0] = findViewById(R.id.day1_icon_adv);
        icons[1] = findViewById(R.id.day2_icon_adv);
        icons[2] = findViewById(R.id.day3_icon_adv);
        icons[3] = findViewById(R.id.day4_icon_adv);

        Button back_btn = findViewById(R.id.back_btn_adv);

        // Слушатель перехода назад
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackActivity();
            }});

        String key = "8236be500fb1619f49986793c55f8c8b";
        String lon = getIntent().getStringExtra("lon");
        String lat = getIntent().getStringExtra("lat");


         String url_coords = "https://api.openweathermap.org/data/2.5/onecall?lat="+ lat+ "&lon=" + lon + "&exclude=minutely,hourly&appid=" + key + "&units=metric&lang=ru";
         //new GetURLDate().execute(url_coords);

    }

    private class GetURLDate extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);


                    String date = "";
                    JSONArray daily = jsonObject.getJSONArray("daily");
                    for (int i = 0; i < daily.length(); ++i) {
                        JSONObject rec = daily.getJSONObject(i);
                        date = rec.getString("dt");
                    }

                    String description = "";
                    String icon_weather = "";

                    JSONArray weather = jsonObject.getJSONArray("weather");
                    for (int i = 0; i < daily.length(); ++i) {
                        JSONObject rec = daily.getJSONObject(i);
                        description = rec.getString("description");
                        icon_weather = rec.getString("icon");
                    }

                    int humidity = (int) Math.round(jsonObject.getJSONObject("main").getDouble("humidity"));
                    int wind = (int) Math.round(jsonObject.getJSONObject("wind").getDouble("speed"));
                    int feels_like = (int) Math.round(jsonObject.getJSONObject("main").getInt("feels_like"));
                    int temp = (int) Math.round(jsonObject.getJSONObject("main").getDouble("temp"));




                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }


    public void BackActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}


