package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

public class OrderActivity extends Activity {

    public static final String ORDER = "order";

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, AppSettings.sharedSettings(this).getLanguage());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);

        googleMap = mapView.getMap();
        googleMap.setMyLocationEnabled(true);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        TextView dateIconTextView = ( TextView )findViewById(R.id.icon_date);
        TextView locationIconTextView = ( TextView )findViewById(R.id.icon_location);
        TextView costIconTextView = ( TextView )findViewById(R.id.icon_cost);
        TextView distanceIconTextView = ( TextView )findViewById(R.id.icon_distance);
        TextView statusIconTextView = ( TextView )findViewById(R.id.icon_status);



        dateIconTextView.setTypeface(icomoonTypeface);
        locationIconTextView.setTypeface(icomoonTypeface);
        costIconTextView.setTypeface(icomoonTypeface);
        distanceIconTextView.setTypeface(icomoonTypeface);
        statusIconTextView.setTypeface(icomoonTypeface);

        TextView dateTextView = ( TextView )findViewById(R.id.date);
        TextView locationTextView = ( TextView )findViewById(R.id.location);
        TextView costTextView= ( TextView )findViewById(R.id.cost);
        TextView distanceTextView = ( TextView )findViewById(R.id.distance);
        TextView statusTextView = ( TextView )findViewById(R.id.status);

        TextView dateValueTextView = ( TextView )findViewById(R.id.date_value);
        TextView startLocationValueTextView = ( TextView )findViewById(R.id.start_location_value);
        TextView endLocationValueTextView = ( TextView )findViewById(R.id.end_location_value);
        TextView costValueTextView = ( TextView )findViewById(R.id.cost_value);
        TextView distanceValueTextView = ( TextView )findViewById(R.id.distance_value);
        TextView statusValueTextView = ( TextView )findViewById(R.id.status_value);


        dateTextView.setTypeface(robotoTypeface);
        locationTextView.setTypeface(robotoTypeface);
        costTextView.setTypeface(robotoTypeface);
        distanceTextView.setTypeface(robotoTypeface);
        statusTextView.setTypeface(robotoTypeface);


        dateValueTextView.setTypeface(robotoTypeface);
        startLocationValueTextView.setTypeface(robotoTypeface);
        endLocationValueTextView.setTypeface(robotoTypeface);
        costValueTextView.setTypeface(robotoTypeface);
        distanceValueTextView.setTypeface(robotoTypeface);
        statusValueTextView.setTypeface(robotoTypeface);


        Intent orderIntent = getIntent();
        Order order = orderIntent.getParcelableExtra(ORDER);

        dateValueTextView.setText(order.getOrderTime().toString());
        startLocationValueTextView.setText(order.getStartingPointName());
        endLocationValueTextView.setText(order.getEndingPointName());
        costValueTextView.setText(order.getMoneyAmount() + " / "+ order.getPaymentType());
        distanceValueTextView.setText("0");
        statusValueTextView.setText(order.getStatus());

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
