package com.example.androidlabs;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


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

    // Database setup

  //  ImageList.MyOwnAdapter myAdapter;
    private String theType;
    private String theMessage;

    SQLiteDatabase db;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latlong);

        //get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        // Attempting to change header text in navigation view
        //   NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //  View header = navigationView.getHeaderView(0);
        //  TextView name = header.findViewById(R.id.name_text_view);
        //  TextView email = header.findViewById(R.id.email_text_view);
        //  TextView contact = header.findViewById(R.id.contact_text_view);
        //  name.setText(myname);
        //  email.setText(myemail);
        //   contact.setText(mycontact);

        // **********************************************************************
        // Toolbar code
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
        // **********************************************************************

        // Start the progress bar
        ProgressBar bar = findViewById(R.id.progress_bar);
        bar.setVisibility(View.VISIBLE);
        bar.setProgress(0);

       // Get Satellite Image Button Listener
        findViewById(R.id.get_sat_button).setOnClickListener((listener) -> {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.searching), Toast.LENGTH_SHORT).show();
            //  bar.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.image_date)).setText("");
            ((TextView) findViewById(R.id.image_lat)).setText("");
            ((TextView) findViewById(R.id.image_long)).setText("");
            bar.setProgress(0);
            new NasaQuery().execute();
        });

      //   Save Image To database
        findViewById(R.id.submit_button).setOnClickListener((listener) -> {
            saveImage();
        });

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Query Nasa API with AsyncTask
    private class NasaQuery extends AsyncTask<String, Integer, String> {
        String NASA_IMAGE_URL, NASA_ASSETS_URL, imageLat, imageLong, imageDate, theDate = null;
        double uvRating;
        Bitmap satImage;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Setup everything on the activity
            ((TextView) findViewById(R.id.image_date)).setText(getString(R.string.image_date, imageDate));
            ((TextView) findViewById(R.id.image_lat)).setText(getString(R.string.image_lat, imageLat));
            ((TextView) findViewById(R.id.image_long)).setText(getString(R.string.image_long, imageLong));
            // Hide the progress bar
            // ((ProgressBar) findViewById(R.id.progress_bar)).setVisibility(View.INVISIBLE);
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
            EditText mEdit;
            theDate = "2021-04-01";

            // Get lat from inpout field
            mEdit = (EditText) findViewById(R.id.lat_enter);
            imageLat = mEdit.getText().toString();
            // Get long from inpout field
            mEdit = (EditText) findViewById(R.id.long_enter);
            imageLong = mEdit.getText().toString();

            Log.i(this.getClass().getName(), "lat =" + imageLat);
            Log.i(this.getClass().getName(), "long =" + imageLong);

            // Check input
            if (imageLat.isEmpty() || imageLong.isEmpty()) {
                Log.i(this.getClass().getName(), "lat or long empty");
                // Find view to pass for snackbar show if lat or long fields empty
                View view;
                view = (View) findViewById(R.id.lat_enter);
                Snackbar.make(view, getResources().getString(R.string.latorlongempty), 3000).show();

                // finish(); // goes to splash

            }

            String nasaKey = "MtVwZnEidfhi46N5UapE3FadxyQREdg9bXLC7C1h";
            // Get image http address
            NASA_IMAGE_URL = "https://api.nasa.gov/planetary/earth/imagery?lon=" + imageLong + "&lat=" + imageLat + "&date=" + theDate + "&dim=.05&api_key=" + nasaKey;
            // Get assets http address
            NASA_ASSETS_URL = "https://api.nasa.gov/planetary/earth/assets?lon=" + imageLong + "&lat=" + imageLat + "&date=" + theDate + "&api_key=" + nasaKey;

            //  "https://api.nasa.gov/planetary/earth/assets?lon=15.00&lat=4.5&date=2014-02-01&api_key=MtVwZnEidfhi46N5UapE3FadxyQREdg9bXLC7C1h"
            //   "https://api.nasa.gov/planetary/earth/assets?lon=10.75&lat=1.5&date=2014-02-01&api_key=MtVwZnEidfhi46N5UapE3FadxyQREdg9bXLC7C1h"

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

                imageDate = nasadate.getString("date");


                // Get Image
                is = new URL(String.format(NASA_IMAGE_URL)).openConnection().getInputStream();
                satImage = BitmapFactory.decodeStream(is);

                ImageView simage = (ImageView) findViewById(R.id.sat_image);
                simage.setImageBitmap(satImage);

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



    private void saveImage() {

        // Get text from Input
       TextView theDate1 = findViewById(R.id.image_date);
        String theDate = theDate1.getText().toString();

        TextView theLat1 = findViewById(R.id.image_lat);
        String theLat = theLat1.getText().toString();

        TextView theLong1 = findViewById(R.id.image_long);
        String theLong = theLong1.getText().toString();

        // Get image from ImageView?
       // ImageView theImage1 = findViewById(R.id.sat_image);
      //  Image theImage = theImage1.getText().toString();



        // Check if there is any text to send or recieve
        if (theDate == null || theDate.length() == 0) {
            return;
        }
        //add to the database and get the new ID
        ContentValues newRowValues = new ContentValues();

        //Now provide a value for every database column defined in MyOpener.java:

        // Need check to see if added to database already ****

        //put string finalmessage in the MESSAGE column:
        newRowValues.put(MyOpener.COL_MESSAGE, theDate);
        //put string messagetype in the TYPE column:
        String messagetype = "r";
        newRowValues.put(MyOpener.COL_TYPE, messagetype);

        //put string finalmessage in the LAT column:
        newRowValues.put(MyOpener.COL_LAT, theLat);
        //put string finalmessage in the MESSAGE column:
        newRowValues.put(MyOpener.COL_LONG, theLong);

        //Now insert in the database:
        long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);
        // long newId = 1;

        //now you have the newId, you can create the Contact object for our list adapter
        Message newmessage = new Message(messagetype, theDate, newId);

        //Show the id of the inserted item:
        Toast.makeText(this, "Inserted item id:" + newId, Toast.LENGTH_LONG).show();
    }




