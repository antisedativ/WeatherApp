package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Для текущего активити
    private EditText user_field;
    private Button more_inf_btn;
    private Button main_button;
    private Button exten_btn;
    private TextView result_info;
    private TextView exceptions_info;
    private TextView description_info;
    private TextView coordinates;
    private ImageView current_weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);
        result_info = findViewById(R.id.result_info);
        current_weather = findViewById(R.id.current_weather);
        exceptions_info = findViewById(R.id.exceptions_info);
        description_info = findViewById(R.id.description_info);
        coordinates = findViewById(R.id.coordinates);
        more_inf_btn = findViewById(R.id.more_information_button);
        exten_btn = findViewById(R.id.extended_day_button);

        exten_btn.setVisibility(View.INVISIBLE);
        more_inf_btn.setVisibility(View.INVISIBLE);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                else {

                    String city = user_field.getText().toString().trim();
                    String key = "8236be500fb1619f49986793c55f8c8b";

                    Utils.hideKeyboard(MainActivity.this);

                    String url_weather = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";
                    new GetURLDate().execute(url_weather);

                    more_inf_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Next_AdvancedActivity(coordinates);
                        }});

                    exten_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Next_MoreToday(user_field);
                        }});
                }
            }
        });
    }

    public void Next_AdvancedActivity(View view) {
        Intent intent = new Intent(this, AdvancedActivity.class);

        String lon = "", lat = "";
        if(coordinates != null) {
            String coords = "";
            coords = coordinates.getText().toString();
            lon = coords.split(" ", 2)[0];
            lat = coords.replaceAll(".* ", "");
        }

        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);

        startActivity(intent);
    }

    public void Next_MoreToday(View view) {

        Intent intent = new Intent(this, MoreToday.class);
        String city = user_field.getText().toString().trim();
        intent.putExtra("city", city);

        startActivity(intent);
    }

    // Скрывает клавиатуру после нажатия на кнопку
    public static class Utils {
        public static void hideKeyboard(@NonNull Activity activity) {
            // Check if no view has focus:
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    // Асинхронный переход на сайт с данными
    private class GetURLDate extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            exceptions_info.setText("Ожидайте...");
        }

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
            if (result == null) {
                exceptions_info.setText("Неправильное навазние");
                current_weather.setImageDrawable(null);
                description_info.setText(null);
                result_info.setText(null);
                coordinates.setText(null);
                more_inf_btn.setVisibility(View.INVISIBLE);
                exten_btn.setVisibility(View.INVISIBLE);
            }
            else {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String description = "";
                    String icon_weather = "";

                    JSONArray weather = jsonObject.getJSONArray("weather");
                    for (int i = 0; i < weather.length(); ++i) {
                        JSONObject rec = weather.getJSONObject(i);
                        description = rec.getString("description");
                        icon_weather = rec.getString("icon");
                    }
                    exceptions_info.setText(null);

                    // Достаем координаты введенного города
                    float lon = (float) jsonObject.getJSONObject("coord").getDouble("lon");
                    float lat = (float) jsonObject.getJSONObject("coord").getDouble("lat");
                    coordinates.setText(lon+" "+lat);

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

                    String icon_weather_last_letter = icon_weather.substring(icon_weather.length() - 1);
                    String rest_icon_weather = icon_weather.substring(0, icon_weather.length() - 1);

                    if(icon_weather_last_letter.equals("n"))
                        current_weather.setImageResource(n.get(rest_icon_weather));
                    else if(icon_weather_last_letter.equals("d"))
                        current_weather.setImageResource(d.get(rest_icon_weather));
                    else
                        current_weather.setImageDrawable(null);

                    int humidity = (int) Math.round(jsonObject.getJSONObject("main").getDouble("humidity"));
                    int wind = (int) Math.round(jsonObject.getJSONObject("wind").getDouble("speed"));
                    int feels_like = (int) Math.round(jsonObject.getJSONObject("main").getInt("feels_like"));
                    int temp = (int) Math.round(jsonObject.getJSONObject("main").getDouble("temp"));


                    description_info.setText("Ощущается как " + feels_like + "°C"
                            + "\n" + "Ветер " + wind + " м/с"
                            + "\n" + "Влажность " + humidity + "%");


                    result_info.setText(description + " "+ temp + "°C");
                    exten_btn.setVisibility(View.VISIBLE);
                    more_inf_btn.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
