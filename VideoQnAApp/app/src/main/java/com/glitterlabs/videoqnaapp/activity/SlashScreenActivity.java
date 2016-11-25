package com.glitterlabs.videoqnaapp.activity;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.glitterlabs.videoqnaapp.R;
import com.glitterlabs.videoqnaapp.model.EditProfile;
import com.glitterlabs.videoqnaapp.model.SingaltonUser;
import com.glitterlabs.videoqnaapp.model.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.glitterlabs.videoqnaapp.model.Util.isInternetOn;

public class SlashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 4000;
    private String strUserType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_screen);

        if (Util.isInternetOn(this)) {
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(SPLASH_TIME_OUT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(new Intent(SlashScreenActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            };
            timerThread.start();
        }else{
            Util.showLocationDialog(this,"Internet Error !","Check your internet connection and try again.");
        }
    }

}
