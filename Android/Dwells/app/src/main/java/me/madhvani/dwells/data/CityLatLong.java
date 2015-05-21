package me.madhvani.dwells.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

/**
 * Created by anthony on 15.21.5.
 */
public class CityLatLong {
    private static HashMap<String,LatLng> cities;

    static
    {
        cities = new HashMap<String, LatLng>();
        cities.put("Gent", new LatLng(51.0878316,3.7237548));
        cities.put("Kortrijk", new LatLng(50.82806, 3.265));
        cities.put("Leuven", new LatLng(50.8775, 4.70444));
        cities.put("Brugge", new LatLng(51.20944, 3.22528));
        cities.put("Brussel", new LatLng(50.84667, 4.35472));
        cities.put("Hasselt", new LatLng(50.92972, 5.33833));
        cities.put("Antwerpen", new LatLng(51.22139, 4.39722));
    }

    public static HashMap<String, LatLng> getCities() {
        return cities;
    }
}
