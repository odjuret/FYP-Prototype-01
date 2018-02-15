package jimjam.googlemapsgoogleplaces;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jimjam.googlemapsgoogleplaces.models.CustomLatLng;
import jimjam.googlemapsgoogleplaces.models.MarkerModel;

/**
 * Created by Jimmie on 26/01/2018.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int REQUEST_CHECK_SETTINGS = 4567;
    private static final float DEFAULT_ZOOM = 17f;

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    private LocationCallback mLocationCallback;
    private MarkerModel markerModel;
    private ArrayList<CustomLatLng> markerList;
    private CustomLatLng targetMark = null;

    //instance saved variables
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY_SAVED";
    private Boolean mRequestingLocationUpdates = false;

    //widgets
    private ImageView mGps;
    private Polyline polyline = null;
        //dropdown menu
        private Button buttonShowDropDown;
        private PopupWindow popupWindow;
        private String popUpContents[];


    //methods
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        if (mLocationPermissionGranted) {
            createLocationRequest();
            getDeviceLocation();
            //startLocationUpdates();

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);
            uiSettings.setCompassEnabled(true);
            uiSettings.setMyLocationButtonEnabled(false);
            uiSettings.setMapToolbarEnabled(true);
            uiSettings.setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdaper(MapActivity.this));
            init();

        }
    }

    private void placeMarker(CustomLatLng customLatLng){
        mMap.clear();
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(customLatLng.latitude,customLatLng.longitude))
                .title(customLatLng.getTitle())
                .snippet(customLatLng.getSnippet());

        Marker m = mMap.addMarker(options);
        m.setTag(customLatLng);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        //Initialize marker model
        markerModel = new MarkerModel();
        markerList = markerModel.getMarkerList();
        List<String> markerTitleList = markerModel.getMarkerTitles();
        Log.d(TAG, "onCreate: markerListTitles: " +markerTitleList.toString());

        //Initialize drop down menu and button for drop down menu
        popUpContents = new String[markerTitleList.size()];
        markerTitleList.toArray(popUpContents);
        popupWindow = popupWindowMarkers();

        buttonShowDropDown = (Button) findViewById(R.id.buttonShowDropDown);

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "mLocationCallback: onLocationResult: location changed");

            }
        };

        //check permissions for locating this device
        getLocationPermission();
        //check if requesting location updates is paused and if so can simply resume updating
        if (savedInstanceState != null){
            updateValuesFromBundle(savedInstanceState);
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        //clearPolylines();
    }
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        // Update the value of mRequestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...

        // Update UI to match restored state
        // updateUI();
    }
    private void init(){
        Log.d(TAG, "init: initializing");


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
        buttonShowDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.buttonShowDropDown:
                        // show the list view as dropdown
                        Log.d(TAG, "buttonShowDropDown: onClick: trying to display popup");
                        popupWindow.showAsDropDown(v, -5, 0);
                        break;
                }
            }
        });
        periodicUpdate.run();
    }

    /**
     * Call this method to draw a polyline between current location and @param latLng.
     * A polyline being the calculated best travel route by google places API.
     * @param latLng        Destinations Lat Long object
     */
    private void showDirections(final CustomLatLng latLng){
        Log.d(TAG, "showDirections: method called");

        try {
            if (mLocationPermissionGranted) {

                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "showDirections: onComplete: found location!");

                            Location currentLocation = (Location) task.getResult();

                            String url = getRequestUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), latLng);
                            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                            taskRequestDirections.execute(url);
                        } else {
                            Log.d(TAG, "showDirections: onComplete: last known location is null");
                            Toast.makeText(MapActivity.this, "unable to get last know current location", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

//        String url = getRequestUrl(new LatLng(location.getLatitude(), location.getLongitude()), latLng);
//        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//        taskRequestDirections.execute(url);
    }

    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable () {
        @Override
        public void run() {
            // scheduled another events to be in 10 seconds later
            Log.d(TAG, "periodicUpdate: run: called");
            handler.postDelayed(periodicUpdate, 6000 );
            if (targetMark != null && polyline != null) {
                showDirections(targetMark);
            }

        }
    };
    
    private String getRequestUrl(LatLng origin,CustomLatLng dest){
        //value of origin
        String str_org = "origin=" +origin.latitude +"," +origin.longitude;
        //value of destination
        String str_dest = "destination=" + dest.latitude +"," +dest.longitude;
        //set value enable the sensor
        String sensor = "sensor=false";
        //mode for find direction
        String mode = "mode=walking";
        //build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" +mode;
        //Output format
        String output = "json";
        //create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" +output +"?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //get the response result below
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader((inputStream));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            Log.e(TAG, "requestDirection: ", e);
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: get the current devices location");

        try {
            if (mLocationPermissionGranted) {

                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location!");

                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        } else {
                            Log.d(TAG, "onComplete: last known location is null");
                            Toast.makeText(MapActivity.this, "unable to get last know current location", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest: creating location request");

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "createLocationRequest: location request settings OK!");

                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                mRequestingLocationUpdates = true;
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    Log.d(TAG,"createLocationRequest: location request settings not satisfied!");
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: retrieving location updates");
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

        }
    }
    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: stop retrieving location updates");
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "stopLocationUpdates: onComplete called");
            }
        });
    }

    private void initMap(){
        Log.d(TAG,"initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + "   lng: " + latLng.longitude );
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 1500, null);
    }

    //////////////////////////////////////
    // initial permission request methods.
    private void getLocationPermission(){
        Log.d(TAG,"getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION};

        //loop below to ensure not crashing on inital permission fail
        while (!mLocationPermissionGranted){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                    initMap();
                }else {
                    ActivityCompat.requestPermissions(this,
                            permissions,
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            }else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionResults: called.");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i =0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG,"onRequestPermissionResults: permission FAILED.");
                            return;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionResults: permission GRANTED");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void clearPolylines(){
        if (polyline != null) {
            polyline.remove();
        }

    }

    /* ----------------------------- inner classes below -----------------------------------------  */

    public class TaskRequestDirections extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "TaskRequestDirections: doInBackground: called");
            String responseString = "";

            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                Log.d(TAG, "TaskParser: doInBackground: " + e.getMessage());

            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(20);
                polylineOptions.color(Color.CYAN);
                polylineOptions.geodesic(true);
                List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(20));
                polylineOptions.pattern(pattern);
                polylineOptions.endCap(new RoundCap());
            }

            if (polylineOptions!=null) {
                clearPolylines();
                polyline = mMap.addPolyline(polylineOptions);

            } else {
                Log.d(TAG, "TaskParser: onPostExecute: directions not found");
            }
        }
    }

    /*---------------------- custom dropdown menu classes ---------------------------*/
    public PopupWindow popupWindowMarkers() {
        Log.d(TAG, "popupWindowMarkers: creating popupwindow");
        // initialize a pop up window type
        PopupWindow popupWindowMarkers = new PopupWindow(this);

        // the drop down list is a list view
        ListView listViewMarkers = new ListView(this);

        // set our adapter and pass our pop up window contents
        listViewMarkers.setAdapter(listViewAdapter(popUpContents));

        // set the item click listener
        listViewMarkers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // get the context and main activity to access variables
                Context mContext = MapActivity.this;

                // add some animation when a list item was clicked
                Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
                fadeInAnimation.setDuration(10);
                v.startAnimation(fadeInAnimation);

                // dismiss the pop up
                popupWindow.dismiss();

                // get the text and set it as the button text
                String selectedItemText = ((TextView) v).getText().toString();
                buttonShowDropDown.setText(selectedItemText);

                // get the id
                String selectedItemTag = ((TextView) v).getTag().toString();
                Toast.makeText(mContext, "ID is: " + selectedItemTag, Toast.LENGTH_SHORT).show();

                // call the show directions method using the id
                for (CustomLatLng item : markerList) {
                    if (item.getTitle().contains(selectedItemTag)) {
                        showDirections(item);
                        targetMark = item;
                        placeMarker(item);
                        moveCamera(new LatLng(item.latitude, item.longitude), DEFAULT_ZOOM);
                    }
                }
            }

        });

        // some other visual settings
        popupWindowMarkers.setFocusable(true);
        popupWindowMarkers.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMarkers.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMarkers.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_border));

        // set the list view as pop up window content
        popupWindowMarkers.setContentView(listViewMarkers);

        return popupWindowMarkers;
    }


    /*
     * adapter where the list values will be set
     */
    private ArrayAdapter<String> listViewAdapter(String markerArray[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, markerArray) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // setting the ID and text for every items in the list
                String item = getItem(position);
                String[] itemArr = item.split("::");
                String text = itemArr[0];
                String id = itemArr[1];

                // visual settings for the list item
                TextView listItem = new TextView(MapActivity.this);

                listItem.setText(text);
                listItem.setTag(id);
                listItem.setTextSize(22);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setTextColor(Color.BLACK);
                listItem.setMaxLines(1);

                return listItem;
            }
        };

        return adapter;
    }
}
