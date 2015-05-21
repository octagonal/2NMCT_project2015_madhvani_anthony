package me.madhvani.dwells.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import me.madhvani.dwells.R;
import me.madhvani.dwells.model.Kot;

public class RouteActivity extends FragmentActivity {

    private ArrayList<Kot> kots;
    private String latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        if (savedInstanceState == null) {
            kots = getIntent().getParcelableArrayListExtra("kots");
            latLng = getIntent().getStringExtra("city");

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Bundle extras1 = new Bundle();
            extras1.putParcelableArrayList("kots", kots);
            extras1.putString("city", latLng);

            RouteActivityFragment fragment = new RouteActivityFragment();
            fragment.setArguments(extras1);
            fragmentTransaction.add(R.id.container, fragment);
            fragmentTransaction.commit();
        }
    }
}
