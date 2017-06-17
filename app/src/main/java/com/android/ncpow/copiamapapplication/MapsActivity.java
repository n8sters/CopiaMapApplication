package com.android.ncpow.copiamapapplication;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public String LOG_TAG = MapsActivity.class.getSimpleName();

    Context context;

    String FILENAME = "coordinates.json";

    File file = new File(Environment.getDataDirectory(), FILENAME);


    private GoogleMap mMap;

    public MapsActivity() throws FileNotFoundException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String test = loadCoordinates();

        Log.e(LOG_TAG, test);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final Double sum = 0.1;
        final Double[] testCoordinates = new Double[2];
        testCoordinates[0] = 36.3343947;
        testCoordinates[1] = -121.0870109;
        for ( int i = 0; i < 1; i++ ) {
            final LatLng latLng = new LatLng(testCoordinates[i], testCoordinates[i+1]);

            final int finalI = i;
            boolean b = new Handler().postDelayed(

                    new Runnable() {
                        public void run() {
                            mMap.addMarker(new MarkerOptions().position(latLng).title("test"));
                            Log.i(LOG_TAG, "This'll run 5000 milliseconds later");
                        }
                    },
                    5000);

        }


        LatLng start = new LatLng(37.3343947,-122.0464412 );
        //LatLng end = new LatLng(37.33947623,-122.0870109 );
        mMap.addMarker(new MarkerOptions().position(start).title("Copia start position"));
       // mMap.addMarker(new MarkerOptions().position(end).title("Copia end position"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
    }


    public String loadCoordinates() {

        Resources res = getResources();

        InputStream stream = res.openRawResource(R.raw.coordinates);

        Scanner in = new Scanner(stream);

        in.useLocale(Locale.US);

        StringBuilder builder = new StringBuilder();
        ArrayList list = new ArrayList();

        while( in.hasNext()) {
            builder.append(in.nextLine());
        }

        return builder.toString();
    }
}
