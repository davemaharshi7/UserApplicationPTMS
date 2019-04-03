package com.ptms.userapplicationptms.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptms.userapplicationptms.Adapter.BusAdapter;
import com.ptms.userapplicationptms.Model.SingleBusClass;
import com.ptms.userapplicationptms.R;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ListOfBusActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRefRoute,myRefBusRouteTime,myRefCity;
    private List<String> routes = new ArrayList<String>();
    private String src,dest,src_name,dest_name;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textView;
    private ArrayList<SingleBusClass> singleBus;
    private MyAsynTask myAsynTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_bus);
        database = FirebaseDatabase.getInstance();
        myRefRoute = database.getReference("Route");

        myRefBusRouteTime = database.getReference("Bus_Route_time");
        myRefCity = database.getReference("City");

        src = getIntent().getStringExtra("src_key");
        dest = getIntent().getStringExtra("dest_key");
        src_name = getIntent().getStringExtra("src");
        dest_name = getIntent().getStringExtra("dest");
        String d = "Buses from "+src_name+" to "+dest_name;
        textView = findViewById(R.id.ErrorMsg);
        textView.setVisibility(View.GONE);
        this.setTitle(d);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView =  findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.INVISIBLE);

    }

    private ArrayList<SingleBusClass> getMyObjects() {
        return singleBus;
    }



    @Override
    protected void onStart() {
        super.onStart();

        myAsynTask = new MyAsynTask();
        myAsynTask.execute();
        progressBar.setVisibility(View.VISIBLE);
    }

    private class MyAsynTask extends AsyncTask< Void, ArrayList<SingleBusClass>, ArrayList<SingleBusClass>>
    {

        @Override
        protected ArrayList<SingleBusClass> doInBackground(Void... voids) {
            myRefRoute.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String route_key = areaSnapshot.getKey();
                        String route_path = areaSnapshot.child("Path").getValue(String.class);
                        String route_startend = areaSnapshot.child("SortPath").getValue(String.class);

                        String[] arrOfStr = route_path.split("#");
                        //Log.d("SSS", "Src and Dest are: " + src +" & " + dest);

                        boolean containsSrc = Arrays.stream(arrOfStr).anyMatch(src::equals);
                        boolean containsDest = Arrays.stream(arrOfStr).anyMatch(dest::equals);
                        if (containsSrc && containsDest) {
                            int indexSrc = -1;
                            for (int i = 0; i < arrOfStr.length; i++) {
                                if (arrOfStr[i].equals(src)) {
                                    indexSrc = i;
                                    break;
                                }
                            }
                            int indexDest = -1;
                            for (int i = 0; i < arrOfStr.length; i++) {
                                if (arrOfStr[i].equals(dest)) {
                                    indexDest = i;
                                    break;
                                }
                            }
                            if (indexSrc < indexDest) {
                                routes.add(route_key);
                                Log.d("SSS", "Route KEY is: " + route_key);
                            }

                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            myRefBusRouteTime.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String bus_id = areaSnapshot.getKey();
                        String bus_depart_time = areaSnapshot.child("Departure_time").getValue(String
                                .class);
                        String route_id = areaSnapshot.child("Route_ID").getValue(String.class);
                        Log.d("SSS", "Bus ID: " + bus_id + " its Route id is : " + route_id);

                        if (routes.contains(route_id)) {
                            Log.d("SSS", "SUCCESS    == Bus ID: " + bus_id + " its timings are : " +
                                    bus_depart_time);
                            myRefRoute.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String srcDest = dataSnapshot.child(route_id).child("SortPath")
                                            .getValue(String.class);
                                    String[] arrOfsrc = srcDest.split("#");
                                    String src_key = arrOfsrc[0];
                                    String dest_key = arrOfsrc[1];

                                    //FETCHING names from Global HashMap
                                    SharedPreferences pref= getApplicationContext().getSharedPreferences("HashMapCityName",
                                            Context.MODE_PRIVATE);
                                    HashMap<String, String> map= (HashMap<String, String> )pref.getAll();
                                    String ObjectSrcName = map.get(src_key);
                                    String ObjectDestName =map.get(dest_key);
                                    String ObjectDepartTime = bus_depart_time;
                                    SingleBusClass s = new SingleBusClass(ObjectSrcName,
                                                    ObjectDestName, ObjectDepartTime,bus_id,route_id);
                                    Log.d("OBJECT", "Src is: " + ObjectSrcName + ", Dest is: " +
                                                    "" + ObjectDestName + ",Depart Time: " +
                                                    "" + ObjectDepartTime);
                                    singleBus.add(s);
                                    publishProgress(singleBus);
                                    Log.d("OBJECT", "SUCCESS");


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return singleBus;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            singleBus = new ArrayList<SingleBusClass>();
        }

        @Override
        protected void onProgressUpdate(ArrayList<SingleBusClass>... values) {
            super.onProgressUpdate(values);
            assert recyclerView != null;
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            BusAdapter adapter = new BusAdapter(values[0]);
//            int size = 0;
//            size = values[0].isEmpty();
            adapter.setOnEntryClickListener(new BusAdapter.OnEntryClickListener() {
                @Override
                public void onEntryClick(View view, int position,String busid, String routeId) {
                    printMessage("Position: "+position+" data:"+busid+" RouteId :"+routeId);
                    Intent i = new Intent(getApplicationContext(),BusDetailsActivity.class);
                    i.putExtra("busid",busid);
                    i.putExtra("routeid",routeId);
                    startActivity(i);
                }
            });
            recyclerView.setAdapter(adapter);
            //Log.d("COUNT", "COUNT"+values[0].isEmpty());
//            if(values[0].isEmpty()|| adapter == null)
//            {
//                Log.d("OBJECT","GONE");
//                recyclerView.setVisibility(View.INVISIBLE);
//                progressBar.setVisibility(View.INVISIBLE);
//                textView.setVisibility(View.VISIBLE);
//
//            }
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<SingleBusClass> s)
        {
            super.onPostExecute(s);

        }
    }


    private void printMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

    }


}