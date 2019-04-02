package com.ptms.userapplicationptms.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptms.userapplicationptms.R;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CitySelection extends AppCompatActivity {
    DatabaseReference databaseSource;
    Spinner srcSpinner, destSpinner;
    SharedPreferences shared;
    Button next;
    TextView loading;
    String source,dest;
    private int src_key,dest_key;
    FirebaseAuth mAuth;
    HashMap<String, Integer> hash_table = new HashMap<>();

    //TODO:please change appropriately
    HashMap<Integer, String> hash_city_name = new HashMap<>();

    protected void onStart() {
        super.onStart();
        if(!isInternetAvailable())
        {
            printMessage("PLEASE CHECK YOUR CONNECTIVITY!!");
        }
        databaseSource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> cities = new ArrayList<String>();
                hash_table.clear();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String city_key = areaSnapshot.getKey();
                    String city_name = areaSnapshot.child("City_Name").getValue(String.class);
//                    Log.i("KEEEEYYY:",city_key);
                    hash_table.put(city_name,Integer.parseInt(city_key));
                    hash_city_name.put(Integer.parseInt(city_key),city_name);
                    cities.add(city_name);
                }
                Collections.sort(cities);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(CitySelection.this, android.R
                        .layout.simple_spinner_item, cities);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                srcSpinner.setAdapter(areasAdapter);
                destSpinner.setAdapter(areasAdapter);
                next.setEnabled(true);
                loading.setVisibility(View.INVISIBLE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);
        databaseSource = FirebaseDatabase.getInstance().getReference("City");
        srcSpinner = (Spinner) findViewById(R.id.srcSpinner);
        destSpinner = (Spinner) findViewById(R.id.destSpinner);
        next = (Button) findViewById(R.id.nextBtn);
        loading = (TextView) findViewById(R.id.loading);

        mAuth = FirebaseAuth.getInstance();

        //For testing Shared Data
        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);

//        SharedPreferences pref= getApplicationContext().getSharedPreferences("City_Map", Context
//                .MODE_PRIVATE);
//        SharedPreferences.Editor editor= pref.edit();
//
//        for (String s : hash_table.keySet()) {
//            editor.putInt(s, hash_table.get(s));
//        }

        //printMessage(shared.getString("bus_id","NULL"));
        next.setEnabled(false);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source = srcSpinner.getSelectedItem().toString();
                dest = destSpinner.getSelectedItem().toString();
                if(source == null && dest == null)
                {
                    printMessage("Please Wait for cities to Load...");
                }
                else if(!TextUtils.equals(source,dest)){

                     src_key = hash_table.get(source);
                     dest_key = hash_table.get(dest);
                    //Log.i("KEEEEY:",Integer.p);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("source",source);
                    editor.putString("destination",dest);
                    editor.putInt("source_key",src_key);
                    editor.putInt("destination_key",dest_key);
                    editor.commit();
                    changeActivity();

                }
                else {
                    printMessage("Source and Destination Cannot be Same!");
                }
            }
        });

    }

    private void printMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

    }

    private void changeActivity() {
        Intent intent = new Intent(getApplicationContext(),ListOfBusActivity.class);
        intent.putExtra("src_key",Integer.toString(src_key));
        intent.putExtra("dest_key",Integer.toString(dest_key));
        intent.putExtra("src",source);
        intent.putExtra("dest",dest);
        startActivity(intent);
//        finish();
        return;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
