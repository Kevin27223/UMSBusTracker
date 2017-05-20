package com.example.cheeseng.umsbustracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chee Seng on 22-Nov-16.
 */

public class RouteActivity extends AppCompatActivity {

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> routeCollection;
    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        createGroupList();

        createCollection();

        Toolbar toolbar = (Toolbar) findViewById(R.id.route_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        expListView = (ExpandableListView) findViewById(R.id.route_list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, groupList, routeCollection);
        expListView.setAdapter(expListAdapter);

        setGroupIndicatorToRight();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add("Campus");
        groupList.add("Usia");
        groupList.add("Kingfisher");
        groupList.add("Sri Angkasa");
    }

    private void createCollection() {
        String[] campusModels = { "DKP Lama - K.Resital", "K.Resital - DKP Baru",
                "DKP Baru - K.Resital", "K.Resital - FKJ", "FKJ - PPIB", "PPIB - DKP Lama"};
        String[] usiaModels = { "USIA - FSSA", "FSSA - PPIB", "PPIB - DKP Lama",
                "DKP Lama - DKP Baru", "DKP Baru - DKP Lama", "DKP Lama - PPIB", "PPIB - FKJ",
                "FKJ - USIA"};
        String[] kingfisherModels = { "KF - FSSA", "FSSA - PPIB", "PPIB - DKP Lama",
                "DKP Lama - DKP Baru", "DKP Baru - DKP Lama", "DKP Lama - PPIB", "PPIB - FKJ",
                "FKJ - KF" };
        String[] angkasaModels = { "Angkasa - FSSA", "FSSA - PPIB", "PPIB - DKP Lama",
                "DKP Lama - DKP Baru", "DKP Baru - DKP Lama", "DKP Lama - PPIB", "PPIB - FKJ",
                "FKJ - Angkasa" };

        routeCollection = new LinkedHashMap<String, List<String>>();

        for (String route : groupList) {
            if (route.equals("Campus")) {
                loadChild(campusModels);
            } else if (route.equals("Usia"))
                loadChild(usiaModels);
            else if (route.equals("Kingfisher"))
                loadChild(kingfisherModels);
            else
                loadChild(angkasaModels);

            routeCollection.put(route, childList);
        }
    }

    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}
