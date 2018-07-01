package com.example.journal.journalapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    private static Intent intent;
    private static Toast myToast;
    private static int position;
    private static String data;
    private static JSONObject JSONData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        intent=getIntent();
        myToast=Toast.makeText(this,null,Toast.LENGTH_SHORT);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        position=intent.getIntExtra("position",-1);
        data=intent.getStringExtra("data");

        if (data.equals("")) {
            finish();
        }else{
            try{
                JSONData=new JSONObject(data);
                actionBar.setTitle(JSONData.getString("header"));
                ((TextView) findViewById(R.id.header)).setText(JSONData.getString("header"));
                ((TextView) findViewById(R.id.body)).setText(JSONData.getString("body"));
            }catch (JSONException e){
                finish();
            }
        }

    }

    public void edit(View v){
        intent.putExtra("action","edit");
        setResult(RESULT_OK,intent);
        finish();
    }
    public void delete(View v){
        intent.putExtra("action","delete");
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        switch (menu.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(menu);
    }

}
