package me.madhvani.dwells.ui;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;

import me.madhvani.dwells.R;
import me.madhvani.dwells.data.CityLatLong;
import me.madhvani.dwells.model.Kot;
import me.madhvani.dwells.utils.BookmarkReaderWriter;

/**
 * A placeholder fragment containing a simple view.
 */
public class RouteActivityFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "RouteActivityFragment";
    private ArrayList<Kot> kots;
    private String latLng;

    public RouteActivityFragment() {
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mapView.onSaveInstanceState(savedInstanceState);
    }

    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static MapView mapView;
    private static final LatLng GENT = new LatLng(51.0878316,3.7237548);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        Bundle extras = getArguments();
        if (extras != null) {
            kots = extras.getParcelableArrayList("kots");
            latLng = extras.getString("city");
        }

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }


    private void setUpMap() {
        if (mMap == null || kots == null)
            return;

        MapsInitializer.initialize(getActivity());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target((CityLatLong.getCities().get(latLng)))
                        .zoom(10)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                        .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);

        // Instantiates a new Polyline object and adds points to define a rectangle
        ArrayList<LatLng> kotsLatLong = new ArrayList<>();
        for (int i = 0; i < kots.size(); i++) {
            kotsLatLong.add(new LatLng(kots.get(i).getLatitude(),kots.get(i).getLongitude()));
        }
        PolygonOptions rectOptions = new PolygonOptions().addAll(kotsLatLong)
            .fillColor(Color.argb(240,255,255,255));

        // Get back the mutable Polyline
        Polygon polygon = mMap.addPolygon(rectOptions);
    }
}
