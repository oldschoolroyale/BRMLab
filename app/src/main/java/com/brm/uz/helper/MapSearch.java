package com.brm.uz.helper;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.brm.uz.R;
import com.brm.uz.adapter.PlaceAutoSuggestAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapSearch extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    GoogleMap map;
    SupportMapFragment supportMapFragment;
    FloatingActionButton floatBtn, synchButton;
    Double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        final AutoCompleteTextView autoCompleteTextView=findViewById(R.id.autocomplete);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(MapSearch.this,android.R.layout.simple_list_item_1));

        floatBtn = findViewById(R.id.map_search_float_button);
        synchButton = findViewById(R.id.map_search_synch);
        synchButton.setOnClickListener(this);
        floatBtn.setOnClickListener(this);


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps_search_fragment);

        supportMapFragment.getMapAsync(this);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LatLng latLng= getLatLngFromAddress(autoCompleteTextView.getText().toString());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                map.clear();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                map.addMarker(markerOptions);
                lat = latLng.latitude;
                lon = latLng.longitude;
            }
        });
    }
    private LatLng getLatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(MapSearch.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address singleaddress=addressList.get(0);
                LatLng latLng=new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
                return latLng;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng defaultMap = new LatLng(41.26465, 69.21627);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultMap, 10));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude+ " : " + latLng.longitude);
                map.clear();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                map.addMarker(markerOptions);
                lat = latLng.latitude;
                lon = latLng.longitude;


            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.map_search_float_button:
                if (lat != null || lon != null){
                    Intent intent = new Intent();
                    intent.putExtra("lat", lat.toString());
                    intent.putExtra("lon", lon.toString());
                    setResult(RESULT_OK, intent);
                    finish();

                }
                else {Toast.makeText(getApplicationContext(), "Ничего не выбрано", Toast.LENGTH_LONG).show();}

            break;
            case R.id.map_search_synch:
                if (map.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                else {map.setMapType(GoogleMap.MAP_TYPE_NORMAL);}
                break;
        }
    }
}
