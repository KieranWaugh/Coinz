package com.kieranwaugh.coinz.coinz;

import android.app.ActivityOptions;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, DownloadCompleteRunner {

    DownloadFileTask DF = new DownloadFileTask();
    private String tag = "MapActivity"; // tag for logcat
    private MapView mapView; // variable to for map
    private MapboxMap map; // map from mapbox
    private LocationEngine locationEngine; // user location
    private Location originLocation; // current user location
    private Location previousLocation; // previous location of the player
    private String mapData; // JSON string of map data from the informatics server
    private double distanceWalked; // distance walked by the player during the life of this activity
    private FloatingActionButton collectButton; // button to collect coins
    private FloatingActionButton shopButton; // button to open shop
    private final String savedMapData = "mapData";
    private String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date()); // date to access the daily map
    private String dateDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()); // date for databsae tree
    private List<Coin> coinsList = new ArrayList<>(); // list of the coins on the map
    private HashMap<String, Marker> markers= new HashMap<>(); // coin id with a associated marker on the map.
    private ArrayList<Marker> collectedMarkers = new ArrayList<>(); // markers that have been removed from the map
    private ArrayList<String> collected = new ArrayList<>(); // ID of collected coins
    private ArrayList<Coin> collectedCoins = new ArrayList<>(); // list of the collected coins
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // firebase firestore initialisation to the root of the database tree
    private String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(); // email address of authenticated user
    private String statsREF; // reference for the stats leaf in the database
    private PlayerStats stats; // statistics object for the player
    private Marker shop; // creates the marker for the shop placed on the map
    private int radius; // current collection radius for coin collection ( default of 25) in metres
    private int multiplyer; // multiplier for coin value (default of 1)
    private double alreadyWalked; // distance already walked by the player in metres
    private List<Feature> features; // feature collection




    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getStats(); // runs function to get the users statistics
            Mapbox.getInstance(this, getString(R.string.access_token)); // assigns the key to this instance of mapbox map
            setContentView(R.layout.activity_map); // sets the activity layout
            BottomNavigationView bottomNavigationView = findViewById(R.id.navigation); // initialises the bottom navigation bar
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(1); // highlights the map icon on the bar
            menuItem.setChecked(true);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out); // transition animation (cross fade)
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> { // listener for icon selected on the bar
                switch (item.getItemId()){
                    case R.id.navigation_player:
                        Intent intent1 = new Intent(MapActivity.this, PlayerActivity.class); // if player is selected
                        intent1.putExtra("stats", stats);
                        startActivity(intent1,options.toBundle()); // start player activity
                        break;

                    case R.id.navigation_map: // do nothing if map is selected

                        break;

                    case R.id.navigation_bank: // if bank is selected
                        Intent intent3 = new Intent(MapActivity.this, BankActivity.class);
                        startActivity(intent3, options.toBundle()); // start bank activity
                        break;
                }
                return false;
            });

            mapView = findViewById(R.id.mapboxMapView); // sets the container for the map
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this); // starts map asynchronous task
            collectButton = findViewById(R.id.floatingActionButton2);
            shopButton = findViewById(R.id.floatingActionButton3);
            collectButton.setVisibility(View.INVISIBLE); // sets coin collection button to invisible
            shopButton.setVisibility(View.INVISIBLE); // sets shop button to invisible

        CollectionReference cr = db.collection("user").document(email).collection("INFO"); // pulls the players radius and multiplier from the database
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        radius = Integer.parseInt(Objects.requireNonNull(document.get("radius")).toString());
                        multiplyer = Integer.parseInt(Objects.requireNonNull(document.get("multi")).toString());
                    }
                }
        });

        SharedPreferences FromFile = getSharedPreferences("mapData", Context.MODE_PRIVATE);
        if (FromFile.contains(date)) {
            Log.d(tag, "[onMapReady] Taking map data from file, moving on");
            mapData = FromFile.getString(date, "");
        }

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.viewSnack), "No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("TRY AGAIN", view -> {
                        Intent i = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(i);
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }

    }


        @Override
        public void onMapReady(MapboxMap mapboxMap) {



            if (mapboxMap == null) { // map must not be null
                Log.d(tag, "[onMapReady] mapBox is null");
            }else{
                map = mapboxMap;
                if (mapData == null || mapData.equals("")) {
                    DF.delegate = this;
                    DF.execute("http://homepages.inf.ed.ac.uk/stg/coinz/" + date + "/coinzmap.geojson"); // starts map download
                    Log.d(tag, "[onMapReady] Taking map data from server");
                }else{
                    addMarkers(mapData);
                }
            }

        }

        private void enableLocation() { // enables the location for the player
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
        private void initializeLocationEngine(){ // initialises the location for the player
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
        private void initializeLocationLayer() { // shows the players location
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

        private void setCameraPosition(Location location) { // moves the map along with the player
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        @Override
        public void onLocationChanged(Location location) { // when the players loatoin has changed
            collectButton.setVisibility(View.INVISIBLE);
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
                    Log.d(tag, "[onLocationChanged] distance walked " + distanceWalked);

                }
                originLocation = location; // sets the players current location

                setCameraPosition(location); // moves the camera


                if (collectOK(shop.getPosition())){ // if the user is close enough to the shop
                    shopButton.setVisibility(View.VISIBLE); // show the shop button
                    shopButton.setOnClickListener(v -> {
                        Intent intent = new Intent(MapActivity.this, ShopActivity.class); // opens the shop
                        intent.putExtra("radius", radius); // send the players current radius
                        intent.putExtra("multi", multiplyer); // send the players current multiplier
                        startActivity(intent);
                    });

                }else{
                    shopButton.setVisibility(View.INVISIBLE); // shop button remains invisible
                }
                for (int i = 0; i < coinsList.size(); i++){ // for each coin on the map
                    Coin coin = coinsList.get(i); // gets the coin
                    Marker m = (markers.get(coin.getId())); // indexing the marker associated with the coin
                    if (!collectedMarkers.contains(m)){
                        assert m != null;
                        LatLng loc = m.getPosition(); // get the markers location
                        if (collectOK(loc)){ // if close enough to collect coin
                            collectButton.setVisibility(View.VISIBLE); // show collection button
                            collectButton.setOnClickListener(v -> {
                                collectedMarkers.add(m); // adds marker to collected list
                                map.removeMarker(m); // removes marker from list

                                db.collection("wallet").document(email).collection("collected ("+dateDB +")")
                                        .add(coin) //adds coin to wallet in database
                                        .addOnSuccessListener(documentReference -> {
                                            Log.d(tag, "coin collected with id " + coin.getId());
                                            collectButton.setVisibility(View.INVISIBLE); // hides collection button
                                            Snackbar.make(findViewById(R.id.viewSnack), "Collected " + m.getTitle() + " of  " + m.getSnippet(),Snackbar.LENGTH_SHORT).show();

                                        })
                                        .addOnFailureListener(e -> Log.w(tag, "Error collecting coin", e));
                            });

                        }

                    }

                }

            }

        }

        public void addMarkers(String data) { // adds markers to map
                System.out.println("[addMarkers] " + data);

                IconFactory iconF = IconFactory.getInstance(this);
                Icon ic = iconF.fromResource(R.drawable.shopping_logo); // create a marker icon of the shop logo
                MarkerOptions mo = new MarkerOptions().position(new LatLng(55.943654, -3.188825)).title("Shop").icon(ic); // sets title
                shop = map.addMarker(mo); // adds the shop to the map

                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                CollectionReference cr = rootRef.collection("wallet").document(email).collection("collected (" + dateDB + ")");
                cr.get().addOnCompleteListener(task -> { // pulling the players wallet from the database
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Coin c = document.toObject(Coin.class);
                            assert c != null;
                            collected.add(c.getId()); // adds the coin id to the collected arraylist
                            collectedCoins.add(c); // adds the coin to the arraylist

                        }
                    }
                    try{
                        features = FeatureCollection.fromJson(data).features(); // creates a feature collection for each marker in the JSON string
                        assert features != null;
                        for (int i = 0; i < features.size(); i++) {
                            try {
                                // extracting the data from the json string for each element of the feature collection.
                                JSONObject jsonObject = new JSONObject(features.get(i).toJson());
                                JSONArray coordinates = jsonObject.getJSONObject("geometry").getJSONArray("coordinates");
                                double lng = Double.parseDouble(coordinates.get(0).toString());
                                double lat = Double.parseDouble(coordinates.get(1).toString());
                                String id = jsonObject.getJSONObject("properties").getString("id");
                                double value = jsonObject.getJSONObject("properties").getDouble("value");
                                String strValue = String.valueOf(value);
                                String currency = jsonObject.getJSONObject("properties").getString("currency");

                                if (!collected.contains(id)) { // only add the marker if it has not been collected already
                                    Context cxt = getApplicationContext();
                                    int iconInt = getIcon(cxt);
                                    IconFactory iconFactory = IconFactory.getInstance(this); //creates the icon for the marker (coin icon)
                                    Icon icon = iconFactory.fromResource(iconInt);
                                    MarkerOptions mk = new MarkerOptions().position(new LatLng(lat, lng)).title(currency).setSnippet("Value: " + strValue).icon(icon); // creates the marker with its location, title and snippet
                                    Marker m = map.addMarker(mk); // adds the marker to the map
                                    Coin coin = new Coin(id, value, currency, lng, lat, false); // creates a coin object with the values from the json string and sets banked to false
                                    coinsList.add(coin); // adds the coi to the list
                                    markers.put(id, m); // adds the marker to the list
                                } else {
                                    continue; // if the coin is already collected, skip and move the next element of the feature collection
                                }
                                Log.d(tag, "[onMapReady] adding marker " + i + " to the map");
                            } catch (JSONException e) {
                                System.out.println(e);
                            }
                        }
                    }catch(Exception e){ // catches download exception
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.viewSnack), "Connection Time Out.", Snackbar.LENGTH_INDEFINITE)
                                .setAction("TRY AGAIN", view -> {
                                    Intent i = new Intent(getApplicationContext(), MapActivity.class);
                                    startActivity(i); // asks user to try download again and restarts activity
                                });
                        snackbar.setActionTextColor(Color.RED);
                        View sbView = snackbar.getView();
                        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.show();
                    }

                });

            enableLocation();
        }


        public boolean collectOK(LatLng coin){ // checks if the user is close enough to a marker
            double UserLat = originLocation.getLatitude(); // current user latitude
            double UserLng = originLocation.getLongitude(); // current user longitude
            double markLat = coin.getLatitude(); // marker latitude
            double marklng = coin.getLongitude(); // marker longitude
            Log.d(tag, "[collectedOK] distance to marker is " + (distance(UserLat, UserLng, markLat, marklng) ));
            return (distance(UserLat, UserLng, markLat, marklng) <= radius); // returns true/false for location


        }
        // conversion of lat/lng to metres
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
            Snackbar.make(findViewById(R.id.viewSnack), "error: " + permissionsToExplain, Snackbar.LENGTH_LONG);
        }

        @Override
        public void onPermissionResult(boolean granted) {
            Log.d(tag, "[onPermissionResult] granted == " + granted);
            if (granted) {
                enableLocation();
            }else{
                Log.d(tag, "permissions not granted");
                //add dialog here
                Snackbar.make(findViewById(R.id.viewSnack), "Permissions must be set", Snackbar.LENGTH_LONG);
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
        protected void onStop(){ // players statistics bust be updated when the map is stopped
            super.onStop();
            mapView.onStop();
            if (collectedCoins.size()!=0){ // for all collected coins
                Log.d(tag, "onStop" + collectedCoins.size());
                alreadyWalked = stats.getDistance();
                Log.d(tag, "[onStop] " + distanceWalked);
                Log.d(tag, "[onStop] " + statsREF);
                int collectedDOLRS = 0;
                int collectedQUIDS = 0;
                int collectedPENYS = 0;
                int collectedSHILS = 0;
                for (int i = 0; i < collectedCoins.size(); i++){ // count how many of each coin has been collected
                    Coin c = collectedCoins.get(i);
                    assert c!=null;
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
                Log.d(tag, "[onStop] +" + statsREF);
                double newDistance = alreadyWalked += distanceWalked;
                Log.d(tag, "[onStop] +" + newDistance);
                FirebaseFirestore inst = FirebaseFirestore.getInstance(); // adds the stats to the database
                inst.collection("user").document(email).collection("STATS").document(statsREF).update("distance", newDistance);
                inst.collection("user").document(email).collection("STATS").document(statsREF).update("dolrs", collectedDOLRS);
                inst.collection("user").document(email).collection("STATS").document(statsREF).update("quids", collectedQUIDS);
                inst.collection("user").document(email).collection("STATS").document(statsREF).update("shils", collectedSHILS);
                inst.collection("user").document(email).collection("STATS").document(statsREF).update("penys", collectedPENYS);


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
                    editor.putString(date,mapData); // saves the map to shared preferences
                    editor.apply();
                }
            }


        }



        @Override
        public void onLowMemory(){
            super.onLowMemory();
            mapView.onLowMemory();
        }

        @Override
        protected void  onDestroy(){ // when the map is destroyed the stats must be added to the databse
            super.onDestroy();
            mapView.onDestroy();
            if (collectedCoins.size()!=0){
                Log.d(tag, "onStop" + collectedCoins.size());
                alreadyWalked = stats.getDistance();
                Log.d(tag, "[onStop] " + distanceWalked);
                Log.d(tag, "[onStop] " + statsREF);
                int collectedDOLRS = 0;
                int collectedQUIDS = 0;
                int collectedPENYS = 0;
                int collectedSHILS = 0;
                for (int i = 0; i < collectedCoins.size(); i++){
                    Coin c = collectedCoins.get(i);
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

        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }

    protected static int getIcon(final Context cxt){ // gets the location of the coin icon
        final int ResourceID = cxt.getResources().getIdentifier("coin", "drawable", cxt.getApplicationContext().getPackageName());
        if (ResourceID == 0){
            throw new IllegalArgumentException();
        }else{
            return ResourceID;
        }
    }

    public void getStats(){ // pulls player stats from database
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference cr = rootRef.collection("user").document(email).collection("STATS");
        cr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    statsREF = (document.getId());
                    stats = document.toObject(PlayerStats.class); // re creates the stats object
                }
                assert stats != null;
                Log.d(tag, "[getStats] " + stats.getDistance());
            }

        });
    }


    @Override
    public void onBackPressed(){ // return to tap to play screen
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.bottom_down, R.anim.nothing);
        startActivity(new Intent(MapActivity.this, MainActivity.class), options.toBundle());
    }

    @Override
    public void processFinish(String output){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        System.out.println(output);
        mapData = output;
        addMarkers(mapData);

    }


}
