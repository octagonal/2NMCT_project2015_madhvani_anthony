package me.madhvani.dwells.ui;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.madhvani.dwells.BuildConfig;
import me.madhvani.dwells.R;
import me.madhvani.dwells.api.KotServiceAPI;
import me.madhvani.dwells.api.component.DaggerKotAPI;
import me.madhvani.dwells.api.component.KotAPI;
import me.madhvani.dwells.api.utilities.QueryBuilder;
import me.madhvani.dwells.model.Kot;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsActivity extends FragmentActivity {

    private static final String TAG = "MapsActivity";

    //TODO: Zet steden LatLng's in aparte struct
    private static final LatLng GENT = new LatLng(51.0878316,3.7237548);

    //http://stackoverflow.com/questions/14054122/associate-an-object-with-marker-google-map-v2
    private HashMap<Marker, Kot> markers = new HashMap <>();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(GENT)
                .zoom(10)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i(TAG, "URL of Kot object bound to clicked Marker: " + markers.get(marker).getUrl());

                Intent i = new Intent(getBaseContext(), KotDetail.class);
                i.putExtra("kot", markers.get(marker));
                startActivity(i);
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                View myContentView = getLayoutInflater().inflate(
                        R.layout.kot_marker, null);
                TextView tvTitle = ((TextView) myContentView
                        .findViewById(R.id.title));
                tvTitle.setText(marker.getTitle());
                TextView tvSnippet = ((TextView) myContentView
                        .findViewById(R.id.snippet));
                tvSnippet.setText(marker.getSnippet());

                String patternStr="([^\\/]*$)";
                Pattern p = Pattern.compile(patternStr);
                Matcher m = p.matcher(markers.get(marker).getUrl());
                String output = m.group(1).substring(0, 1).toUpperCase() + m.group(1).substring(1);
                return myContentView;
            }
        });

        KotAPI kotAPI = DaggerKotAPI.create();
        kotAPI.query().getKotByCity(QueryBuilder.StringBuilder("gent"), new Callback<List<Kot>>() {
            @Override
            public void success(List<Kot> kots, Response response) {
                for (int i = 0; i < kots.size(); i++) {
                    Marker m = mMap.addMarker(
                            new MarkerOptions()
                                    .position(
                                            new LatLng(kots.get(i).getLatitude(), kots.get(i).getLongitude())
                                    )
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.house))
                                    .title("€" + kots.get(i).getPrice().toString())
                                    .snippet(kots.get(i).getArea().toString() + "m²")
                    );
                    Log.v(TAG, "Kot URL: " + kots.get(i).getUrl());
                    markers.put(m, kots.get(i));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
