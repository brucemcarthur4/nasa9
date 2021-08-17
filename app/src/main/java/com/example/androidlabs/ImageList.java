package com.example.androidlabs;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Arrays;

public class ImageList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // private ArrayList<String> elements = new ArrayList<>( Arrays.asList( "one", "Two"/*Empty*/ ) );

    ArrayList<Message> elements = new ArrayList<Message>();
    MyOwnAdapter myAdapter;
    private String theType;
    private String theMessage;
    SQLiteDatabase db;

    public static final String ITEM_TYPE = "TYPE";
    public static final String ITEM_MESSAGE = "MESSAGE";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        //Get the fields from the screen:
      //  EditText themessage = findViewById(R.id.message);
      //  Button sendButton = (Button) findViewById(R.id.button_send);
      //  Button recieveButton = (Button) findViewById(R.id.button_receive);
        ListView theList = findViewById(R.id.theListView);


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



        //create an adapter object and send it to the listVIew
        myAdapter = new MyOwnAdapter();
        theList.setAdapter(myAdapter);

        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded


        loadDataFromDatabase(); //get any previously saved Contact objects


        // onClick Send Button
       // sendButton.setOnClickListener((listener) -> {
       //     Log.e("button", " clicked send");
       //     doMessage("s");
      //  });
        // onClick Receive Button
      //  recieveButton.setOnClickListener((listener) -> {
       //     Log.e("button", " clicked receive");
      //      doMessage("r");
      //  });


        //This listens for items being clicked in the list view
        theList.setOnItemLongClickListener((parent, view, position, id) -> {
            // for editing entry
            showMessage(position);
            return true;
        });


        theList.setOnItemClickListener((parent, view, position, id) -> {


            Message selectedMessage = elements.get(position);
            // rowMessage.setText(selectedMessage.getMessage());

            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TYPE, selectedMessage.getType());
            dataToPass.putString(ITEM_MESSAGE, selectedMessage.getMessage());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, selectedMessage.getId());

            if (isTablet) {
                DetailFragment dFragment = new DetailFragment(); //add a DetailFragment
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment,"targetfrag") //Add the fragment in FrameLayout
                        .addToBackStack("sourcefrag") // add to backstack
                        .commit(); //actually load the fragment.
            } else //isPhone
            {
                Intent nextActivity = new Intent(ImageList.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }

        });

    }

    /*
    private void doMessage(String messagetype) {

        // Get text from Input
        EditText themessage = findViewById(R.id.message);
        String finalmessage = themessage.getText().toString();

        // Check if there is any text to send or recieve
        if (finalmessage == null || finalmessage.length() == 0) {
            return;
        }

        //add to the database and get the new ID
        ContentValues newRowValues = new ContentValues();

        //Now provide a value for every database column defined in MyOpener.java:
        //put string messagetype in the TYPE column:
        newRowValues.put(MyOpener.COL_TYPE, messagetype);
        //put string finalmessage in the MESSAGE column:
        newRowValues.put(MyOpener.COL_MESSAGE, finalmessage);

        //Now insert in the database:
        long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);
        // long newId = 1;

        //now you have the newId, you can create the Contact object for our list adapter
        Message newmessage = new Message(messagetype, finalmessage, newId);

        //add the new contact to the list:
        elements.add(newmessage);
        //update the listView:
        myAdapter.notifyDataSetChanged();

        // Clear input
        final EditText message = (EditText) findViewById(R.id.message);
        // message.setText("");
        message.getText().clear();

        //Show the id of the inserted item:
        Toast.makeText(this, "Inserted item id:" + newId, Toast.LENGTH_LONG).show();
    }
*/

    private void loadDataFromDatabase() {
        //get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String[] columns = {MyOpener.COL_ID, MyOpener.COL_MESSAGE, MyOpener.COL_TYPE, MyOpener.COL_LAT, MyOpener.COL_LONG};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);


        //Now the results object has rows of results that match the query.
        //find the column indices:
        int messageColumnIndex = results.getColumnIndex(MyOpener.COL_MESSAGE);
        int typeColIndex = results.getColumnIndex(MyOpener.COL_TYPE);
        int idColIndex = results.getColumnIndex(MyOpener.COL_ID);
        int latIndex = results.getColumnIndex(MyOpener.COL_LAT);
        int longIndex = results.getColumnIndex(MyOpener.COL_LONG);

        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            String type = results.getString(typeColIndex);
            String message = results.getString(messageColumnIndex);
            long id = results.getLong(idColIndex);
            String lat = results.getString(latIndex);
            String long2 = results.getString(longIndex);

            //add the new Message to the array list:
            elements.add(new Message(type, message,id));
        }


        // Print cursor details
        printCursor(results, (db.getVersion()));

        //At this point, the contactsList array has loaded every row from the cursor.
    }

    // Print out Database and cursor details
    public void printCursor(Cursor c, int version) {
        //	The database version number using db.getVersion() for the version number.
        Log.i("printCursor function", "Database Version = " + db.getVersion());
//	The number of columns in the cursor.
        Log.i("printCursor function", "Cursor Number of Columns = " + c.getColumnCount());
//	The name of the columns in the cursor.
        Log.i("printCursor function", "Cursor Column Names = " + Arrays.toString(c.getColumnNames()));
//	The number of results in the cursor
        Log.i("printCursor function", "Cursor Result Count = " + c.getCount());

    }


    protected void showMessage(int position) {
        Message selectedMessage = elements.get(position);

        View message_view = getLayoutInflater().inflate(R.layout.message_edit, null);
        //get the TextViews
        EditText rowMessage = message_view.findViewById(R.id.text_message);
        //  EditText rowEmail = message_view.findViewById(R.id.row_email);
        //  TextView rowId = message_view.findViewById(R.id.row_id);

        //set the fields for the alert dialog
        rowMessage.setText(selectedMessage.getMessage());
        //    rowId.setText("id:" + selectedMessage.getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You clicked on item #" + position)
                .setMessage("You can update the message and then click update to save in the database")
                .setView(message_view) //add the 3 edit texts showing the contact information
                .setPositiveButton("Update", (click, b) -> {
                    selectedMessage.update(selectedMessage.getType().toString(), rowMessage.getText().toString());
                    updateMessage(selectedMessage);
                    myAdapter.notifyDataSetChanged(); //the email and name have changed so rebuild the list
                })
                .setNegativeButton("Delete", (click, b) -> {
                    deleteMessage(selectedMessage); //remove the message from database
                    elements.remove(position); //remove the message from message list
                    myAdapter.notifyDataSetChanged(); //there is one less item so update the list
                    // remove fragment *******************************
                  Fragment frag = getSupportFragmentManager().findFragmentByTag("targetfrag");
                    if(frag != null)
                        getSupportFragmentManager().beginTransaction().remove(frag).commit();
                })
                .setNeutralButton("dismiss", (click, b) -> {
                })
                .create().show();

    }


    // updates database message
    protected void updateMessage(Message c) {
        //Create a ContentValues object to represent a database row:
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(MyOpener.COL_TYPE, c.getType());
        updatedValues.put(MyOpener.COL_MESSAGE, c.getMessage());
        updatedValues.put(MyOpener.COL_LAT, c.getMessage());
        updatedValues.put(MyOpener.COL_LONG, c.getMessage());

        //now call the update function:
        db.update(MyOpener.TABLE_NAME, updatedValues, MyOpener.COL_ID + "= ?", new String[]{Long.toString(c.getId())});
    }

    // deletes database message
    protected void deleteMessage(Message c) {
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[]{Long.toString(c.getId())});
    }


    //This class needs 4 functions to work properly:
//   adapter not using old view if already created
    private class MyOwnAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return elements.size();
        }

        public Message getItem(int position) {
            return elements.get(position);
        }

        //  public long getItemId(int position) {
        //      return (long) position;
        //   }

        public long getItemId(int position) {
            return getItem(position).getId();
        }


        public View getView(int position, View old, ViewGroup parent) {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            Log.i("position", " positiona = " + position);

            String jj1 = elements.get(position).getType();
            Log.i("JJ1", " value = " + jj1);

            //select view based on type of message:
            //  if (newView == null) {
            // String jj = elements.get(getCount()-1).getType();
            //  Log.e("JJ", " value = "+jj);


            if (jj1.equals("s")) {
                //Send
                Log.i("view", "use sendview");
                newView = inflater.inflate(R.layout.send_row, parent, false);
            } else {
                // Receive
                Log.i("view", "use receiveview");
                newView = inflater.inflate(R.layout.receive_row, parent, false);
            }

            //   }

            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.text_message);
            tView.setText(getItem(position).message.toString());

            //return it to be put in the table
            return newView;
        }


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
                alertDialogBuilder.setTitle(getResources().getString(R.string.alert_imagelist_title)).setMessage(getResources().getString(R.string.alert_imagelist))
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



