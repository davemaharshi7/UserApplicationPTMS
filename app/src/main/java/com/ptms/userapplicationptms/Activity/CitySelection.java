package com.ptms.userapplicationptms.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    Button next,addbalance;
    TextView loading;
    String source, dest;
    private int src_key, dest_key;
    FirebaseAuth mAuth;
    Double latitude,longitude;
    HashMap<String, Integer> hash_table = new HashMap<>();
    HashMap<Integer, String> hash_city_name = new HashMap<>();
//    private LocationManager locationManager;
//    private LocationListener locationListener;

    protected void onStart() {
        super.onStart();

        databaseSource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> cities = new ArrayList<String>();
                hash_table.clear();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String city_key = areaSnapshot.getKey();
                    String city_name = areaSnapshot.child("City_Name").getValue(String.class);
//                    Log.i("KEEEEYYY:",city_key);
                    hash_table.put(city_name, Integer.parseInt(city_key));
                    hash_city_name.put(Integer.parseInt(city_key), city_name);
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

                SharedPreferences pref = getApplicationContext().getSharedPreferences
                        ("HashMapCityKey",
                                Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                for (String s : hash_table.keySet()) {
                    editor.putInt(s, hash_table.get(s));
                }
                editor.commit();
                SharedPreferences pref1 = getApplicationContext().getSharedPreferences
                        ("HashMapCityName",
                                Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = pref1.edit();

                for (Integer s : hash_city_name.keySet()) {
                    ed.putString(Integer.toString(s), hash_city_name.get(s));
                }
                ed.commit();
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
        addbalance = findViewById(R.id.addBalance);
        loading = (TextView) findViewById(R.id.loading);
        mAuth = FirebaseAuth.getInstance();
        //For testing Shared Data
        shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
        //printMessage(shared.getString("bus_id","NULL"));
        next.setEnabled(false);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source = srcSpinner.getSelectedItem().toString();
                dest = destSpinner.getSelectedItem().toString();
                if (source == null && dest == null) {
                    printMessage("Please Wait for cities to Load...");
                } else if (!TextUtils.equals(source, dest)) {

                    src_key = hash_table.get(source);
                    dest_key = hash_table.get(dest);
                    //Log.i("KEEEEY:",Integer.p);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("source", source);
                    editor.putString("destination", dest);
                    editor.putInt("source_key", src_key);
                    editor.putInt("destination_key", dest_key);
                    editor.commit();
                    changeActivity();

                } else {
                    printMessage("Source and Destination Cannot be Same!");
                }
            }
        });


        addbalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),QrCardScanner.class);
                startActivity(intent1);
                finish();
            }
        });
//        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                Log.i("LOCATION", Double.toString(location.getLatitude()) + " & "+ Double
//                        .toString(location.getLongitude()));
//                latitude = location.getLatitude()*100;
//                longitude = location.getLongitude()*100;
//                locationManager.removeUpdates(locationListener);
////                SharedPreferences.Editor editor = shared.edit();
////                editor.putFloat("latitude", latitude);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
//                        .ACCESS_FINE_LOCATION}, 1);
//                return;
//
//            } else {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
//                        locationListener);
//            }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
//                    ==  PackageManager.PERMISSION_GRANTED){
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 , 0,
//                        locationListener);
//
//            }
//        }
//    }

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
    }



}
