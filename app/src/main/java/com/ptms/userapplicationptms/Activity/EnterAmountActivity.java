package com.ptms.userapplicationptms.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptms.userapplicationptms.R;

public class EnterAmountActivity extends AppCompatActivity {

    EditText amt;
    Button b;
    int lastAmt;String cardId;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_amount);
        databaseReference = FirebaseDatabase.getInstance().getReference("Card_Details");
        amt = findViewById(R.id.amt);
        cardId = getIntent().getStringExtra("cardId");
        b = findViewById(R.id.submit);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(amt.getText().toString());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        lastAmt = dataSnapshot.child(cardId).child("Balance").getValue(Integer.class);
                        int ans = lastAmt + amount;
                        databaseReference.child(cardId).child("Balance").setValue(ans);
                        Toast.makeText(getApplicationContext(),"MONEY ADDED",Toast.LENGTH_SHORT);
                        Intent in = new Intent(getApplicationContext(),CitySelection.class);
                        startActivity(in);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

}
