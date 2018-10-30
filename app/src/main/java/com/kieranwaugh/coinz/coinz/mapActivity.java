package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
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
import java.util.Map;


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


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Mapbox.getInstance(this, getString(R.string.access_token));
            setContentView(R.layout.activity_map);
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(1);
            menuItem.setChecked(true);
            ActivityOptions options1 = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_left);
            ActivityOptions options2 = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_right);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.navigation_stats:
                            Intent intent1 = new Intent(mapActivity.this, statsActivity.class);
                            startActivity(intent1,options2.toBundle());
                            break;

                        case R.id.navigation_map:

                            break;

                        case R.id.navigation_bank:
                            Intent intent3 = new Intent(mapActivity.this, bankActivity.class);
                            startActivity(intent3, options1.toBundle());
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
            }else {
                Log.d(tag, "[onMapReady] problem finding map data, taking from server");
                mapData = DownloadCompleteRunner.result;
            }

            if (mapboxMap == null) {
                Log.d(tag, "[onMapReady] mapBox is null");
            }else{
                map = mapboxMap;
                List<Feature> features = FeatureCollection.fromJson(mapData).features();
                GeoJsonSource source = new GeoJsonSource("geojson", mapData);
                mapboxMap.addSource(source);
                mapboxMap.addLayer(new LineLayer("geojson", "geojson"));

                for (int i = 0; i < features.size(); i++){
                    try {
                        JSONObject jsonObject = new JSONObject(features.get(i).toJson());
                        JSONArray coordinates = jsonObject.getJSONObject("geometry").getJSONArray("coordinates");
                        double lng = Double.parseDouble(coordinates.get(0).toString());
                        double lat = Double.parseDouble(coordinates.get(1).toString());
                        String type = jsonObject.getJSONObject("geometry").getString("type");
                        String id = jsonObject.getJSONObject("properties").getString("id");
                        double value = jsonObject.getJSONObject("properties").getDouble("value");
                        String strValue = String.valueOf(value);
                        String currency = jsonObject.getJSONObject("properties").getString("currency");
                        int markerSymbol = jsonObject.getJSONObject("properties").getInt("marker-symbol");
                        String color = jsonObject.getJSONObject("properties").getString("marker-color");
                        coin coin = new coin(id, value, currency, lng,lat);
                        coin.coins.put(id, coin);
                        mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .title(currency)
                        .setSnippet("Value - " + strValue)
                        );
                        Log.d(tag, "[onMapReady] adding marker " + i + " to the map");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            enableLocation();
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
            Log.d(tag, "[onResume] on resume test before");
            mapView.onResume();
            Log.d(tag, "[onResume] on resume test after");
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
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.threebutton, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.threebutton_signout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(mapActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





}
