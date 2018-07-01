package com.example.journal.journalapp;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        new CountDownTimer(3000,3000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent mainActivity=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        }.start();
    }
}
