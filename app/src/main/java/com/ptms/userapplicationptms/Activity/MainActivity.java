package com.ptms.userapplicationptms.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import com.mapbox.mapboxsdk.maps.Style;
import com.ptms.userapplicationptms.R;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private DatabaseReference myRefLocation;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        myRefLocation = database.getReference("Location");

        String busid = getIntent().getStringExtra("busid");
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Add the marker image to map


                        myRefLocation.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Double lati = dataSnapshot.child(busid).child("latitude")
                                        .getValue(Double.class);
                                Double longitude =dataSnapshot.child(busid).child("longitude")
                                        .getValue(Double.class);
                                Log.i("MAIN ACTIVITY",lati+"$$$"+longitude);
                                style.addImage("marker-icon-id",
                                        BitmapFactory.decodeResource(
                                                MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));

                                GeoJsonSource geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(
                                        Point.fromLngLat(longitude,lati)));
                                style.addSource(geoJsonSource);

                                SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
                                symbolLayer.withProperties(
                                        PropertyFactory.iconImage("marker-icon-id")
                                );
                                style.addLayer(symbolLayer);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });





                    }
                });
            }
        });
    }

    // Add the mapView's own lifecycle methods to the activity's lifecycle methods
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
