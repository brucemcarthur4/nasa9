package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;


public class Splash extends AppCompatActivity {

    SharedPreferences prefs = null;



        private ProgressBar mProgress;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Show the splash screen
            setContentView(R.layout.activity_splash);
            mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);

            // Start lengthy operation in a background thread
            new Thread(new Runnable() {
                public void run() {
                    doWork();
                    startApp();
                  //  finish();
                }
            }).start();
        }

        // Use this are to downlad image of the day maybe or any other pre work i need
        private void doWork() {
            for (int progress=0; progress<100; progress+=20) {
                try {
                    Thread.sleep(1000);
                    mProgress.setProgress(progress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    private void startApp() {
        Intent nextPage = new Intent(this, LatLong.class);
        startActivity(nextPage);
      //  Button loginbutton = findViewById(R.id.loginButton);
      //  loginbutton.setOnClickListener( click -> startActivity( nextPage ));
    }


    }


/*


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Main Activity", "In onCreate()");
      //  setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_splash);

        EditText myEmail = findViewById(R.id.enterEmail);

        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String savedString = prefs.getString("Email", "email not found");

        myEmail.setText(savedString);


      //  Button saveButton = findViewById(R.id.loginButton);
     //   saveButton.setOnClickListener( bt -> saveSharedPrefs( myEmail.getText().toString()) );

        Intent nextPage = new Intent(this, ProfileActivity.class);
        Button loginbutton = findViewById(R.id.loginButton);
        loginbutton.setOnClickListener( click -> startActivity( nextPage ));

        // Set onClickListener for the button to display a toast
        // This is using a lambda that was covered in Module 2
      //  findViewById(R.id.button).setOnClickListener((listener) -> {
       //     Toast.makeText(this, getResources().getString(R.string.toast), Toast.LENGTH_LONG).show();
      //  });

        // Set onCheckedChangeListener for switch and checkbox to display a snackbar
        // This is using a seperate class instead of a lambda. Both approaches were valid.
 //       CheckedChangeListener ccListener = new CheckedChangeListener(this);
  //      ((CheckBox) findViewById(R.id.checkbox)).setOnCheckedChangeListener(ccListener);
  //      ((Switch) findViewById(R.id.switchbox)).setOnCheckedChangeListener(ccListener);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Main Activity", "In onActivityResult()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Main Activity", "In onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Main Activity", "In onPause()");

        EditText myEmail = findViewById(R.id.enterEmail);
        String stringToSave = myEmail.getText().toString();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Email", stringToSave);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Main Activity", "In onResume()");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Main Activity", "In onDestroy()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Main Activity", "In onStop()");
    }


    private void saveSharedPrefs(String stringToSave)
    {
        Log.e("Main Activity", "In saveSharedPrefs()");

    }

*/



