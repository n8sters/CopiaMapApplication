package com.android.ncpow.copiamapapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


    // public String LOG_TAG = MapsActivity.class.getSimpleName();

    // file with coordinates, minus that odd one out. Ask Fernando ;)
    private final String FILENAME = "coordinates.json";

    File file = new File(Environment.getDataDirectory(), FILENAME);

    private final Context context = this;
    private GoogleMap mMap;

    public MapsActivity() throws FileNotFoundException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // set up dummy button
        final Button clickButton = (Button) findViewById(R.id.start_navigation_button);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // currently just a dummy, but the action makes it look more like a
                // real project!
                Toast.makeText(context, getString(R.string.toast_message), Toast.LENGTH_LONG).show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onMapReady(GoogleMap googleMap) {
        // set up map layer
        mMap = googleMap;

        // load coordinates from JSON file
        String test = loadCoordinates();
        // get all lat coords into one ArrayList
        final List<Double> testCoordinatesA = getLatCoordinates(test);
        // and all lngs into another
        final List<Double> testCoordinatesB = getLongCoordinates(test);


        // set up runnable handler
        Handler handler1 = new Handler();

        // set start position
        final LatLng latLng = new LatLng(37.3343947, -122.0464412);

        /*

        ATTENTION READER! There are two custom icon options I set up, the one currently commented
        out is just a green circle. I personally think it is easier to see, but I don't think
        it follows Material Design Specs as closely as the one below. If you want to use this icon,
        just uncomment it ( ctr - shift - / ), and replace the .icon(BitmapDescriptor...)); with
        .icon(markerIcon);

        Drawable circleDrawable = getResources().getDrawable(R.drawable.circle_shape);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);*/

        // sets up marker
        final MarkerOptions marker = new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(String.valueOf(R.mipmap.current_location)
                )));

        // adds icon to start position
        final Marker m = mMap.addMarker(marker);

        // iterate through the arrays. Two arrays means no i and i+1! Clever huh?
        for ( int i = 0; i < testCoordinatesA.size(); i++ ) {
            //needs to be declared final
            final int finalI = i;
            // set timeout thread
            handler1.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // update position of marker
                    m.setPosition(new LatLng(testCoordinatesA.get(finalI), testCoordinatesB.get(finalI)));
                }

            }, 500 * i); // currently set to 500 milliseconds, or 0.5 secs, per instructions
        }

        // start camera off at start position, zoomed in a little bit.
        goToLocationZoom(37.3343947,-122.0464412, 15);
        LatLng start = new LatLng(37.3343947,-122.0464412 );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
    }

    // resize icon to look good on all devices
    private Bitmap resizeMapIcons(String iconName) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 150, 150, false);
    }

    // zoom to location.
    private void goToLocationZoom(double lat, double lng, float zoom ) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }


// --Commented out by Inspection START (6/19/2017 4:35 PM):
//    // only necessary when using the small green circle icon
//    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
//        Canvas canvas = new Canvas();
//        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        canvas.setBitmap(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }
// --Commented out by Inspection STOP (6/19/2017 4:35 PM)


    // load JSON data into a String
    private String loadCoordinates() {

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
    // pretty self explanatory
    private List<Double> getLatCoordinates(String coordinates) {

        Pattern p = Pattern.compile("\\d{2,3}.\\d*");

        Matcher m = p.matcher(coordinates);
        List<String> coord = new ArrayList<>();

        while (m.find()) {
            coord.add(m.group());
        }

        List<Double> latArr = new ArrayList<>();

        for ( int i = 0; i < coord.size(); i++ ) {
            if ( Double.parseDouble(coord.get(i)) < 100 ) {
                latArr.add(Double.parseDouble(coord.get(i)));
            }
        }

        return latArr;
    }

    // gets the longitude from a JSON string
    // pretty self explanatory
    private List<Double> getLongCoordinates(String coordinates) {

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
