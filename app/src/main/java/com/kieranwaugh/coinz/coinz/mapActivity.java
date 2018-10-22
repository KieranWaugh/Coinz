package com.kieranwaugh.coinz.coinz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class mapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {


        private String tag = "MapActivity";
        private MapView mapView;
        private MapboxMap map;

        private PermissionsManager permissionsManager;
        private LocationEngine locationEngine;
        private LocationLayerPlugin locationLayerPlugin;
        private Location originLocation;
        public String mapData;
        private final String savedMapData = "mapData";
    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

        //List<FeatureCollection> features = new ArrayList<>();


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Mapbox.getInstance(this, getString(R.string.access_token));
            setContentView(R.layout.activity_map);
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(1);
            menuItem.setChecked(true);
            //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.navigation_stats:
                            Intent intent1 = new Intent(mapActivity.this, statsActivity.class);
                            startActivity(intent1);
                            break;

                        case R.id.navigation_map:

                            break;

                        case R.id.navigation_bank:
                            Intent intent3 = new Intent(mapActivity.this, bankActivity.class);
                            startActivity(intent3);
                            break;
                    }
                    return false;
                }
            });

            mapView = findViewById(R.id.mapboxMapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

        @Override
        public void onMapReady(MapboxMap mapboxMap) {

            SharedPreferences FromFile = getSharedPreferences(savedMapData, Context.MODE_PRIVATE);
            if (FromFile.contains(date)){
                mapData = FromFile.getString(date, "");
                Log.d(tag, "[onMapReady] map data taken from file");
                //Log.d(tag, "[onMapReady] " + mapData);
            }else {
                Log.d(tag, "[onMapReady] problem finding map data, taking from server");
                mapData = DownloadCompleteRunner.result;

            }


            List<Feature> features = FeatureCollection.fromJson(mapData).features();

            if (mapboxMap == null) {
                Log.d(tag, "[onMapReady] mapBox is null");
            }else{
                map = mapboxMap;
                System.out.println("feature string " + features.get(0));
//                for (int i = 0; i < features.size(); i++) { // create coin object from the features
//
//                }


                //mapboxMap.addSource();
                // Set user interface options
                //map.getUiSettings().setCompassEnabled(true);
                //map.getUiSettings().setZoomControlsEnabled(true);

                // Make location information available
                enableLocation();
            }
        }

        private void enableLocation() {
            if (PermissionsManager.areLocationPermissionsGranted(this)) {
                Log.d(tag, "[enableLocation] Permissions are granted");
                initializeLocationEngine();
                initializeLocationLayer();
            }else{
                Log.d(tag, "Permissions are not granted");
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(this);
            }
        }

        @SuppressWarnings("MissingPermission")
        private void initializeLocationEngine(){
            locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
            locationEngine.setInterval(5000); // preferably every 5 seconds
            locationEngine.setFastestInterval(1000); // at most every second
            locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
            locationEngine.activate();

            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                originLocation = lastLocation;
                setCameraPosition(lastLocation);
            }else{
                locationEngine.addLocationEngineListener(this);
            }
        }

        @SuppressWarnings("MissingPermission")
        private void initializeLocationLayer() {
            if (mapView == null) {
                Log.d(tag, "mapView is null");
            }else{
                if (map == null) {
                    Log.d(tag, "map is null");
                }else{
                    locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
                    locationLayerPlugin.setLocationLayerEnabled(true);
                    locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
                    locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
                }
            }
        }

        private void setCameraPosition(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                Log.d(tag, "[onLocationChanged] location is null");
            }else{
                Log.d(tag, "[onLocationChanged] location is not null");
                originLocation = location;
                setCameraPosition(location);
            }
        }

        @Override
        @SuppressWarnings("MissingPermission")
        public void onConnected() {
            Log.d(tag, "[onConnected] requesting location updates");
            locationEngine.requestLocationUpdates();
        }

        @Override
        public void onExplanationNeeded(List<String> permissionsToExplain){
            Log.d(tag, "Permissions: " + permissionsToExplain.toString());
            // Present toast or dialog.
        }

        @Override
        public void onPermissionResult(boolean granted) {
            Log.d(tag, "[onPermissionResult] granted == " + granted);
            if (granted) {
                enableLocation();
            }else{
                // Open a dialogue with the user
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
            mapView.onStart();
        }

        @Override
        protected void onResume(){
            super.onResume();
            mapView.onResume();
        }

        @Override
        protected void onPause(){
            super.onPause();
            mapView.onPause();
        }

        @Override
        protected void onStop(){
            super.onStop();
            mapView.onStop();
            SharedPreferences FromFile = getSharedPreferences(savedMapData, Context.MODE_PRIVATE);
            if (FromFile.contains(date)){
                Log.d(tag, "[onStop] mapData already saved");
            }else{
                Log.d(tag, "[onStop] New map, Saving mapData");
                SharedPreferences settings = getSharedPreferences("mapData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(date,mapData);
                editor.apply();
            }
        }

        @Override
        public void onLowMemory(){
            super.onLowMemory();
            mapView.onLowMemory();
        }

        @Override
        protected void  onDestroy(){
            super.onDestroy();
            mapView.onDestroy();
        }

        @Override
        protected void onSaveInstanceState(Bundle outState){
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }
}
