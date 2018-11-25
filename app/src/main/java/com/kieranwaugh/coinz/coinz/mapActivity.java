package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@IgnoreExtraProperties
public class mapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {


    private String tag = "MapActivity";
    private MapView mapView;
    private MapboxMap map;
    private LocationEngine locationEngine;
    private Location originLocation;
    private Location previousLocation;
    public String mapData;
    private double distanceWalked;
    private FloatingActionButton collectButton;
    private final String savedMapData = "mapData";
    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
    String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    public List<coin> coinsList = new ArrayList<>();
    public HashMap<String, Marker> markers= new HashMap<>();
    public ArrayList<Marker> collectedMarkers = new ArrayList<>();
    public ArrayList<String> collected = new ArrayList<>();
    public ArrayList<coin> collectedCoins = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private String statsREF;
    private PlayerStats stats;



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getStats();
            Mapbox.getInstance(this, getString(R.string.access_token));
            setContentView(R.layout.activity_map);
            BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(1);
            menuItem.setChecked(true);
            ActivityOptions options1 = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
            ActivityOptions options2 = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                switch (item.getItemId()){
                    case R.id.navigation_stats:
                        Intent intent1 = new Intent(mapActivity.this, Test_Player_Activity.class);
                        intent1.putExtra("stats", stats);
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
            });

            mapView = findViewById(R.id.mapboxMapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
            //getCollected();
        collectButton = findViewById(R.id.floatingActionButton2);
        collectButton.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onMapReady(MapboxMap mapboxMap) {
            Log.d(tag, "[onMapReady] " + collected.toString());
            SharedPreferences FromFile = getSharedPreferences(savedMapData, Context.MODE_PRIVATE);
            if (FromFile.contains(date)){
                mapData = FromFile.getString(date, "");
                Log.d(tag, "[onMapReady] map data taken from file");
                Log.d(tag, mapData);
            }else {
                Log.d(tag, "[onMapReady] problem finding map data, taking from server");
                mapData = DownloadCompleteRunner.result;
            }
            if (mapboxMap == null) {
                Log.d(tag, "[onMapReady] mapBox is null");
            }else{

                map = mapboxMap;

                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                CollectionReference cr = rootRef.collection("wallet").document(email).collection("collected ("+dateDB +")");
                cr.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            coin c = document.toObject(coin.class);
                            assert c != null;
                            collected.add(c.getId());
                            collectedCoins.add(c);

                        }
                    }

                    List<Feature> features = FeatureCollection.fromJson(mapData).features();
                    enableLocation();
                    assert features != null;
                    for (int i = 0; i < features.size(); i++){
                        try {

                            JSONObject jsonObject = new JSONObject(features.get(i).toJson());
                            JSONArray coordinates = jsonObject.getJSONObject("geometry").getJSONArray("coordinates");
                            double lng = Double.parseDouble(coordinates.get(0).toString());
                            double lat = Double.parseDouble(coordinates.get(1).toString());
                            String id = jsonObject.getJSONObject("properties").getString("id");
                            double value = jsonObject.getJSONObject("properties").getDouble("value");
                            String strValue = String.valueOf(value);
                            String currency = jsonObject.getJSONObject("properties").getString("currency");
                            int markerSymbol = jsonObject.getJSONObject("properties").getInt("marker-symbol");
                            String color = jsonObject.getJSONObject("properties").getString("marker-color");


                            if (!collected.contains(id)){
                                Context cxt = getApplicationContext();
                                int iconInt = getIcon("marker_"+color.substring(1, color.length())+"_"+ markerSymbol, cxt);
                                IconFactory iconFactory = IconFactory.getInstance(this);
                                Icon icon =  iconFactory.fromResource(iconInt);
                                MarkerOptions mo = new MarkerOptions().position(new LatLng(lat,lng)).title(currency).setSnippet("Value: " + strValue).icon(icon);
                                Marker m = mapboxMap.addMarker(mo);
                                coin coin = new coin(id, value, currency, lng,lat,false);
                                coinsList.add(coin);
                                markers.put(id, m);
                            }else{
                                continue;
                            }
                            Log.d(tag, "[onMapReady] adding marker " + i + " to the map");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        private void enableLocation() {
            if (PermissionsManager.areLocationPermissionsGranted(this)) {
                Log.d(tag, "[enableLocation] Permissions are granted");
                initializeLocationEngine();
                initializeLocationLayer();
            }else{
                Log.d(tag, "Permissions are not granted");
                PermissionsManager permissionsManager = new PermissionsManager(this);
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
                    LocationLayerPlugin locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
                    locationLayerPlugin.setLocationLayerEnabled(true);
                    locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
                    locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
                    Lifecycle lifecycle = getLifecycle();
                    lifecycle.addObserver(locationLayerPlugin);
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
                if (originLocation != null){
                    if (previousLocation == null){
                        previousLocation = originLocation;
                    }
                    Log.d(tag, "origin " + originLocation);
                    Log.d(tag, "prev " + previousLocation);
                    distanceWalked += distance(originLocation.getLatitude(), originLocation.getLongitude(), previousLocation.getLatitude(), previousLocation.getLongitude());
                    previousLocation = originLocation;
                    Log.d(tag, "distance " + distanceWalked);

                }
                originLocation = location;

                setCameraPosition(location);



                for (int i = 0; i < coinsList.size(); i++){
                    coin coin = coinsList.get(i);
                    Marker m = (markers.get(coin.getId()));
                    if (!collectedMarkers.contains(m)){
                        assert m != null;
                        LatLng loc = m.getPosition();
                        if (collectOK(loc)){
                            collectButton.setVisibility(View.VISIBLE);
                            collectButton.setOnClickListener(v -> {
                                collectedMarkers.add(m);
                                map.removeMarker(m);

                                db.collection("wallet").document(email).collection("collected ("+dateDB +")")
                                        .add(coin)
                                        .addOnSuccessListener(documentReference -> {
                                            Log.d(tag, "coin collected with id " + coin.getId());
                                            collectButton.setVisibility(View.INVISIBLE);
                                            Snackbar.make(findViewById(R.id.viewSnack), "Collected " + m.getTitle() + " of  " + m.getSnippet(),Snackbar.LENGTH_SHORT).show();

                                        })
                                        .addOnFailureListener(e -> Log.w(tag, "Error collecting coin", e));
                            });

                        }
                    }

                }

            }

        }


        public boolean collectOK(LatLng coin){
            double UserLat = originLocation.getLatitude();
            double UserLng = originLocation.getLongitude();
            double markLat = coin.getLatitude();
            double marklng = coin.getLongitude();

            return (distance(UserLat, UserLng, markLat, marklng) <= 25);
            //Log.d(tag, "[collectedOK] distance to marker is " + (distance(UserLat, UserLng, markLat, marklng) ));

        }

        public double distance(double lat1, double lng1, double lat2, double lng2){ //https://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
            double earthRadius = 6371000; //meters
            double dLat = Math.toRadians(lat2-lat1);
            double dLng = Math.toRadians(lng2-lng1);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLng/2) * Math.sin(dLng/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            return (float) (earthRadius * c);

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
                Log.d(tag, "permissions not granted");
                //add dialog here
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
            mapView.onStart();
            if(locationEngine != null){

                try {
                    locationEngine.requestLocationUpdates();
                } catch(SecurityException ignored) {}
                locationEngine.addLocationEngineListener(this);
            }
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
            Log.d(tag, "onStop" + collectedCoins.size());
            double alreadyWalked = stats.getDistance();
            Log.d(tag, "[onStop] " + distanceWalked);
            Log.d(tag, "[onStop] " + statsREF);
            int collectedDOLRS = 0;
            int collectedQUIDS = 0;
            int collectedPENYS = 0;
            int collectedSHILS = 0;
            for (int i = 0; i < collectedCoins.size(); i++){
                coin c = collectedCoins.get(i);
                switch (c.getCurrency()){
                    case "DOLR":
                        collectedDOLRS +=1;
                        break;
                    case "QUID":
                        collectedQUIDS +=1;
                        break;
                    case "PENY"  :
                        collectedPENYS +=1;
                        break;
                    case "SHIL":
                        collectedSHILS +=1;
                        break;
                }
            }

            double newDistance = alreadyWalked += distanceWalked;
            FirebaseFirestore inst = FirebaseFirestore.getInstance();
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("distance", newDistance);
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("dolrs", collectedDOLRS);
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("quids", collectedQUIDS);
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("shils", collectedSHILS);
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("penys", collectedPENYS);

            super.onStop();
            mapView.onStop();
            if(locationEngine != null){
                locationEngine.removeLocationEngineListener(this);
                locationEngine.removeLocationUpdates();
            }
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
            int collectedDOLRS = 0;
            int collectedQUIDS = 0;
            int collectedPENYS = 0;
            int collectedSHILS = 0;
            for (int i = 0; i < collectedCoins.size(); i++){
                coin c = collectedCoins.get(i);
                switch (c.getCurrency()){
                    case "DOLR":
                        collectedDOLRS +=1;
                        break;
                    case "QUID":
                        collectedQUIDS +=1;
                        break;
                    case "PENY"  :
                        collectedPENYS +=1;
                        break;
                    case "SHIL":
                        collectedSHILS +=1;
                        break;
                }
            }
            double alreadyWalked = stats.getDistance();
            double newDistance = alreadyWalked += distanceWalked;
            FirebaseFirestore inst = FirebaseFirestore.getInstance();
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("distance", newDistance);
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("dolrs", collectedDOLRS);
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("quids", collectedQUIDS);
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("shils", collectedSHILS);
            inst.collection("user").document(email).collection("STATS").document(statsREF).update("penys", collectedPENYS);
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
            startActivity(new Intent(mapActivity.this, MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected static int getIcon(final String resName, final Context cxt){
        final int ResourceID = cxt.getResources().getIdentifier(resName, "drawable", cxt.getApplicationContext().getPackageName());
        if (ResourceID == 0){
            throw new IllegalArgumentException();
        }else{
            return ResourceID;
        }
    }

    public void getStats(){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("user").document(email).collection("STATS");
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    statsREF = (document.getId());
                    stats = document.toObject(PlayerStats.class);
                }
            }
            assert stats != null;
            Log.d(tag, "[getStats] " + stats.getDistance());
        });
    }


    @Override
    public void onBackPressed(){
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.bottom_down, R.anim.nothing);
        startActivity(new Intent(mapActivity.this, MainActivity.class), options.toBundle());
    }


}
