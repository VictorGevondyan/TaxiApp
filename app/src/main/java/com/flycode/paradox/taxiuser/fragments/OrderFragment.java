package com.flycode.paradox.taxiuser.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flycode.paradox.taxiuser.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

public class OrderFragment extends Fragment {
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View orderView = inflater.inflate(R.layout.fragment_order, container, false);

        mapView = (MapView) orderView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        googleMap = mapView.getMap();
        googleMap.setMyLocationEnabled(true);

        return orderView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
