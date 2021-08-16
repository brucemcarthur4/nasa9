package com.example.androidlabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int REQUEST_500 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Profile Activity", "In onCreate()");
        setContentView(R.layout.profileactivity);


        // Set onClickListener for the button to take photo
        // This is using a lambda that was covered in Module 2
        findViewById(R.id.profileTakePictureButton).setOnClickListener((listener) -> {
            dispatchTakePictureIntent();
        });

        // Go to toolbar section
        findViewById(R.id.goToolbar).setOnClickListener((listener) -> {
            dispatchGoToolbar();
        });

        // Go to Chat section
        Intent nextPage = new Intent(this, ChatRoomActivity.class);
        Button gotochat = findViewById(R.id.goChat);
        gotochat.setOnClickListener( click -> startActivity( nextPage ));

        // Go to Toolbar section
      //  Intent toolbarPage = new Intent(this, TestToolbar.class);
      //  Button gototoolbar = findViewById(R.id.goToolbar);
      //  gototoolbar.setOnClickListener( click -> startActivity( toolbarPage ));


        // When the 'Go to Weather Forecast' button is clicked, launch the forecast activity
        ((Button) findViewById(R.id.weather_forecast_button)).setOnClickListener(clk -> {
            startActivity(new Intent(this, LatLong.class));
        });

        }

    private void dispatchGoToolbar() {
        Log.d("Profile Activity", "In dispatchGoToolbar()");
        Intent intenttoolbar = new Intent(this, TestToolbar.class);
        if (intenttoolbar.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intenttoolbar, REQUEST_500);
        }
    }

    private void dispatchTakePictureIntent() {
        Log.d("Profile Activity", "In dispatchTakePictureIntent()");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "REQUEST_IMAGE_CAPTURE");
            ImageButton mImageButton = findViewById(R.id.profileTakePictureButton);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageButton.setImageBitmap(imageBitmap);
        }

        if (requestCode == REQUEST_500 && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "REQUEST_500");
            int num = Integer.parseInt(data.getStringExtra("data2"));
            if (num == 500) {
// go to  login
                finish();
               // Intent intent = new Intent(this, MainActivity.class);
              //  startActivity(intent);

            }else{
// nothing

            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Profile Activity", "In onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Profile Activity", "In onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Profile Activity", "In onResume()");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Profile Activity", "In onDestroy()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Profile Activity", "In onStop()");
    }







}