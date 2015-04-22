package me.madhvani.dwells.ui;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import me.madhvani.dwells.BuildConfig;
import me.madhvani.dwells.R;
import me.madhvani.dwells.api.KotService;
import me.madhvani.dwells.model.Kot;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    void setMarkers(List<Kot> kots){

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
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", "Dwells/" + BuildConfig.VERSION_NAME + " (Android" + "; cd:" + Build.VERSION.CODENAME + "; si:" + Build.VERSION.SDK_INT + "; rv:" + Build.VERSION.RELEASE + ")");
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(me.madhvani.dwells.api.Constants.ENDPOINT)
                .setRequestInterceptor(requestInterceptor)
                .build();

        KotService service = restAdapter.create(KotService.class);
        service.getKotByCity("eq.gent", new Callback<List<Kot>>() {
            @Override
            public void success(List<Kot> kots, Response response) {
                for (int i = 0; i < kots.size(); i++) {
                    mMap.addMarker(
                            new MarkerOptions().position(
                                    new LatLng( kots.get(i).getLatitude(), kots.get(i).getLongitude() )
                            )
                    );
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
