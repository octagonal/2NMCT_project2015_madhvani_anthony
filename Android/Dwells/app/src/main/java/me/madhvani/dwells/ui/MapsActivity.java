package me.madhvani.dwells.ui;

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
        vpPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        //vpPager.setClipToPadding(false);
        //vpPager.setPageMargin(12);

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabsStrip.setViewPager(vpPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        private static int NUM_ITEMS = 1;

        private int tabIcons[] = {R.drawable.ic_tab_map, R.drawable.ic_tab_bookmarks};

        public MyPagerAdapter(FragmentManager fragmentManager) {
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
                //case 1:
                  //  return BookmarksFragment.newInstance(1);
                default:
                    return null;
            }
        }

        @Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }

    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;

        public ContactViewHolder(View v) {
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

            SimpleRecyclerAdapter aItems = new SimpleRecyclerAdapter(items);
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

        // Store instance variables based on arguments passed
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
            mapView = (MapView) view.findViewById(R.id.map);
            mapView.getMapAsync(this);
            mapView.onCreate(bundle);

            return view;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            setUpMap();
            Log.v(TAG,"MapView already initialized");
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

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.i(TAG, "URL of Kot object bound to clicked Marker: " + markers.get(marker).getUrl());

                    Intent i = new Intent(getActivity().getBaseContext(), KotDetail.class);
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
                    View myContentView = getActivity().getLayoutInflater().inflate(
                            R.layout.kot_marker, null);
                    TextView tvTitle = ((TextView) myContentView
                            .findViewById(R.id.title));
                    tvTitle.setText(marker.getTitle());
                    TextView area = ((TextView) myContentView
                            .findViewById(R.id.area));
                    area.setText(marker.getSnippet());

                    String patternStr="([^\\/]*$)";
                    Pattern p = Pattern.compile(patternStr);
                    Matcher m = p.matcher(markers.get(marker).getUrl());
                    m.find();
                    String output = m.group(1).substring(0, 1).toUpperCase() + m.group(1).substring(1);
                    Log.v(TAG, "Output location: " + output.replaceAll("-"," "));

                    output = output.replaceAll("-"," ");

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

    public static class SimpleRecyclerAdapter extends RecyclerView.Adapter<ContactViewHolder> {
        private List<String> items;

        // Provide a reference to the views for each data item
        // Provide access to all the views for a data item in a view holder

        // Provide a suitable constructor (depends on the kind of dataset)
        public SimpleRecyclerAdapter(List<String> items) {
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
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.bookmarks_card, viewGroup, false);
            return new ContactViewHolder(itemView);
        }

        // Replace the contents of a view (invoked by the layout manager)
        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(ContactViewHolder viewHolder, int position) {
            String item = items.get(position);
            viewHolder.title.setText(item);
        }
    }

}
