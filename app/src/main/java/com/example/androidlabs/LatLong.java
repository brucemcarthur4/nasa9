package com.example.androidlabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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

import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class LatLong extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Get assets http address
    //{
    //     date: "2021-05-29T15:44:34.939000",
    //            id: "LANDSAT/LC08/C01/T1_SR/LC08_015029_20210529",
    //         resource: {
    //     dataset: "LANDSAT/LC08/C01/T1_SR",
    //             planet: "earth"
    // },
    //     service_version: "v5000",
    //             url: "https://earthengine.googleapis.com/v1alpha/projects/earthengine-legacy/thumbnails/4c5b7451697792cdf6bc8e54e7d3fc71-185486d3cc6db37f3a24221b68a1ba90:getPixels"
    //  }
    // new york 40.71452325335553, -74.00162703114904
    // my house 45.433685109297066, -75.69267672422043

    private static final String nasaKey = "MtVwZnEidfhi46N5UapE3FadxyQREdg9bXLC7C1h";
    // Get image http address
    private static final String NASA_IMAGE_URL = "https://api.nasa.gov/planetary/earth/imagery?lon=-75.69&lat=45.4336&date=2021-06-01&dim=.05&api_key=" + nasaKey;
    // Get assets http address
    private static final String NASA_ASSETS_URL = "https://api.nasa.gov/planetary/earth/assets?lon=-75.69&lat=45.4336&date=2021-06-01&dim=.05&api_key=" + nasaKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latlong);


        //  https://api.nasa.gov/planetary/earth/imagery?lon=-75.69&lat=45.4336&date=2021-06-01&dim=.05&api_key=DEMO_KEY


        // nasa api key
        // MtVwZnEidfhi46N5UapE3FadxyQREdg9bXLC7C1h
/////////////////////////////////////////////////////////

        //For toolbar:
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);


        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////////////////////

        // Start the progress bar
        ProgressBar bar = findViewById(R.id.progress_bar);
        bar.setVisibility(View.VISIBLE);
        bar.setProgress(0);

        // This is using a lambda that was covered in Module 2
        findViewById(R.id.get_sat_button).setOnClickListener((listener) -> {
            new ForecastQuery().execute();
        });


    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        String currentTemp, minTemp, maxTemp, icon,date = null;
        double uvRating;
        Bitmap satImage;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Setup everything on the activity
             ((TextView) findViewById(R.id.current_temp)).setText(getString(R.string.weather_current_temp, currentTemp));
            // ((TextView) findViewById(R.id.max_temp)).setText(getString(R.string.weather_max_temp, maxTemp));
            // ((TextView) findViewById(R.id.min_temp)).setText(getString(R.string.weather_min_temp, minTemp));
            // ((TextView) findViewById(R.id.uv_rating)).setText(getString(R.string.weather_uv_index, String.valueOf(uvRating)));
            ((ImageView) findViewById(R.id.sat_image)).setImageBitmap(satImage);

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
            InputStream response = null;
            InputStream is = null;


            try {

                response = new URL(NASA_ASSETS_URL).openConnection().getInputStream();
                // InputStream response = NASA_URL.getInputStream();

                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                    Log.i(this.getClass().getName(), "sb = " + line + "\n");
                }
                String result = sb.toString();




                Log.i(this.getClass().getName(), "JSON String = " + result);

                // convert string to json
                JSONObject nasadate = new JSONObject(result);

                currentTemp = nasadate.getString("slogan");



                // Get Image

                is = new URL(String.format(NASA_IMAGE_URL)).openConnection().getInputStream();
                satImage = BitmapFactory.decodeStream(is);


/*
                // Download the weather image if we don't already have it.
                Log.i(this.getClass().getName(), "Looking for " + icon);
                if (!fileExists(icon)) {
                    Log.i(this.getClass().getName(), "Downloading " + icon);
                    is = new URL(String.format(WEATHER_ICON_URL, icon)).openConnection().getInputStream();
                    satImage = BitmapFactory.decodeStream(is);

                    // Save it, using a try-with-resources will automatically close and flush
                    try (FileOutputStream fos = openFileOutput(icon, Context.MODE_PRIVATE)) {
                        satImage.compress(Bitmap.CompressFormat.PNG, 80, fos);
                    }
                } else {
                    Log.i(this.getClass().getName(), icon + " already exists");

                    // Load it from disk
                    try (FileInputStream fis = openFileInput(icon)) {
                        satImage = BitmapFactory.decodeStream(fis);
                    }
                }
*/
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


    /*
    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        String currentTemp, minTemp, maxTemp, icon = null;
        double uvRating;
        Bitmap satImage;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Setup everything on the activity
            ((TextView) findViewById(R.id.current_temp)).setText(getString(R.string.weather_current_temp, currentTemp));
            ((TextView) findViewById(R.id.max_temp)).setText(getString(R.string.weather_max_temp, maxTemp));
            ((TextView) findViewById(R.id.min_temp)).setText(getString(R.string.weather_min_temp, minTemp));
            ((TextView) findViewById(R.id.uv_rating)).setText(getString(R.string.weather_uv_index, String.valueOf(uvRating)));
            ((ImageView) findViewById(R.id.weather_icon)).setImageBitmap(satImage);

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
                    satImage = BitmapFactory.decodeStream(is);

                    // Save it, using a try-with-resources will automatically close and flush
                    try (FileOutputStream fos = openFileOutput(icon, Context.MODE_PRIVATE)) {
                        satImage.compress(Bitmap.CompressFormat.PNG, 80, fos);
                    }
                } else {
                    Log.i(this.getClass().getName(), icon + " already exists");

                    // Load it from disk
                    try (FileInputStream fis = openFileInput(icon)) {
                        satImage = BitmapFactory.decodeStream(fis);
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

*/

///////////////////////////////////////////////////////////////////////////////


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);


	    /* slide 15 material:
	    MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchView sView = (SearchView)searchItem.getActionView();
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }  });

	    */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.home:
                message = "You clicked Home";
                break;
            case R.id.global:
                message = "You clicked on Global";
                break;
            case R.id.list:
                message = "You clicked on List";
                // Go to Chat section
                Intent intent = new Intent(this, ChatRoomActivity.class);
                startActivity(intent);
                break;
            case R.id.mail:
                message = "You clicked on mail";
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }


    // Needed for the OnNavigationItemSelected interface:
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        String message = null;

        switch (item.getItemId()) {
            case R.id.list:
                //   message = "You clicked chatroom";
                // Go to Chat section
                Intent intent = new Intent(this, ChatRoomActivity.class);
                startActivity(intent);
                break;
            case R.id.global:
                //   message = "You clicked on the weather";
                // Go to Chat section
                Intent intent2 = new Intent(this, LatLong.class);
                startActivity(intent2);
                break;
            case R.id.home:
                //   message = "You clicked on the weather";
                // Go to Chat section
                Intent intent3 = new Intent(this, Home.class);
                startActivity(intent3);
                break;
            case R.id.mail:
                //   message = "You clicked on the weather";
                // Go to Chat section
                //  Intent intent4 = new Intent(this, Mail.class);
                //  startActivity(intent4);
                break;
            case R.id.options:
                //   message = "You clicked on go login";
                Intent intent5 = new Intent();
                intent5.putExtra("data2", "500");
                setResult(RESULT_OK, intent5);
                //  finish();
                //  finish();
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        // Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }


}