package com.ptms.userapplicationptms.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptms.userapplicationptms.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Date;


public class BusDetailsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRefRoute,myRefTicketlog,mRefBusDetails,myRefLastCityVisited;
    private Button locationButton,getLocationButton;
    private Spinner routeSpinner;
    private TextView TextViewBusId,TextViewRoute,TextViewTickets,TextViewBusNumber,
        TextviewLastVisitedCity;
    private StringBuilder stringBuilder;
    private String busid,routeid,totalSeats,busNumber,City;
    private String[] arrOfStr;
    private int total_no_of_ticket_issued;
    HashMap<String, String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        setTitle("Bus Details");
        busid = getIntent().getStringExtra("busid");
        routeid = getIntent().getStringExtra("routeid");
        Log.d("DETAILS","DATA:"+busid);
        Log.d("DETAILS","DATA:"+routeid);
        total_no_of_ticket_issued=0;
        database = FirebaseDatabase.getInstance();
        myRefRoute = database.getReference("Route");
        myRefTicketlog = database.getReference("Ticket_Log");
        mRefBusDetails = database.getReference("BusDetails");
        myRefLastCityVisited = database.getReference("Location");

        TextviewLastVisitedCity = findViewById(R.id.lastVisitedCity);
        routeSpinner = findViewById(R.id.citySpinner);
        locationButton = findViewById(R.id.locationButton);
        TextViewBusNumber = findViewById(R.id.busNumber);
        TextViewBusId = findViewById(R.id.BUSID);
        TextViewRoute = findViewById(R.id.RouteDisplay);
        TextViewTickets = findViewById(R.id.liveTicketCount);
        getLocationButton = findViewById(R.id.getTicketCount);
        SharedPreferences pref= getApplicationContext().getSharedPreferences("HashMapCityName",
                Context.MODE_PRIVATE);
        map = (HashMap<String, String> )pref.getAll();

        getLocationButton.setOnClickListener(v -> {
            String spinnerCity = routeSpinner.getSelectedItem().toString();
            ArrayList<String> mList = new ArrayList<>();
            total_no_of_ticket_issued=0;
            for(String a:arrOfStr)
            {
                mList.add(map.get(a));
                Log.d("past",map.get(a));
                if(spinnerCity.equals(map.get(a)))
                    break;
            }
            myRefTicketlog.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = 0;
                    //for all list of conductors during single trip
                    //dataSnapshot.child(busid)
                    for( DataSnapshot conductors: dataSnapshot.child(busid).getChildren()){

                        //Log.d("TICKETS:",conductors.getKey());
                        for(DataSnapshot tickets : conductors.getChildren()){
                            //Log.d("TICKETS:",tickets.getKey());
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            String formattedDate = df.format(c);
                            //Log.d("current date: ", formattedDate);
                            if(tickets.child("d_Date").getValue(String.class).equals
                                    (formattedDate)){
                                //Log.d("database date: ", tickets.child("d_Date").getValue(String.class));
                                total_no_of_ticket_issued += Integer.parseInt(tickets.child("e_No_Of_Ticket").getValue(String.class));
                                String destination = tickets.child("c_Destination").getValue(String.class);
                                if(mList.contains(destination)) {
                                    String singleTicket = tickets.child("e_No_Of_Ticket").getValue(String
                                            .class);
                                    Log.d("Tickets :", singleTicket);
                                    int single = Integer.parseInt(singleTicket.trim());
                                    count = count + single;
                                }
                            }

                        }

                    }
                    Log.d("COUNT :" ,""+ count);
                    int finalcount=0;
                    if(Integer.parseInt(totalSeats)-(total_no_of_ticket_issued-count)>0)
                        finalcount=Integer.parseInt(totalSeats)-(total_no_of_ticket_issued-count);
                    Log.d("totalSeats :" ,""+ totalSeats);
                    Log.d("issues :" ,""+ total_no_of_ticket_issued);
                    TextViewTickets.setText("Tickets Available at "+City+" are "+finalcount);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });
        TextViewBusId.setText(busid);



        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("busid",busid);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        stringBuilder = new StringBuilder("");

        //for (String s : map.keySet()) {
            //String value = map.get("1");
        //}
        List<String> cities = new ArrayList<String>();

        myRefRoute.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    String route_path = dataSnapshot.child(routeid).child("Path").getValue(String.class);

                    arrOfStr = route_path.split("#");

                    for(String a : arrOfStr)
                    {
                        stringBuilder.append(map.get(a));
                        Log.d("DDD","Value"+map.get(a));
                        cities.add(map.get(a));
                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(BusDetailsActivity.this,
                                android.R
                                .layout.simple_spinner_item, cities);
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        routeSpinner.setAdapter(areasAdapter);
                        if(a != arrOfStr[arrOfStr.length-1])
                        {
                            stringBuilder.append("-");
                        }

                    }
                    String finalstring = stringBuilder.toString();
                    //Log.d("BusDetailsActivity","final string :"+finalstring);
                    TextViewRoute.setText(finalstring);
//                    stringBuilder

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRefBusDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalSeats = dataSnapshot.child(busid).child("Total_Seats").getValue(String
                        .class);
                busNumber = dataSnapshot.child(busid).child("Bus_No").getValue(String.class);
                TextViewBusNumber.setText(busNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRefLastCityVisited.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                City = dataSnapshot.child(busid).child("lastVisitedCity").getValue(String.class);
                TextviewLastVisitedCity.setText(City);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
