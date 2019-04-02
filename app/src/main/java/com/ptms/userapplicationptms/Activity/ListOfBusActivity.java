package com.ptms.userapplicationptms.Activity;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptms.userapplicationptms.Adapter.BusAdapter;
import com.ptms.userapplicationptms.Model.SingleBusClass;
import com.ptms.userapplicationptms.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfBusActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRefRoute,myRefBusRouteTime,myRefCity;
    List<String> routes = new ArrayList<String>();
    String src,dest,src_name,dest_name;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<SingleBusClass> singleBus;
    boolean flag1, flag2;
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
        this.setTitle(d);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView =  findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.INVISIBLE);



    }

    private ArrayList<SingleBusClass> getMyObjects() {
        // This will return the ArrayList of your CustomClass objects
        //onStart();
        return singleBus;
    }



    @Override
    protected void onStart() {
        super.onStart();
        // Read from the database
        flag1 = false;
        flag2=false;
        new AsyncCaller().execute();
//        while(!flag1 && !flag2) {
            //ArrayList<SingleBusClass> yourObjects = getMyObjects();
            // Standard RecyclerView implementation
//            flag1 = true;
//        }

//        while(flag1 && !flag2) {
            progressBar.setVisibility(View.INVISIBLE);

            //assert recyclerView != null;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            singleBus = new ArrayList<>();
            SingleBusClass s = new SingleBusClass("AHMEDABAD",
                    "RAJKOT", "NULL");
            singleBus.add(s);
//        routes.add("R_1");
//        routes.add("R_2");
//        routes.add("R_5");
//        routes.add("R_7");
            BusAdapter adapter = new BusAdapter(singleBus);
            adapter.setOnEntryClickListener(new BusAdapter.OnEntryClickListener() {
                @Override
                public void onEntryClick(View view, int position) {
                    Toast.makeText(getApplicationContext(), "Clicked:" + position, Toast.LENGTH_SHORT).show();
                }
            });
            recyclerView.setAdapter(adapter);
            Log.d("OBJECT", "INFINAL");
            recyclerView.setVisibility(View.VISIBLE);
//            flag2=true;
//        }
    }

    private class LongOperation extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
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

                                    myRefCity.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String ObjectSrcName = dataSnapshot.child(src_key).child
                                                    ("City_Name")
                                                    .getValue
                                                            (String.class);
                                            String ObjectDestName = dataSnapshot.child(dest_key)
                                                    .child("City_Name").getValue(String.class);
                                            String ObjectDepartTime = bus_depart_time;

                                            SingleBusClass s = new SingleBusClass(ObjectSrcName,
                                                    ObjectDestName, ObjectDepartTime);
                                            Log.d("OBJECT", "Src is: " + ObjectSrcName + ", Dest is: " +
                                                    "" + ObjectDestName + ",Depart Time: " +
                                                    "" + ObjectDepartTime);

                                            singleBus.add(s);
                                            Log.d("OBJECT", "SUCCESS");
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

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
            return null;
        }
    }

    private class YourAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        public YourAsyncTask(MyMainActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Doing something, please wait.");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... args) {
            // do background work here
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}