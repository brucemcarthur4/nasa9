package com.example.androidlabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;


public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SharedPreferences prefs = null;


    private ProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the splash screen
        setContentView(R.layout.activity_home);


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

            case R.id.help:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.alert_home_title)).setMessage(getResources().getString(R.string.alert_home))
                        .setPositiveButton("Ok", (click, arg) -> {
                        })
                        // .setNegativeButton("No", (click, arg) -> { })
                        // .setNeutralButton("Maybe", (click, arg) -> { })
                        .create().show();

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

