package com.example.journal.journalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.text.DateFormat.getDateTimeInstance;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,RVAdapter.OnItemClicked {

    private static RecyclerView MainRV;
    private static RVAdapter mainAdapter;
    private static final int RC_SIGN_IN = 123;
    private static final int DETAIL_ACT_ID = 492;
    private static final int EDIT_RQ_CODE = 822;
    private static final int NEW_ENTRY_RQ_CODE = 131;
    private static Toast myToast;
    private static ArrayList<String> myData;
    private static DatabaseReference myDBRef;
    private static FirebaseDatabase database;
    private static String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myToast=Toast.makeText(getApplicationContext(),null,Toast.LENGTH_SHORT);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editorActivity=new Intent(getApplicationContext(),EditorActivity.class);
                startActivityForResult(editorActivity,NEW_ENTRY_RQ_CODE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navigationHeader=navigationView.getHeaderView(0);

        Menu menu=navigationView.getMenu();

        MainRV=findViewById(R.id.MainRV);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        MainRV.setLayoutManager(layoutManager);
        myData=new ArrayList<>();
        mainAdapter = new RVAdapter(myData, this);
        MainRV.setAdapter(mainAdapter);
        mainAdapter.setOnClick(this);

        database = FirebaseDatabase.getInstance();
//        database.setPersistenceEnabled(true);
        myDBRef = database.getReference("JournalApp");

        SharedPreferences sharedPreferences=getPreferences(MODE_PRIVATE);
        if(sharedPreferences.contains("LoginData")) {

            try {
                JSONObject LoginData = new JSONObject(sharedPreferences.getString("LoginData", null));
                UserID=LoginData.getString("id");
                ((TextView) navigationHeader.findViewById(R.id.fullnames)).setText(LoginData.getString("fullnames"));
                ((TextView) navigationHeader.findViewById(R.id.email)).setText(LoginData.getString("email"));
                if (LoginData.getString("dp").length() > 0) {
                    ImageView dp = navigationHeader.findViewById(R.id.dp);
                    Picasso.get().load(LoginData.getString("dp")).resize(60, 60).into(dp);
                }
                menu.findItem(R.id.nav_login).setVisible(false);
                menu.findItem(R.id.nav_logout).setVisible(true);


                myDBRef.child(UserID).addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String value = dataSnapshot.getValue(String.class);
                        String resKey = dataSnapshot.getKey();

                        try {
                            JSONObject JSONVal = new JSONObject(value);
                            JSONVal.put("id",resKey);
                            mainAdapter.add(JSONVal.toString());
                            if (mainAdapter.getItemCount() == 1) {
                                dataAvailability();
                            }
                        }catch (JSONException e){}
                    }


                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        String resKey = dataSnapshot.getKey();

                        try {
                            JSONObject JSONVal = new JSONObject(value);
                            JSONVal.put("id", resKey);
                            mainAdapter.remove(JSONVal.toString());
                            if (mainAdapter.getItemCount() == 1) {
                                dataAvailability();
                            }
                        }catch (JSONException e){}
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });


            }catch(JSONException e){ }
        }

        dataAvailability();
    }

    @Override
    public void onItemClick(int position) {
        Intent detailActivity=new Intent(this,DetailActivity.class);
        detailActivity.putExtra("position",position);
        detailActivity.putExtra("data",mainAdapter.data.get(position));
        startActivityForResult(detailActivity,DETAIL_ACT_ID);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_login:
                googleSignin();
                break;
            case R.id.nav_logout:
                googleSignout();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getCurrentTimeStamp() {
        DateFormat DF = getDateTimeInstance(1,1,Locale.getDefault());
        String date = DF.format(new Date());
        return date;
    }
    public void googleSignin(){


        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public void googleSignout(){


        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        getPreferences(MODE_PRIVATE).edit().remove("LoginData").commit();

                        myToast.setText("Account Logged Out");
                        myToast.show();

                        Intent mainActivity=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(mainActivity);
                        finish();
                    }
                });
    }

    private void dataAvailability(){
        LinearLayout nodata=(findViewById(R.id.no_data));
        if(mainAdapter.getItemCount()==0){
            if(nodata.getVisibility()==View.GONE) {
                MainRV.setVisibility(View.GONE);
                nodata.setVisibility(View.VISIBLE);
            }
        }else{
            if(MainRV.getVisibility()==View.GONE) {
                nodata.setVisibility(View.GONE);
                MainRV.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                SharedPreferences sharedPreferences=getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor sharedPreferencesEditor=sharedPreferences.edit();
                JSONObject LoginData=new JSONObject();
                try{
                    LoginData.put("fullnames",user.getDisplayName());
                    LoginData.put("email",user.getEmail());
                    LoginData.put("dp",user.getPhotoUrl());
                    LoginData.put("id",user.getUid());
                    sharedPreferencesEditor.putString("LoginData",LoginData.toString());
                    sharedPreferencesEditor.commit();
                    Intent mainActivity=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(mainActivity);
                    finish();

                    myToast.setText("Welcome "+user.getDisplayName());
                }catch (JSONException e){
                    myToast.setText("Session Initialization Failed");
                }

            } else {
                if (response==null){
                    myToast.setText("Cancelled");
                }else{
                    myToast.setText("Login Failed. Try again later");
                }
            }
            myToast.show();
        }else if(requestCode==DETAIL_ACT_ID){
            if (resultCode == RESULT_OK) {
                String action = data.getStringExtra("action");
                switch (action) {
                    case "delete":
                        try {
                            JSONObject JSONData=new JSONObject(data.getStringExtra("data"));
                            if(getPreferences(MODE_PRIVATE).contains("LoginData")){
                                myDBRef.child(UserID).child(JSONData.getString("id")).removeValue();
                            }else{
                                mainAdapter.remove(data.getIntExtra("position",-1));
                                dataAvailability();
                            }

                            myToast.setText("Entry Deleted");
                            myToast.show();
                        }catch (JSONException e){}
                        break;
                    case "edit":
                        Intent editorActivity = new Intent(this, EditorActivity.class);
                        editorActivity.putExtras(data.getExtras());
                        startActivityForResult(editorActivity, EDIT_RQ_CODE);
                        break;
                }
            }
        } else if(requestCode==NEW_ENTRY_RQ_CODE){
            if (resultCode == RESULT_OK) {
                String myData = data.getStringExtra("data");
                myData=addTimestamp(myData);
                if(getPreferences(MODE_PRIVATE).contains("LoginData")) {
                    myDBRef.child(UserID).push().setValue(myData);
                }else{
                    mainAdapter.add(myData);
                    if(mainAdapter.getItemCount()==1){
                        dataAvailability();
                    }
                }
                myToast.setText("New Entry Added");
                myToast.show();
            }
        }else if(requestCode==EDIT_RQ_CODE) {
            if (resultCode == RESULT_OK) {
                String intentData=data.getStringExtra("data");
                intentData = addTimestamp(intentData);
                try {
                    JSONObject JSONData=new JSONObject(intentData);
                    if(getPreferences(MODE_PRIVATE).contains("LoginData")) {
                        myDBRef.child(UserID).child(JSONData.getString("id")).setValue(intentData);
                    }
                    mainAdapter.updateAt(data.getIntExtra("position", -1), intentData);
                    myToast.setText("Entry Updated");
                    myToast.show();
                }catch (JSONException e){}
            }
        }
    }

    private String addTimestamp (String data){
        try{
            JSONObject JSONData=new JSONObject(data);
            JSONData.put("timestamp",getCurrentTimeStamp());
            return JSONData.toString();
        }catch (JSONException e){}
        return data;
    }

}
