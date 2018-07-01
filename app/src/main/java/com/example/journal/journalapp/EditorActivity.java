package com.example.journal.journalapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class EditorActivity extends AppCompatActivity {

    private static Intent intent;
    private static String data;
    private static JSONObject JSONData;
    private static Toast myToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("New Entry");
        }

        myToast=Toast.makeText(this,null,Toast.LENGTH_SHORT);
        intent=getIntent();

        if(intent!=null && intent.getIntExtra("position",-1)!=-1){
            data=intent.getStringExtra("data");
            try{
                JSONData=new JSONObject(data);
                assert actionBar != null;
                actionBar.setTitle(JSONData.getString("header"));
                ((EditText) findViewById(R.id.header)).setText(JSONData.getString("header"));
                ((EditText) findViewById(R.id.body)).setText(JSONData.getString("body"));
                (findViewById(R.id.body)).requestFocus();
            }catch (JSONException e){}
        }
    }

    public void save(View view){
        EditText header=findViewById(R.id.header);
        EditText body=findViewById(R.id.body);
        String headerTxt=header.getText().toString().trim();
        String bodyTxt=body.getText().toString().trim();

        if(getIntent()!=null && getIntent().getIntExtra("position",-1)!=-1) {
            try {
                JSONObject JSONData = new JSONObject(getIntent().getStringExtra("data"));
                if(!JSONData.getString("header").equals(headerTxt) || !JSONData.getString("body").equals(bodyTxt)) {
                    JSONData.put("header", headerTxt);
                    JSONData.put("body", bodyTxt);
                    if(JSONData.has("id")) {
                        JSONData.put("id", JSONData.getString("id"));
                    }
                    getIntent().putExtra("data",JSONData.toString());
                    setResult(RESULT_OK,getIntent());
                    finish();

                }else{
                    myToast.setText("No Changes Made");
                    myToast.show();
                }


            }catch (JSONException e){}
        }else{
            if (headerTxt.length() == 0) {
                myToast.setText("Invalid Header");
                myToast.show();
                header.requestFocus();
            } else if (bodyTxt.length() == 0) {
                myToast.setText("Invalid Body");
                myToast.show();
                body.requestFocus();
            } else {
                JSONObject JSONData = new JSONObject();
                try {
                    JSONData.put("header", headerTxt);
                    JSONData.put("body", bodyTxt);
                    Intent intent = getIntent();
                    intent.putExtra("data", JSONData.toString());
                    setResult(RESULT_OK, intent);
                } catch (JSONException e) {
                }
                finish();
            }
        }
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
