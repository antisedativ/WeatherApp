package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class MoreToday extends AppCompatActivity {

    private TextView current_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_today);

        Button back_btn = findViewById(R.id.back_btn_today);
        current_desc = findViewById(R.id.current_desc);

        // Слушатель перехода назад
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackActivity();
            }});

        String key = "8236be500fb1619f49986793c55f8c8b";
        String city = getIntent().getStringExtra("city");
        String url_weather = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";
        new GetURLDate().execute(url_weather);
    }

    public void BackActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Асинхронный переход на сайт с данными
    @SuppressLint("StaticFieldLeak")
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

                    int humidity = (int) jsonObject.getJSONObject("main").getDouble("humidity");
                    int wind = (int) Math.round(jsonObject.getJSONObject("wind").getDouble("speed"));
                    int feels_like = (int) Math.round(jsonObject.getJSONObject("main").getInt("feels_like"));
                    int temp = (int) Math.round(jsonObject.getJSONObject("main").getDouble("temp"));

                    int temp_min = (int) Math.round(jsonObject.getJSONObject("main").getDouble("temp_min"));
                    int temp_max = (int) Math.round(jsonObject.getJSONObject("main").getDouble("temp_max"));
                    int pressure = (int) jsonObject.getJSONObject("main").getDouble("pressure");
                    int gust = (int) Math.round(jsonObject.getJSONObject("wind").getDouble("gust"));

                // Попробовать скрыть текст вью на главном экране и передать его на это активити
                    current_desc.setText("DA");
//                    current_desc.setText("Температура "+ temp+ "°C\n"
//                            + "Ощущается как " +feels_like + "°C\n"
//                            + "Максимальная темаратура за сегодня " + temp_max + "°C\n"
//                            + "Минимальная темаратура за сегодня " + temp_min + "°C\n"
//                            + "Влажность " + humidity + "%\n"
//                            + "Ветер " + wind + " м/с с порывами до "
//                            + gust + "м/с\n"
//                            + "Атмосферное давление" + pressure + "мм рт. ст.");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

}