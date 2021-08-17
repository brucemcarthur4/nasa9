package com.example.androidlabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;



public class TestToolbar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
        //For toolbar:
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);


        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar,R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


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
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.home:
                message = "You clicked Home";
                break;
            case R.id.global:
                message = "You clicked on Global";
                break;
            case R.id.list:
                message = "You clicked on List";
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
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {
            case R.id.list:
             //   message = "You clicked chatroom";
                // Go to Chat section
                Intent intent = new Intent(TestToolbar.this, ImageList.class);
                startActivity(intent);
                break;
            case R.id.global:
             //   message = "You clicked on the weather";
                // Go to Chat section
                Intent intent2 = new Intent(TestToolbar.this, LatLong.class);
                startActivity(intent2);
                break;
            case R.id.home:
                //   message = "You clicked on the weather";
                // Go to Chat section
                Intent intent3 = new Intent(TestToolbar.this, LatLong.class);
                startActivity(intent3);
                break;
            case R.id.mail:
                //   message = "You clicked on the weather";
                // Go to Chat section
                Intent intent4 = new Intent(TestToolbar.this, LatLong.class);
                startActivity(intent4);
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