// **********************************************************************
// Toolbar code

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch (item.getItemId()) {
            case R.id.list:
                Intent intent1 = new Intent(this, ImageList.class);
                startActivity(intent1);
                break;
            case R.id.global:
                Intent intent2 = new Intent(this, LatLong.class);
                startActivity(intent2);
                break;
            case R.id.home:
                Intent intent3 = new Intent(this, Home.class);
                startActivity(intent3);
                break;
            case R.id.mail:
                Intent intent4 = new Intent(this, Mail.class);
                startActivity(intent4);
                break;

            case R.id.help:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.alert_latlong_title)).setMessage(getResources().getString(R.string.alert_latlong))
                        .setPositiveButton("Ok", (click, arg) -> {
                        })
                        // .setNegativeButton("No", (click, arg) -> { })
                        // .setNeutralButton("Maybe", (click, arg) -> { })
                        .create().show();

                break;


            case R.id.options:
                Intent intent5 = new Intent();
                intent5.putExtra("data2", "500");
                setResult(RESULT_OK, intent5);
                //  finish(); // gos to previous activity
                break;
        }
        return true;
    }

    // Needed for the OnNavigationItemSelected interface:
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        String message = null;

        switch (item.getItemId()) {
            case R.id.list:
                Intent intent1 = new Intent(this, ImageList.class);
                startActivity(intent1);

                break;
            case R.id.global:
                Intent intent2 = new Intent(this, LatLong.class);
                startActivity(intent2);
                break;
            case R.id.home:
                Intent intent3 = new Intent(this, Home.class);
                startActivity(intent3);
                break;
            case R.id.mail:
                Intent intent4 = new Intent(this, Mail.class);
                startActivity(intent4);
                break;
            case R.id.options:
                Intent intent5 = new Intent();
                intent5.putExtra("data2", "500");
                setResult(RESULT_OK, intent5);
                //  finish(); // kills app
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        // Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }
// **********************************************************************

} // end class


// How to parse XML and store previously downloaded image so in cache

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