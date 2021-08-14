package com.example.androidlabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class WeatherForecast extends AppCompatActivity {
    private static final String TEMP_URL = "https://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
    private static final String UV_URL = "https://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";
    private static final String WEATHER_ICON_URL = "https://openweathermap.org/img/w/%s";
    private static XmlPullParser xpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        // Start the progress bar
        ProgressBar bar = findViewById(R.id.progress_bar);
        bar.setVisibility(View.VISIBLE);
        bar.setProgress(0);

        // Setup the XML parser
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            xpp = factory.newPullParser();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }

        new ForecastQuery().execute();
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        String currentTemp, minTemp, maxTemp, icon = null;
        double uvRating;
        Bitmap weatherIcon;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Setup everything on the activity
            ((TextView) findViewById(R.id.current_temp)).setText(getString(R.string.weather_current_temp, currentTemp));
            ((TextView) findViewById(R.id.max_temp)).setText(getString(R.string.weather_max_temp, maxTemp));
            ((TextView) findViewById(R.id.min_temp)).setText(getString(R.string.weather_min_temp, minTemp));
            ((TextView) findViewById(R.id.uv_rating)).setText(getString(R.string.weather_uv_index, String.valueOf(uvRating)));
            ((ImageView) findViewById(R.id.weather_icon)).setImageBitmap(weatherIcon);

            // Hide the progress bar
            ((ProgressBar) findViewById(R.id.progress_bar)).setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ((ProgressBar) findViewById(R.id.progress_bar)).setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {
            InputStream is = null;
            try {
                // First retrieve the temperature information
                is = new URL(TEMP_URL).openConnection().getInputStream();
                xpp.setInput(is, null);

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("temperature")) {
                            publishProgress(25);
                            currentTemp = xpp.getAttributeValue(null, "value");
                            publishProgress(50);
                            maxTemp = xpp.getAttributeValue(null, "max");
                            publishProgress(75);
                            minTemp = xpp.getAttributeValue(null, "min");
                        } else if (xpp.getName().equals("weather")) {
                            icon = xpp.getAttributeValue(null, "icon") + ".png";
                        }
                    }
                    eventType = xpp.next();
                }

                // Now get the UV information
                is = new URL(UV_URL).openConnection().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                JSONObject uvReport = new JSONObject(sb.toString());
                uvRating = uvReport.getDouble("value");

                // Download the weather image if we don't already have it.
                Log.i(this.getClass().getName(), "Looking for " + icon);
                if (!fileExists(icon)) {
                    Log.i(this.getClass().getName(), "Downloading " + icon);
                    is = new URL(String.format(WEATHER_ICON_URL, icon)).openConnection().getInputStream();
                    weatherIcon = BitmapFactory.decodeStream(is);

                    // Save it, using a try-with-resources will automatically close and flush
                    try (FileOutputStream fos = openFileOutput(icon, Context.MODE_PRIVATE)) {
                        weatherIcon.compress(Bitmap.CompressFormat.PNG, 80, fos);
                    }
                } else {
                    Log.i(this.getClass().getName(), icon + " already exists");

                    // Load it from disk
                    try (FileInputStream fis = openFileInput(icon)) {
                        weatherIcon = BitmapFactory.decodeStream(fis);
                    }
                }
                publishProgress(100);
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage());
            }
            return null;
        }

        public boolean fileExists(String fname) {
            return getBaseContext().getFileStreamPath(fname).exists();
        }
    }
}