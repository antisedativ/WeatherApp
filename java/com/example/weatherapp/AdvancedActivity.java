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
import java.util.HashMap;

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

                    HashMap<String, Integer> n = new HashMap<>();
                    n.put("01", R.drawable.w01n);
                    n.put("02", R.drawable.w02n);
                    n.put("03", R.drawable.w03n);
                    n.put("04", R.drawable.w04n);
                    n.put("09", R.drawable.w09n);
                    n.put("10", R.drawable.w10n);
                    n.put("11", R.drawable.w11n);
                    n.put("13", R.drawable.w13n);
                    n.put("50", R.drawable.w50n);

                    HashMap<String, Integer> d = new HashMap<>();
                    d.put("01", R.drawable.w01d);
                    d.put("02", R.drawable.w02d);
                    d.put("03", R.drawable.w03d);
                    d.put("04", R.drawable.w04d);
                    d.put("09", R.drawable.w09d);
                    d.put("10", R.drawable.w10d);
                    d.put("11", R.drawable.w11d);
                    d.put("13", R.drawable.w13d);
                    d.put("50", R.drawable.w50d);


                    for(int i = 0; i < icons.length; ++i) {

                        String icon_weather_last_letter = icon_weather[i].substring(icon_weather[i].length() - 1);
                        String rest_icon_weather = icon_weather[i].substring(0, icon_weather[i].length() - 1);

                        if(icon_weather_last_letter.equals("n"))
                            icons[i].setImageResource(n.get(rest_icon_weather));
                        else if(icon_weather_last_letter.equals("d"))
                            icons[i].setImageResource(d.get(rest_icon_weather));
                        else
                            icons[i].setImageDrawable(null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
}


