package com.android.ncpow.copiamapapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public String LOG_TAG = MapsActivity.class.getSimpleName();

    String FILENAME = "coordinates.json";

    File file = new File(Environment.getDataDirectory(), FILENAME);

    Context context = this;
    private GoogleMap mMap;

    public MapsActivity() throws FileNotFoundException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final Button clickButton = (Button) findViewById(R.id.start_navigation_button);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, getString(R.string.toast_message), Toast.LENGTH_LONG).show();

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String test = loadCoordinates();
        final List<Double> testCoordinatesA = getLatCoordinates(test);
        final List<Double> testCoordinatesB = getLongCoordinates(test);


        // FINALLY WORKINGGGGGGGGGGG YUSSSSSS THANK GODDDD
        Handler handler1 = new Handler();
        final LatLng latLng = new LatLng(37.3343947, -122.0464412);
        for ( int i = 0; i < testCoordinatesA.size(); i++ ) {
            final int finalI = i;
            handler1.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Drawable circleDrawable = getResources().getDrawable(R.drawable.circle_shape);
                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                    MarkerOptions marker = new MarkerOptions().position(latLng).icon(markerIcon);
                    Marker m = mMap.addMarker(marker);
                    m.setPosition(new LatLng(testCoordinatesA.get(finalI), testCoordinatesB.get(finalI)));
                }


            }, 500 * i);

        }

        goToLocationZoom(37.3343947,-122.0464412, 15);

        LatLng start = new LatLng(37.3343947,-122.0464412 );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
    }

    private void goToLocationZoom(double lat, double lng, float zoom ) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public String loadCoordinates() {

        Resources res = getResources();

        InputStream stream = res.openRawResource(R.raw.coordinates);

        Scanner in = new Scanner(stream);

        in.useLocale(Locale.US);

        StringBuilder builder = new StringBuilder();

        while( in.hasNext()) {
            builder.append(in.nextLine());
        }

        return builder.toString();
    }

    // gets the latitude from the JSON string
    public List<Double> getLatCoordinates(String coordinates ) {

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

        for ( int i = 0; i < coord.size(); i++ ) {
            if ( Double.parseDouble(coord.get(i)) > 100 ) {
                longArr.add(Double.parseDouble(coord.get(i)) * -1);
            }
        }

        return longArr;
    }

}
