package jimjam.dmusmartcampusapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

import org.json.JSONArray;
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

import jimjam.dmusmartcampusapp.models.CustomLatLng;
import jimjam.dmusmartcampusapp.models.MarkerModel;
import jimjam.dmusmartcampusapp.models.MySqliteOpenHelper;

/**
 * <p><strong>The Main interactive activity</strong>
 * The controller of the appplication as a whole.
 * Here the SQLite database is created and filled with info if it
 * does not already exists using {@Link #initDB}.
 * The application will, after init methods are called, check if it has
 * permissions to locate device.
 * If {@Link #targetMark} is set then a polyline will be drawn on the map between
 * the device current location and the target mark.
 * Polylines are drawn using {@Link #showDirections} which in turn uses many methods and inner
 * classes, one of these {@Link #requestDirections} connects to a web api using http connection,
 * this is why this application needs internet permission.
 * Redraws the polyline every 6 seconds using the runnable {@Link #periodicUpdate}
 * which recursively calls itself after a set delay. </p>
 *
 * @author Jimmie / p15241925
 *
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    //debugging related vars
    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //permission related vars
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_CHECK_SETTINGS = 4567;

    //main map and location vars
    private static final float DEFAULT_ZOOM = 17f;
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    private LocationCallback mLocationCallback;
    private ArrayList<CustomLatLng> markerList;

    //the current "target" to guide the user towards and draw polylines to.
    private CustomLatLng targetMark = null;

    //instance saved variables
    /* if mRequestingLocationUpdates is true then this wont check for locate device permissions
     */
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY_SAVED";
    private Boolean mRequestingLocationUpdates = false;

    //widgets
    private ImageView mGps;
    public Polyline polyline = null;
        //dropdown menu
        private Button buttonShowDropDown;
        private PopupWindow popupWindow;
        private String popUpContents[];

    //guided tour related
    private Boolean touring = false; //true if user clicks guided tour in MainActivity
    private Integer counter = 0;
    private Button nextButton;
    private ArrayList<CustomLatLng> markerTourList = new ArrayList<CustomLatLng>(); //null unless touring is true

    //SQLite related variables below
    private MySqliteOpenHelper mySqliteOpenHelper;
    private SQLiteDatabase mDatabase;
    private SQLiteDatabase mWriteAbleDB;

    private ProgressBar progressBar;

    //methods

    /**
     * After initMap has successfully acquired and synced the map the onMapReady callback is called.
     * A custom map generated on https://mapstyle.withgoogle.com/ is loaded.
     * Generic GoogleMap buttons and settings setup, some are adjusted depending if {@Link #touring}
     * variable is true or false.
     *
     * @param googleMap         the ready map in use in fragment.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
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
            uiSettings.setCompassEnabled(true);
            uiSettings.setMyLocationButtonEnabled(false);
            uiSettings.setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdaper(MapActivity.this));
            init();

            if (touring) {
                buttonShowDropDown.setVisibility(View.GONE);
                for (CustomLatLng marker: markerList) {
                    if (marker.getInTour()) {
                        markerTourList.add(marker);
                    }
                }
                doTheTour();
                uiSettings.setMapToolbarEnabled(false);
            } else {
                nextButton.setVisibility(View.GONE);
                uiSettings.setMapToolbarEnabled(true);
                uiSettings.setAllGesturesEnabled(true);

            }
        }
    }

    /*------------------------- common activity related methods -----------------------------------*/

    /**
     * common onCreate. defines what will happen upon creation of this activity.
     *
     * @param savedInstanceState    saved instance state if there is one.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        //Initialize database
        initDB();
        //database fills markerList
        ArrayList<String> markerTitleList = new ArrayList<String>();
        for (CustomLatLng item: markerList) {
            markerTitleList.add(item.getTitle());
        }
        Log.d(TAG, "onCreate: markerListTitles: " +markerTitleList.toString());


        //Initialize drop down menu and button for drop down menu
        popUpContents = new String[markerTitleList.size()];
        markerTitleList.toArray(popUpContents);
        popupWindow = popupWindowMarkers();

        buttonShowDropDown = (Button) findViewById(R.id.buttonShowDropDown);
        nextButton = (Button) findViewById(R.id.nextBtn);

        progressBar = (ProgressBar) findViewById(R.id.determinateBar);
        progressBar.setMax(10);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);


        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //why the hell is this callback function not called???

                Log.i(TAG, "mLocationCallback: onLocationResult: location changed");
                Log.d(TAG, "onLocationResult: onLocationResult: location changed");
            }
        };

        //check permissions for locating this device
        getLocationPermission();
        //check if requesting location updates is paused and if so can simply resume updating
        if (savedInstanceState != null){
            updateValuesFromBundle(savedInstanceState);
        }

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            touring = (Boolean) bd.get("Tour");
        }
        Log.d(TAG, "onCreate: touring is " +touring.toString());
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        // Update the value of mRequestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    /**
     * This application will handle orientation and screensize changes itself by calling
     * this callback, see: AndroidManifest.xml ->  android:configChanges="orientation|screenSize"
     * This is considered somewhat of a bad practice according to developer.android.com, see
     * https://developer.android.com/guide/topics/resources/runtime-changes.html
     *
     * @param newConfig Configuration class containign data on the new configuration chjanges.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //do something when the config mentioned in the manifest changes.
    }

    /* ---------------------------- initialization methods ---------------------------------------*/
    /**
     * Method for calling getMapAsync.
     * "A GoogleMap must be acquired using getMapAsync(OnMapReadyCallback).
     * This class automatically initializes the maps system and the view." - taken from API page on MapFragment.
     */
    private void initMap(){
        Log.d(TAG,"initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
        //mapFragment.setRetainInstance(true);
    }

    /**
     * Initializes interactive components. And starts the periodic update runnable.
     */
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
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.nextBtn:
                        // show the list view as dropdown
                        Log.d(TAG, "nextButton: onClick: next item on tour");
                        if (counter< (markerTourList.size()-1)) {
                            counter++;
                            onTheTour();
                        } else {
                            endTour();
                        }
                        break;
                }
            }
        });

        periodicUpdate.run();
    }

    /**
     * Initializes the database. If database is empty it will populate it.
     * Fills the markerList with data from database as CustomLatLng objects
     */
    private void initDB(){
        //database init

        mySqliteOpenHelper = new MySqliteOpenHelper(getApplicationContext());
        markerList = new ArrayList<>();
        //populateDB();

        mDatabase = mySqliteOpenHelper.getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery("select * from markers ;", null);
        Log.d(TAG, "initDB: number of columns in DB: " + cursor.getColumnCount());
        Log.d(TAG, "initDB: data in DB: " +cursor.getCount());
        if (cursor.getCount() < 4) {
            populateDB();
        }
        if (cursor.getCount() > 7) {
            mWriteAbleDB = mySqliteOpenHelper.getWritableDatabase();
            mWriteAbleDB.execSQL("delete from markers");
        }

        //read all the markers from the DB
        while (cursor.moveToNext()) {
            double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
            double lon = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String snippet = cursor.getString(cursor.getColumnIndexOrThrow("snippet"));
            String tourinfo = cursor.getString(cursor.getColumnIndexOrThrow("tourinfo"));
            CustomLatLng cll = new CustomLatLng(lat,lon,title, snippet, tourinfo);
            if (!cll.getTitle().contains("5")) {
                cll.setInTour(true);
            }

            markerList.add(cll);
        }
        cursor.close();

    }

    /* ----------------------- polyline/directions related methods & classes ----------------------*/
    /**
     * Call this method to draw a polyline between current location and @param latLng.
     * A polyline being the calculated best travel route by google places API.
     * @param latLng        Destinations Lat Long object
     */
    public void showDirections(final CustomLatLng latLng){
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
                            Toast.makeText(MapActivity.this,
                                    "unable to get last known current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    /**
     * A runnable which recursively calls itself after a delay.
     * acts as a update method for the directions polyline.
     * DO NOTE: that this way of doing this has only been briefly tested with arbitrary results.
     */
    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable () {
        @Override
        public void run() {
            Log.d(TAG, "periodicUpdate: run: called");
            handler.postDelayed(periodicUpdate, 6000 );
            if (targetMark != null && polyline != null) {
                showDirections(targetMark);
            }
        }
    };

    /**
     * This method acts as a factory to construct a correct string representing the url
     * needed for connecting to the google maps web api that retrieves the directions (polyline).
     * Used by {@Link #showDirections}.
     *
     * @param origin    original user location.
     * @param dest      destination user location.
     * @return          the finished string to use as url for http connecting to the web api.
     */
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

    /**
     * Queries the google maps web api.
     *
     * @param reqUrl            url string created by {@Link #getRequestUrl}
     * @return                  the returned response represented as a string, either a JSON or XML object.
     *                          needs to be parsed to make sense of.
     * @throws IOException      error message if http connection fails.
     */
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

    /**
     * Inner class used by showDirections method(). Queries the googlemaps web api
     * using the local requestDirection class.
     * Extends AsyncTask so the requests and polyline drawing on the map can be done without
     * pausing the application / interrupting the user experience.
     */
    public class TaskRequestDirections extends AsyncTask<String, Integer, String>{

        /**
         * Override this method to perform a computation on a background thread.
         * The specified parameters are the parameters passed to execute by the caller of this task.
         * This method can call publishProgress to publish updates on the UI thread.
         *
         * @param strings   the url string generated by {@Link #getRequestUrl}
         * @return          the returned response represented as a string, either a JSON or XML object.
         *                  needs to be parsed to make sense of.
         */
        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "TaskRequestDirections: doInBackground: called");
            String responseString = "";

            try {
                responseString = requestDirection(strings[0]);
                //publishProgress(someprogress);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            super.onProgressUpdate(progress);
//            progressBar.setProgress(progress[0]);
//
//        }

        /**
         * Runs on the UI thread after doInBackground.
         * The specified result is the value returned by doInBackground.
         *
         * @param s     the result returned by doInBackground
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
            //progressBar.setVisibility(View.GONE);

        }
    }

    /**
     * This inner class opens up a thread to handle the {@Link #DirectionsParser} class.
     * Parse the JSON objects arrays and nested arrays into lists and hashmaps.
     *
     * Extends AsyncTask so the requests and polyline drawing on the map can be done without
     * pausing the application / interrupting the user experience.
     */
    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        /**
         * Overriden method to perform a computation on a background thread.
         * The specified parameters are the parameters passed to execute by the caller of this task.
         * This method can call publishProgress to publish updates on the UI thread.
         *
         * @param strings   the string generated by {@Link #TaskRequestDirections}.
         * @return          the returned response represents the arrays and nested arrays from the
         *                  JSON object as lists and hashmaps.
         */
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

        /**
         * Runs on the UI thread after doInBackground.
         * The specified result is the value returned by doInBackground.
         *
         * @param lists     returned response from doInBackground which represents the arrays and
         *                  nested arrays from the JSON object as lists and hashmaps.
         */
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

                    //Log.d(TAG, "TaskParser: onPostExecute: lat:" + lat +",, lon: " +lon);
                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(25);
                polylineOptions.color(Color.CYAN);
                polylineOptions.geodesic(true);
                List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(20));
                polylineOptions.pattern(pattern);
                polylineOptions.endCap(new RoundCap());
                polylineOptions.startCap(new RoundCap());
            }

            if (polylineOptions!=null) {
                clearPolylines();
                polyline = mMap.addPolyline(polylineOptions);

            } else {
                Log.d(TAG, "TaskParser: onPostExecute: directions not found");
                if (targetMark!=null) {
                    showDirections(targetMark);
                }
            }
        }
    }

    /*------------------------------- Location related methods below ------------------------------*/

    /**
     *  Retrieves the current locations langitude and longitude coordinates.
     *  DO NOTE: that this could easily be modified to return a LatLng object.
     */
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

    /**
     * A method to prepare retrieval of coordinates for this device periodically.
     * Requests settings have to be prepared and checked for validity before use.
     * If settings return valid then {@Link #startLocationUpdates} should be called.
     */
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

    /**
     * starts the periodical retrieval of new locations of this device
     */
    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: retrieving location updates");
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

        }
    }

    /**
     * stops the periodical retrieval of new locations of device
     */
    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: stop retrieving location updates");
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "stopLocationUpdates: onComplete called");
            }
        });
    }

    ////////////////////////////////////////////
    // initial permission request methods below.
    /**
     * Checks if this application has permission to retrieve the location of this device.
     * If not then request permissions from the user using requestPermissions.
     */
    public void getLocationPermission(){
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

    /**
     * callback method from {@Link #getLocationPermission} failed checks.
     *
     * @param requestCode       as passed in getLocationPermission, should be: LOCATION_PERMISSION_REQUEST_CODE.
     * @param permissions       the permissions requested.
     * @param grantResults      The grant results for the corresponding permissions which is either
     *                          android.content.pm.PackageManager.PERMISSION_GRANTED or
     *                          android.content.pm.PackageManager.PERMISSION_DENIED. Never null.
     */
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

    /*------------------------------ map visuals related methods ---------------------------------- */

    /**
     * A custom addMarker method using the custom marker picture.
     *
     * @param customLatLng  the marker to be placed upon the map.
     */
    private void placeMarker(CustomLatLng customLatLng){
        mMap.clear();
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(customLatLng.latitude,customLatLng.longitude))
                .title(customLatLng.getTitle())
                .snippet(customLatLng.getSnippet())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_custom));

        Marker m = mMap.addMarker(options);
        m.setTag(customLatLng);
    }

    /**
     * clear the map of polylines.
     */
    private void clearPolylines(){
        if (polyline != null) {
            polyline.remove();
        }
    }

    /**
     * Method to move the camera.
     *
     * @param latLng    latitude and longitude coordinates to center camera on
     * @param zoom      the level of zoom to center the map upon.
     */
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + "   lng: " + latLng.longitude );
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 1500, null);
    }


    /*---------------------- custom dropdown menu classes ---------------------------*/

    /**
     * Custom popupwindow method. fills it with info from {@Link #markerList} variable.
     * Uses the id number in title of the CustomLatLng, "Building::1" id=1 for example,
     * to identify the target. This is a result from using hardcoded data early on and should
     * be avoided.
     *
     * @return popupWindowMarkers      the finished custom popupwindow ready to use.
     */
    public PopupWindow popupWindowMarkers() {
        Log.d(TAG, "popupWindowMarkers: creating popupwindow");

        PopupWindow popupWindowMarkers = new PopupWindow(this);
        ListView listViewMarkers = new ListView(this);
        listViewMarkers.setAdapter(listViewAdapter(popUpContents));

        listViewMarkers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Context mContext = MapActivity.this;

                // add some animation when a list item was clicked
                Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
                fadeInAnimation.setDuration(10000);
                v.startAnimation(fadeInAnimation);

                popupWindow.dismiss();

                // get the text and set it as the button text
                String selectedItemText = ((TextView) v).getText().toString();
                buttonShowDropDown.setText(selectedItemText);

                // get the id
                String selectedItemTag = ((TextView) v).getTag().toString();
                //Toast.makeText(mContext, "ID is: " + selectedItemTag, Toast.LENGTH_SHORT).show();

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


    /**
     *  Adapter for the custom popupwindow where the list values will be set. Used by {@Link #popupWindowMarkers}
     *
     * @param markerArray       The items to populate the listview with
     * @return                  returns the filled adapter ready to be used for views.
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




    /*----------------------------- guided tour methods below ------------------------------------*/

    /**
     * Inital guided tour method. should only be called once per tour.
     * ordering of tour methods doTheTour() -> onTheTour() -> makeAdialog() -> endTour().
     */
    private void doTheTour(){
        Log.d(TAG, "doTheTour: tour started");

        final AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setMessage("Welcome to De Montfort University! \n" +
                "To make your transition to university life here at DMU we would like to " +
                "take you on a tour around campus to show the main places of interest. " +
                "\nWhen you find the place just click the button at the bottom of the screen " +
                "to continue on with the tour" );
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "doTheTour: onClick: clicked");
                dialog.dismiss();
                onTheTour();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog.show();
    }

    /**
     * Main tour method. Looks redundant. howver, the author had portability plans which would involve
     * database handler methods to be called in here and then passed into makeAdialog or other methods.
     */
    public void onTheTour(){
        Log.d(TAG, "onTheTour: called");
        makeAdialog(markerTourList.get(counter));
        targetMark = markerTourList.get(counter);
        }

    /**
     * Creates a custom popupwindow and fills it with information taken from the custom latlng paramater.
     * uses the following methods: showDirections, moveCamera, placeMarker.
     * @param customLatLng the current targetmarker to present information about through this guided
     *                     tour popup dialog creater method.
     */
    public void makeAdialog(CustomLatLng customLatLng){
        Log.d(TAG, "makeAdialog: called.");

        //alertdialog and custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.custom_dialog, null);

        //pick image title depending on title in the customLatLng para
        String imageString = customLatLng.getTitle().substring(0,2);
        if (imageString.contains(" ")) {
            imageString = imageString.substring(0, 1);
        }
        Context mContext = this.getApplicationContext();
        int imageId = mContext.getResources().getIdentifier(imageString.toLowerCase(),
                "drawable", mContext.getPackageName());
        ImageView icon = (ImageView) dialoglayout.findViewById(R.id.imageDialog);
        icon.setImageResource(imageId);

        //fill the textview with customlatlng tourinfo
        TextView textView = (TextView) dialoglayout.findViewById(R.id.textDialog);
        textView.setText(customLatLng.getTourInfo());
        textView.setMovementMethod(new ScrollingMovementMethod());

        //more dialog settings, buttons and show it.
        builder.setView(dialoglayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "doTheTour: onClick: clicked");
                dialog.dismiss();
                showDirections(targetMark);
                moveCamera(new LatLng(targetMark.latitude,targetMark.longitude), DEFAULT_ZOOM);
                placeMarker(targetMark);
            }
        });
        if (counter > 0) {
            builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    counter--;
                    onTheTour();
                }
            });
        }
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog.show();

    }

    /**
     * final touring method whith a final popup dialog whcih exits the activity
     */
    private void endTour(){
        Log.d(TAG, "endTour: called");

        final AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setMessage("Thank you for taking the tour! \nWe hope you will enjoy your stay at \nDe Montfort University");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "endTour: onClick: clicked");
                dialog.dismiss();
                targetMark = null;
                clearPolylines();
                mMap.clear();
                finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog.show();
    }


    //----------------------------------------- redundant code below -------------------------------------------------

    /**
     * since the author did not have access to a remote server from which he could run a SQL server on this
     * method was used to populate the local SQLite database.
     * should only be called once.
     */
    private void populateDB(){
        Log.d(TAG, "populateDB: called");
        mySqliteOpenHelper.addRow("52.629682", "-1.138504", "CC - Campus Centre::1",
                "DMU Students' Union."+"\n" +"DMU Supplies Shop." +"\n" +"Food and Drinks.",
                "A great place to start the tour is at the Campus Centre building. " +
                        "The home of De Montfort Students' Union (DSU), Campus Centre is the hub for student life. " +
                        "On the ground floor, visitors can eat or drink at a café selling Starbucks coffee, at Subway or at Milly Lane’s");

        mySqliteOpenHelper.addRow("52.629381","-1.138030", "GH - Gateway House::2",
                "The Student Gateway." +"\n" +"Information and Help." +"\n"
                        +"Finance Office." +"\n" +"Faculty of Technology Home." +"\n" +"Computer Labs." +"\n",
                "Next stop is the Gateway House where the student gateway is located. This is a central hub of activity for Technology students. " +
                        "it is home to the Student Gateway, the Graduate School Office and the Faculty of Technology Advice Centre. " +
                        "The Student Gateway offers information, advice and guidance on a wide range of topics such as finance and welfare, " +
                        "jobs and careers, disability issues, counselling, mental health and wellbeing.");

        mySqliteOpenHelper.addRow("52.629777","-1.139679",
                "VP - Vijay Patel Building::3","Art & Design Courses." +"\n" +"DMU Food Village." +"\n"
                        +"Riverside Cafe.",
                "DMU's new Vijay Patel Building, home to art and design courses, " +
                        "is the centrepiece of the £136 million Campus Transformation Project which will, " +
                        "providing DMU with one of the finest campuses in the country.");

        mySqliteOpenHelper.addRow("52.630757","-1.137346",
                "CH - Clephan Building::4","Humanities Courses." +"\n" +"Cultural Exchanges Festival."
                        +"\n" +"Sports History and Culture.",
                "Home to the humanities subjects at DMU, Clephan has the latest audio-visual equipment and cinema screens. " +
                        "Clephan hosts the annual DMU Cultural Exchanges Festival and houses the Leicester Centre for Creative Writing, " +
                        "the Centre for Textual Studies, the Centre for Adaptations and the International Centre for Sports History and Culture.");

        mySqliteOpenHelper.addRow("52.629220","-1.139771",
                "Q  - Queens Building::5","Technology." +"\n" +"School of Engineering." +"\n"
                        +"Centre of Sustainable Energy." +"\n" +"Leicester Media School.",
                "If you can read this message then the author of this app has 'fucked shit up'");

        mySqliteOpenHelper.addRow("52.629015", "-1.139162",
                "KL - Kimberlin Library::6", "Main library of DMU \n" + "+1500 study places \n"
                        +"650+ computer workstations \n" +"Open 24/7",
                "Kimberlin Library is at the heart of the student learning experience, both as a physical resource and a " +
                        "virtual online service that is accessible anywhere online. During term time it is open 24 hours a day, " +
                        "seven days a week, giving you access to more than half a million publications and a wide range of DVDs, " +
                        "as well as e-resources and thousands of electronic journals. We have 1,500 study places and 650 computer " +
                        "workstations across four sites on campus.");
    }

    /* --------- Junit tests below ---------*/

    public GoogleMap getMapMap(){
        return mMap;
    }
    public SQLiteDatabase getmDatabase(){
        return mDatabase;
    }
}
