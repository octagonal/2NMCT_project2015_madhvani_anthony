package me.madhvani.dwells.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
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
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.madhvani.dwells.R;
import me.madhvani.dwells.api.component.DaggerKotAPI;
import me.madhvani.dwells.api.component.KotAPI;
import me.madhvani.dwells.api.utilities.QueryBuilder;
import me.madhvani.dwells.model.Kot;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsActivity extends FragmentActivity {

    private static final String TAG = "MapsActivity";

    //TODO: Zet steden LatLng's in aparte struct
    private static final LatLng GENT = new LatLng(51.0878316,3.7237548);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        vpPager.setAdapter(new MapNavigationPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabsStrip.setViewPager(vpPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static class MapNavigationPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        private static int NUM_ITEMS = 2;

        private int tabIcons[] = {R.drawable.ic_tab_map, R.drawable.ic_tab_bookmarks};

        public MapNavigationPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MapFragment.newInstance(0);
                case 1:
                   return BookmarksFragment.newInstance(1);
                default:
                    return null;
            }
        }

        @Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }

    }

    public static class BookmarksViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;

        public BookmarksViewHolder(View v) {
            super(v);
            title =  (TextView) v.findViewById(R.id.title);
        }
    }

    public static class BookmarksFragment extends Fragment {
        public static final String ARG_PAGE = "ARG_PAGE";
        private int page;
        private static RecyclerView recyclerView;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.bookmarks_fragment, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            // Setup layout manager for items
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            // Control orientation of the items
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.scrollToPosition(0);
            // Attach layout manager
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            // Bind adapter to recycler
            ArrayList<String> items = new ArrayList<String>();
            items.add(0,"lol");
            items.add(1,"lel");
            items.add(2,"kek");

            BookmarksRecyclerAdapter aItems = new BookmarksRecyclerAdapter(items);
            recyclerView.setAdapter(aItems);
            return view;
        }

        public static BookmarksFragment newInstance(int page) {
            BookmarksFragment fragmentFirst = new BookmarksFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, page);
            fragmentFirst.setArguments(args);
            return fragmentFirst;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            page = getArguments().getInt(ARG_PAGE);
        }
    }

    public static class MapFragment extends Fragment implements OnMapReadyCallback {
        // Store instance variables
        public static final String ARG_PAGE = "ARG_PAGE";
        public static final int ANIMATION_DURATION = 250;
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
        public void onPause(){
            super.onPause();
            mapView.onPause();
        }

        @Override
        public void onSaveInstanceState(Bundle savedInstanceState){
            super.onSaveInstanceState(savedInstanceState);
            mapView.onSaveInstanceState(savedInstanceState);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            bundle = savedInstanceState;
            page = getArguments().getInt(ARG_PAGE);
        }

        //http://stackoverflow.com/questions/14054122/associate-an-object-with-marker-google-map-v2
        private static HashMap<Marker, Kot> markers = new HashMap <>();
        public static GoogleMap mMap; // Might be null if Google Play services APK is not available.
        public static MapView mapView;
        public static Marker selectedMarker = null;

        public static Button webViewer;
        public static Button bookmarkAdder;

        private static String BOOKMARKS_FILENAME = "bookmarks";

        public static LinearLayout markerActions;

        public static SharedPreferences  mPrefs;

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
                    i.putExtra("kot",(Parcelable) markers.get(selectedMarker));
                    startActivity(i);
                }
            });

            bookmarkAdder = (Button) markerActions.findViewById(R.id.bookmark_adder);
            bookmarkAdder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        bookmarkWriterHandler(markers.get(selectedMarker));
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

        private void hideButtons(){
            if(selectedMarker != null)
                return;

            ObjectAnimator fadeAltAnim = ObjectAnimator.ofFloat(markerActions, View.ALPHA, 1, 0);
            fadeAltAnim.setDuration(ANIMATION_DURATION);
            fadeAltAnim.start();

        }

        private void showButtons(){
            if(selectedMarker != null)
                return;

            ObjectAnimator fadeAltAnim = ObjectAnimator.ofFloat(markerActions, View.ALPHA, 0, 1);
            fadeAltAnim.setDuration(ANIMATION_DURATION);
            fadeAltAnim.start();
        }

        private void setUpMap() {
            if (mMap == null)
                return; // Google Maps not available


            MapsInitializer.initialize(getActivity());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(GENT)
                    .zoom(10)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);

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
            kotAPI.query().getKotByCity(QueryBuilder.StringBuilder("gent"), new Callback<List<Kot>>() {
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

        //http://stackoverflow.com/a/1195078
        public class AppendingObjectOutputStream extends ObjectOutputStream {

            public AppendingObjectOutputStream(OutputStream out) throws IOException {
                super(out);
            }

            @Override
            protected void writeStreamHeader() throws IOException {
                // do not write a header, but reset:
                // this line added after another question
                // showed a problem with the original
                reset();
            }

        }

        private ArrayList<Kot> readItems() throws IOException {
            FileInputStream fis = getActivity().openFileInput(BOOKMARKS_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Kot kot = null;
            ArrayList<Kot> kots = new ArrayList<Kot>();

            try {
                while(true){
                    //http://stackoverflow.com/a/19213702
                    try {
                        kot = (Kot) ois.readObject();
                        Log.v(TAG, "Reading: " + kot.getUrl());
                        kots.add(kot);
                    } catch (EOFException e) {
                        Log.v(TAG, e.getMessage());
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ois.close();

            if(kot != null) {
                return kots;
            } else {
                return new ArrayList<Kot>();
            }
        }

        //http://stackoverflow.com/a/16111797
        private void writeItems(Kot kot) throws IOException {
            FileOutputStream fos = getActivity().openFileOutput(BOOKMARKS_FILENAME, Context.MODE_PRIVATE);
            //Zet naar MODE_APPEND na run
            ObjectOutputStream oos;
            if(new File(BOOKMARKS_FILENAME).exists()) {
                oos = new AppendingObjectOutputStream(fos);
            } else {
                oos = new ObjectOutputStream(fos);
            }
            oos.writeObject((Serializable) kot);
            oos.close();
        }

        private void bookmarkWriterHandler(Kot kot) throws IOException {
            Log.v(TAG, "Writing: " + kot.getUrl());
            writeItems(kot);
            readItems();
        }
    }

    public static class BookmarksRecyclerAdapter extends RecyclerView.Adapter<BookmarksViewHolder> {
        private List<String> items;

        // Provide a reference to the views for each data item
        // Provide access to all the views for a data item in a view holder

        // Provide a suitable constructor (depends on the kind of dataset)
        public BookmarksRecyclerAdapter(List<String> items) {
            this.items = items;
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return this.items.size();
        }

        // Create new items (invoked by the layout manager)
        // Usually involves inflating a layout from XML and returning the holder
        @Override
        public BookmarksViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.bookmarks_card, viewGroup, false);
            return new BookmarksViewHolder(itemView);
        }

        // Replace the contents of a view (invoked by the layout manager)
        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(BookmarksViewHolder viewHolder, int position) {
            String item = items.get(position);
            viewHolder.title.setText(item);
        }
    }

}
