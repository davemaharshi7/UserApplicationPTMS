package com.ptms.userapplicationptms.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptms.userapplicationptms.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class cardPinActivity extends AppCompatActivity {
    private DatabaseReference myRefCard,databaseTicket,databaseFareCollected;
    private String cardId,cardName,cardPhone,cardPin;
    private EditText inputPin;
    private Integer cardBalance;
    private TextView textViewCardId,textViewUserName,textViewPhone;
    private SharedPreferences shared;
    private int ticketFare;
    private int lastFare;
    private FirebaseAuth mAuth;
    private String fareCollectedKey;
    private Button checkPin;
    private String cardEmail,cardContact;
    private int CASH_PAYMENT = 0;
    private int CARD_PAYMENT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pin);



            cardId = getIntent().getStringExtra("cardID");
            cardName = getIntent().getStringExtra("cardName");
            cardPhone = getIntent().getStringExtra("cardPhone");

            myRefCard = FirebaseDatabase.getInstance().getReference("Card_Details");
            inputPin = findViewById(R.id.inputPin);
            checkPin = findViewById(R.id.confirmPinButton);
            textViewCardId = findViewById(R.id.scannedCardId2);
            textViewUserName = findViewById(R.id.userNameCard2);
            textViewPhone = findViewById(R.id.userPhoneCard2);

            setTitle("Verify PIN Code");


            shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
            ticketFare = shared.getInt("Ticket_FARE",0);
            Toast.makeText(getApplicationContext(),""+ticketFare,Toast.LENGTH_SHORT).show();
            textViewCardId.setText(cardId);
            textViewPhone.setText(cardPhone);
            textViewUserName.setText(cardName);



            checkPin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(inputPin.getText().toString().isEmpty())
                    {
                        inputPin.setError("Please Enter your Pin");
                        inputPin.requestFocus();
                        return;
                    }
                    if(inputPin.getText().toString().length() != 4)
                    {
                        inputPin.setError("Pin Must be of 4 digits only");
                        inputPin.requestFocus();
                        return;
                    }
                    String pin = inputPin.getText().toString();
                    String encrptyString = encryptAlgo(pin);
                    Log.d("ENCRYPT",encrptyString);
                    if(cardPin.equals(encrptyString)){
//                    TODO: ADDMONEY
                        Intent add = new Intent(getApplicationContext(),EnterAmountActivity.class);
                        add.putExtra("cardId",cardId);
                        startActivity(add);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"PIN INVALID",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }



        private void printMessage(String s) {
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
        }

        private String encryptAlgo(String pin) {
            try {

                // Static getInstance method is called with hashing MD5
                MessageDigest md = MessageDigest.getInstance("MD5");

                // digest() method is called to calculate message digest
                //  of an input digest() return array of byte
                byte[] messageDigest = md.digest(pin.getBytes());

                // Convert byte array into signum representation
                BigInteger no = new BigInteger(1, messageDigest);

                // Convert message digest into hex value
                String hashtext = no.toString(16);
                while (hashtext.length() < 32) {
                    hashtext = "0" + hashtext;
                }
                return hashtext;
            }

            // For specifying wrong message digest algorithms
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onStart() {
            super.onStart();


            myRefCard.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cardBalance = dataSnapshot.child(cardId).child("Balance").getValue(Integer.class);
                    cardPin = dataSnapshot.child(cardId).child("pin").getValue(String.class);
                    cardEmail = dataSnapshot.child(cardId).child("Email").getValue(String.class);
                    cardContact = dataSnapshot.child(cardId).child("Mobile_no").getValue(String.class);
//                cardName = dataSnapshot.child(cardId).child("Mobile_no").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

