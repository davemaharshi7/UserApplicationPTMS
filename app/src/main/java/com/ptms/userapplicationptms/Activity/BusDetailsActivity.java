package com.ptms.userapplicationptms.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptms.userapplicationptms.Model.SingleBusClass;
import com.ptms.userapplicationptms.R;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;

public class BusDetailsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRefRoute,myRefTicketlog,mRefBusDetails;
    private Button locationButton;
    private TextView TextViewBusId,TextViewRoute,TextViewTickets,TextViewBusNumber;
    private StringBuilder stringBuilder;
    private String busid,routeid,totalSeats,busNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        setTitle("Bus Details");
        busid = getIntent().getStringExtra("busid");
        routeid = getIntent().getStringExtra("routeid");
        Log.d("DETAILS","DATA:"+busid);
        Log.d("DETAILS","DATA:"+routeid);

        database = FirebaseDatabase.getInstance();
        myRefRoute = database.getReference("Route");
        myRefTicketlog = database.getReference("Ticket_Log");
        mRefBusDetails = database.getReference("BusDetails");

        locationButton = findViewById(R.id.locationButton);
        TextViewBusNumber = findViewById(R.id.busNumber);
        TextViewBusId = findViewById(R.id.BUSID);
        TextViewRoute = findViewById(R.id.RouteDisplay);
        TextViewTickets = findViewById(R.id.liveTicketCount);
        TextViewBusId.setText(busid);
        stringBuilder = new StringBuilder();


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

        SharedPreferences pref= getApplicationContext().getSharedPreferences("HashMapCityName",
        Context.MODE_PRIVATE);
        HashMap<String, String> map= (HashMap<String, String> )pref.getAll();
        //for (String s : map.keySet()) {
            //String value = map.get("1");
        //}


        myRefRoute.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    String route_path = dataSnapshot.child(routeid).child("Path").getValue(String.class);

                    String[] arrOfStr = route_path.split("#");

                    for(String a : arrOfStr)
                    {
                        stringBuilder.append(map.get(a));
                        Log.d("DDD","Value"+map.get(a));
                        if(a != arrOfStr[arrOfStr.length-1])
                        {
                            stringBuilder.append("-");
                        }

                    }
                    String finalstring = stringBuilder.toString();
                    //Log.d("BusDetailsActivity","final string :"+finalstring);
                    TextViewRoute.setText(finalstring);


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
        myRefTicketlog.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                //for all list of conductors during single trip
                //dataSnapshot.child(busid)
                for( DataSnapshot conductors: dataSnapshot.child(busid).getChildren()){

                    Log.d("TICKETS:",conductors.getKey());
                    for(DataSnapshot tickets : conductors.getChildren()){
                        Log.d("TICKETS:",tickets.getKey());

                            String singleTicket = tickets.child("e_No_Of_Ticket").getValue(String
                                    .class);
                            Log.d("Tickets :" ,singleTicket);
                        int single = Integer.parseInt(singleTicket.trim());
                        count = count + single;
                    }

                }
                Log.d("COUNT :" ,""+ count);
                int finalcount = Integer.parseInt(totalSeats)- count;
                TextViewTickets.setText(""+finalcount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
