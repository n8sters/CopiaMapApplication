package com.android.ncpow.copiamapapplication;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
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
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.x;

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


    // maybe declare varuable syncrtonized or volitile
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String test = loadCoordinates();
        final List<Double> testCoordinatesA = getLatCoordinates(test);
        final List<Double> testCoordinatesB = getLongCoordinates(test);

        final Handler handler = new Handler();


        final Stack<Double> latStack = new Stack<>();
        final Stack<Double> lngStack = new Stack<>();
        for ( int i = 0; i < testCoordinatesA.size(); i++ ) {
            latStack.push(testCoordinatesA.get(i));
            lngStack.push(testCoordinatesB.get(i));
        }

        // FINALLY WORKINGGGGGGGGGGG YUSSSSSS THANK GODDDD
        Handler handler1 = new Handler();
        for ( int i = 0; i < testCoordinatesA.size(); i++ ) {
            final int finalI = i;
            handler1.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Log.i(LOG_TAG, "test");
                    final LatLng latLng = new LatLng(testCoordinatesA.get(finalI), testCoordinatesB.get(finalI));
                    mMap.addMarker(new MarkerOptions().position(latLng).title(Integer.toString(x)));
                    // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }, 500 * i);
        }

        goToLocationZoom(37.3343947,-122.0464412, 15);

        LatLng start = new LatLng(37.3343947,-122.0464412 );
        //LatLng end = new LatLng(37.33947623,-122.0870109 );
        //mMap.addMarker(new MarkerOptions().position(start).title("Copia start position"));
       // mMap.addMarker(new MarkerOptions().position(end).title("Copia end position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
    }

    private void goToLocationZoom(double lat, double lng, float zoom ) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
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

    public void convertJSONStringtoDoubleArray(String coordinates) {
        // start here!
        Pattern p = Pattern.compile("\\d{2,3}.\\d*");

        Matcher m = p.matcher(coordinates);
        List<String> coord = new ArrayList<>();

        while (m.find()) {
            //System.out.println("Found a " + m.group());
            coord.add(m.group());
        }

        // currently getting values!
        // but....
        // it's stripping the "-" sign off the 122's
        // so I'm checking if the Double value of the String
        // > 100 then m
        // multiply it by -1.

        List<Double> arrA = new ArrayList<>();
        List<Double> arrB = new ArrayList<>();

        //System.out.println("Coord size " + coord.size());

        for ( int i = 0; i < coord.size(); i++ ) {
            if ( i % 2 == 0) {
                arrA.add(Double.parseDouble(coord.get(i)));
            } else {
                arrB.add(Double.parseDouble(coord.get(i)) * -1);
            }
        }

        //System.out.println("Array A: " + arrA.size());
        //System.out.println("Array B: " + arrB.size());

        for ( int i = 0; i <arrA.size(); i++ ) {
            System.out.println("Arr A value: " + arrA.get(i));
            System.out.println("Arr B value: " + arrB.get(i));
        }

        // ok!
    }

    // gets the latitude from the JSON string
    public List<Double> getLatCoordinates(String coordinates ) {

        // start here!
        Pattern p = Pattern.compile("\\d{2,3}.\\d*");

        Matcher m = p.matcher(coordinates);
        List<String> coord = new ArrayList<>();

        while (m.find()) {
            coord.add(m.group());
        }

        List<Double> latArr = new ArrayList<>();

        // another option
        for ( int i = 0; i < coord.size(); i++ ) {
            if ( Double.parseDouble(coord.get(i)) < 100 ) {
                latArr.add(Double.parseDouble(coord.get(i)));
            }
        }

        return latArr;
    }

    // gets the longitude from a JSON string
    public List<Double> getLongCoordinates(String coordinates ) {

        // start here!
        Pattern p = Pattern.compile("\\d{2,3}.\\d*");

        Matcher m = p.matcher(coordinates);
        List<String> coord = new ArrayList<>();

        while (m.find()) {
            coord.add(m.group());
        }

        List<Double> longArr = new ArrayList<>();

        // another option
        for ( int i = 0; i < coord.size(); i++ ) {
            if ( Double.parseDouble(coord.get(i)) > 100 ) {
                longArr.add(Double.parseDouble(coord.get(i)) * -1);
            }
        }

        return longArr;
    }
}
