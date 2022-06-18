package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_button;
    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);
        result_info = findViewById(R.id.result_info);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                else {

                    String city = user_field.getText().toString().trim();
                    String key = "8236be500fb1619f49986793c55f8c8b";

                    // hourly weather
                    // https://api.openweathermap.org/data/2.5/onecall?lat=33.44&lon=-94.04&exclude=daily&appid=8236be500fb1619f49986793c55f8c8b

                    String url_weather = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    new GetURLDate().execute(url_weather);

                    Utils.hideKeyboard(MainActivity.this);
                }
            }
        });
    }

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

    private class GetURLDate extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
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
            if (result == null)
                result_info.setText("Неправильное навазние");
            else {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    // humidity - Влажиность в %

                    int wind = (int) Math.round(jsonObject.getJSONObject("wind").getDouble("speed"));
                    int feels_like = (int) Math.round(jsonObject.getJSONObject("main").getInt("feels_like"));
                    int temp = (int) Math.round(jsonObject.getJSONObject("main").getDouble("temp"));
                    String description = "";

                    JSONArray weather = jsonObject.getJSONArray("weather");
                    for (int i = 0; i < weather.length(); ++i) {
                        JSONObject rec = weather.getJSONObject(i);
                        description = rec.getString("description");
                    }

                    result_info.setText(description + " "
                            + temp + "°C"
                            + "\n" + "Ощущается как " + feels_like + "°C"
                            + "\n" + "Ветер: " + wind + " м/с");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}