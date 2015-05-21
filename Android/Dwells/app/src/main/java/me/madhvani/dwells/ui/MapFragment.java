package me.madhvani.dwells.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.madhvani.dwells.R;
import me.madhvani.dwells.api.component.DaggerKotAPI;
import me.madhvani.dwells.api.component.KotAPI;
import me.madhvani.dwells.api.utilities.QueryBuilder;
import me.madhvani.dwells.model.Kot;
import me.madhvani.dwells.utils.BookmarkReaderWriter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by anthony on 15.17.5.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    // Store instance variables
    public static final String ARG_PAGE = "ARG_PAGE";

    private static final LatLng GENT = new LatLng(51.0878316,3.7237548);
    private static final LatLng KORTRIJK = new LatLng(50.82806, 3.265);

    public static final int ANIMATION_DURATION = 250;
    private static final String TAG = "MapFragment";
    private int page;

    private static Bundle bundle;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;
        page = getArguments().getInt(ARG_PAGE);
    }


    public void changeLocation(LatLng latLng){
        if(mMap != null) {
            Log.v(TAG, "Changing location");
            CameraPosition cameraPosition = getCameraPosition(latLng);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
        }
    }

    //http://stackoverflow.com/questions/14054122/associate-an-object-with-marker-google-map-v2
    private static HashMap<Marker, Kot> markers = new HashMap<>();
    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static MapView mapView;
    public static Marker selectedMarker = null;

    public static Button webViewer;
    public static Button bookmarkAdder;

    public static LinearLayout markerActions;

    public static SharedPreferences mPrefs;

    // newInstance constructor for creating fragment with arguments
    public static MapFragment newInstance(int page) {
        MapFragment fragmentFirst = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        markerActions = (LinearLayout) view.findViewById(R.id.marker_actions);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.getMapAsync(this);
        mapView.onCreate(bundle);

        webViewer = (Button) markerActions.findViewById(R.id.web_viewer);
        webViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getBaseContext(), KotDetail.class);
                i.putExtra("kot", (Parcelable) markers.get(selectedMarker));
                startActivity(i);
            }
        });

        bookmarkAdder = (Button) markerActions.findViewById(R.id.bookmark_adder);
        bookmarkAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BookmarkReaderWriter bookmarkReaderWriter = new BookmarkReaderWriter(getActivity().getApplicationContext(),
                            ((MapsActivity) getActivity()).getSelectedItem());
                    bookmarkReaderWriter.writeItem(markers.get(selectedMarker));

                    //bookmarkWriterHandler(markers.get(selectedMarker));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    private void hideButtons() {
        if (selectedMarker != null)
            return;

        ObjectAnimator fadeAltAnim = ObjectAnimator.ofFloat(markerActions, View.ALPHA, 1, 0);
        fadeAltAnim.setDuration(ANIMATION_DURATION);
        fadeAltAnim.start();

    }

    private void showButtons() {
        if (selectedMarker != null)
            return;

        ObjectAnimator fadeAltAnim = ObjectAnimator.ofFloat(markerActions, View.ALPHA, 0, 1);
        fadeAltAnim.setDuration(ANIMATION_DURATION);
        fadeAltAnim.start();
    }

    private void setUpMap() {
        if (mMap == null)
            return; // Google Maps not available


        MapsInitializer.initialize(getActivity());

        CameraPosition cameraPosition = getCameraPosition(GENT);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);

        changeLocation(GENT);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selectedMarker != null) {
                    selectedMarker = null;
                    hideButtons();
                }
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                showButtons();
                selectedMarker = marker;

                View myContentView = getActivity().getLayoutInflater().inflate(
                        R.layout.kot_marker, null);
                TextView tvTitle = ((TextView) myContentView
                        .findViewById(R.id.title));
                tvTitle.setText(marker.getTitle());
                TextView area = ((TextView) myContentView
                        .findViewById(R.id.area));
                area.setText(marker.getSnippet());

                String patternStr = "([^\\/]*$)";
                Pattern p = Pattern.compile(patternStr);
                Matcher m = p.matcher(markers.get(marker).getUrl());
                m.find();
                String output = m.group(1).substring(0, 1).toUpperCase() + m.group(1).substring(1);

                output = output.replaceAll("-", " ");

                    TextView location = ((TextView) myContentView
                            .findViewById(R.id.location));
                location.setText(output);

                return myContentView;
            }
        });

        KotAPI kotAPI = DaggerKotAPI.create();
        kotAPI.query().getAllKots(new Callback<List<Kot>>() {
            @Override
            public void success(List<Kot> kots, Response response) {
                for (int i = 0; i < kots.size(); i++) {
                    Marker m = mMap.addMarker(
                            new MarkerOptions()
                                    .position(
                                            new LatLng(kots.get(i).getLatitude(), kots.get(i).getLongitude())
                                    )
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_house))
                                    .title("€" + kots.get(i).getPrice().toString())
                                    .snippet(kots.get(i).getArea().toString() + "m²")
                    );
                    markers.put(m, kots.get(i));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private CameraPosition getCameraPosition(LatLng latLng) {
        return new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(12)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                    .build();
    }

    private static String BOOKMARKS_FILENAME = "bookmarks";

    //TODO: Maak gebruik van nieuwe interface, spooky

    private ArrayList<Kot> readItems() throws IOException {
        ArrayList<Kot> kots = null;
        FileInputStream fis = getActivity().getApplicationContext().openFileInput(BOOKMARKS_FILENAME);
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(fis);
        } catch (EOFException e){
            Log.v(TAG, "EOF: " + e.getMessage());
            return new ArrayList<Kot>();
        }

        try {
            //http://stackoverflow.com/a/19213702
            kots = (ArrayList<Kot>) ois.readObject();
        } catch (EOFException e) {
            Log.v(TAG,"EOF was reached: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.v(TAG, "Could not readObject -> Kots: " + e.getMessage());
        } finally {
            ois.close();
        }

        if (kots != null) {
            return kots;
        } else {
            return new ArrayList<Kot>();
        }
    }

    //http://stackoverflow.com/a/16111797
    private void writeItems(ArrayList<Kot> kots) throws IOException {

        FileOutputStream fos = getActivity().getApplicationContext().openFileOutput(BOOKMARKS_FILENAME, Context.MODE_PRIVATE);
        ObjectOutputStream oos;

        Log.v(TAG, "Creating new bookmarks");
        oos = new ObjectOutputStream(fos);
        oos.writeObject((Serializable) kots);
    }

    private void bookmarkWriterHandler(Kot kot) throws IOException {
        Log.v(TAG, "Writing: " + kot.getUrl());

        ArrayList<Kot> kots = readItems();
        kots.add(kot);
        writeItems(kots);

        kots = readItems();

        for (int i = 0; i < kots.size(); i++) {
            Log.v(TAG, kots.get(i).getUrl());
        }
    }
}
