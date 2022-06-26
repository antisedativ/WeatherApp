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
    private TextView d1_desc;
    private TextView d2_desc;
    private TextView d3_desc;
    private TextView d4_desc;

    private ImageView d1_icon;
    private ImageView d2_icon;
    private ImageView d3_icon;
    private ImageView d4_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);

        d1_desc = findViewById(R.id.day1_desc_adv);
        d2_desc= findViewById(R.id.day2_desc_adv);
        d3_desc = findViewById(R.id.day3_desc_adv);
        d4_desc = findViewById(R.id.day4_desc_adv);

        d1_icon = findViewById(R.id.day1_icon_adv);
        d2_icon = findViewById(R.id.day2_icon_adv);
        d3_icon = findViewById(R.id.day3_icon_adv);
        d4_icon = findViewById(R.id.day4_icon_adv);

        Button back_btn = findViewById(R.id.back_btn_adv);

        d1_icon.setImageDrawable(null);
        d2_icon.setImageDrawable(null);
        d3_icon.setImageDrawable(null);
        d4_icon.setImageDrawable(null);

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
         new GetURLDate().execute(url_coords);
    }
    public void BackActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public String Date(String dt) {

        int unix_seconds = Integer.parseInt(dt);

        java.util.Date time = new java.util.Date((long)unix_seconds*1000);
        return time.toString().substring(4, 10);
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

                    JSONArray daily = jsonObject.getJSONArray("daily");

                    int[] tempDay = new int[daily.length()];
                    int[] tempNight = new int[daily.length()];
                    int[] wind_speed = new int[daily.length()];
                    int[] humidity = new int[daily.length()];

                    String[] date = new String[daily.length()];
                    String[] description = new String[daily.length()];
                    String[] icon_weather = new String[daily.length()];

                    for (int i = 0; i < daily.length(); ++i) {
                        JSONObject rec = daily.getJSONObject(i);
                        date[i]= rec.getString("dt");

                        tempDay[i] = (int) Math.round(rec.getJSONObject("temp").getDouble("day"));
                        tempNight[i] = (int) Math.round(rec.getJSONObject("temp").getDouble("night"));
                        wind_speed[i] = (int) Math.round(rec.getDouble("wind_speed"));
                        humidity[i] = (int) Math.round(rec.getDouble("humidity"));

                        JSONArray weather = rec.getJSONArray("weather");

                        for (int j = 0; j < weather.length(); ++j) {
                            JSONObject cw = weather.getJSONObject(j);
                            description[i] = cw.getString("description");
                            icon_weather[i] = cw.getString("icon");
                        }
                    }

                    TextView[] desc = new TextView[4];
                    desc[0] = d1_desc;
                    desc[1] = d2_desc;
                    desc[2] = d3_desc;
                    desc[3] = d4_desc;

                    for(int i = 1; i <= desc.length; ++i) {
                        desc[i-1].setText(Date(date[i]) + " " + description[i]
                                + "\nТемпература днём " + tempDay[i] + "°C"
                                + "\nТемпература ночью " + tempNight[i] + "°C"
                                + "\nВетер " + wind_speed[i] + "м/с"
                                + "\nВлажность " + humidity[i] + "%");
                    }

                    ImageView[] icons = new ImageView[4];
                    icons[0] = d1_icon;
                    icons[1] = d2_icon;
                    icons[2] = d3_icon;
                    icons[3] = d4_icon;

                    for(int i = 0; i < icons.length; ++i) {
                        switch (icon_weather[i]) {
                            case "01d":
                                icons[i].setImageResource(R.drawable.w01d);
                                break;
                            case "01n":
                                icons[i].setImageResource(R.drawable.w01n);
                                break;
                            case "02d":
                                icons[i].setImageResource(R.drawable.w02d);
                                break;
                            case "02n":
                                icons[i].setImageResource(R.drawable.w02n);
                                break;
                            case "03n":
                                icons[i].setImageResource(R.drawable.w03n);
                                break;
                            case "03d":
                                icons[i].setImageResource(R.drawable.w03d);
                                break;
                            case "04n":
                                icons[i].setImageResource(R.drawable.w04n);
                                break;
                            case "04d":
                                icons[i].setImageResource(R.drawable.w04d);
                                break;
                            case "09n":
                                icons[i].setImageResource(R.drawable.w09n);
                                break;
                            case "09d":
                                icons[i].setImageResource(R.drawable.w09d);
                                break;
                            case "10n":
                                icons[i].setImageResource(R.drawable.w10n);
                                break;
                            case "10d":
                                icons[i].setImageResource(R.drawable.w10d);
                                break;
                            case "11n":
                                icons[i].setImageResource(R.drawable.w11n);
                                break;
                            case "11d":
                                icons[i].setImageResource(R.drawable.w11d);
                                break;
                            case "13d":
                                icons[i].setImageResource(R.drawable.w13d);
                                break;
                            case "13n":
                                icons[i].setImageResource(R.drawable.w13n);
                                break;
                            case "50d":
                                icons[i].setImageResource(R.drawable.w50d);
                                break;
                            case "50n":
                                icons[i].setImageResource(R.drawable.w50n);
                                break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
}


