package jimjam.dmusmartcampusapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

/**
 * <p>First activity when launching application. checks if googleplay services is "high" enough to
 * make map requests. Then checks for device location permission.
 * mainMap here has only simple touch functionality and is only used for visual effect.</p>
 *
 * @author Jimmie / p15241925
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    //vars
    private static final String TAG = "MainActivity";
    private GoogleMap mainMap;
    public Boolean mLocationPermissionGranted = false;

    //request codes for debugging purposes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //permission vars
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;


    /**
     * Overrides the callback interface for when the map is ready to be used.
     * Used by local method initMap() below.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap){
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: mainMap is ready");
        mainMap = googleMap;
        mainMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.629713, -1.138955), 16f));

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file: https://mapstyle.withgoogle.com/

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        periodicUpdate.run();
    }

    /**
     * Method for calling getMapAsync.
     * "A GoogleMap must be acquired using getMapAsync(OnMapReadyCallback).
     * This class automatically initializes the maps system and the view." - taken from API page on MapFragment.
     */
    private void initMap(){
        Log.d(TAG,"initMap: initializing mainMap");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainMap);
        mapFragment.getMapAsync(MainActivity.this);
    }

    /**
     * Called when the activity is first created.
     * Here we setup the view. then config buttons in view. and "inflate" the fragment as a mapfragment.
     * Then lastly check if application has permission to locate device using network and gps services.
     *
     * @param savedInstanceState contains data from previously frozen states of this activity, if exists.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesOK()){
            init();
            initMap();
            getLocationPermission();
        }
    }

    /**
     * initializes interactive components.
     */
    private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        Button btnTour = (Button) findViewById(R.id.btnTour);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        btnTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("Tour",true);
                startActivity(intent);
            }
        });

    }

    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable () {
        @Override
        public void run() {
            Log.d(TAG, "periodicUpdate: run: called");
            handler.postDelayed(periodicUpdate, 10000 );
            mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.629713, -1.138955), 16f), 1500, null);

        }
    };

    /**
     * checks if the device google play services is high enough to make map requests
     * @return  false if google play services is too low to make map requests
     */
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS){
            //everything is fine and the user device can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            //Toast.makeText(this, "Toast test: Google Play Services is working", Toast.LENGTH_SHORT).show();
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "you cant make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //////////////////////////////////////
    // initial permission request methods.
    public void getLocationPermission(){
        Log.d(TAG,"getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        //loop below to ensure not crashing on inital permission fail
        while (!mLocationPermissionGranted){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
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
                }
            }
        }
    }

    /**
     * retrieval of this activitys google map instance. used for testing
     * @return  this activitys google map instance
     */
    public GoogleMap getMainMap(){
        return mainMap;
    }
}
