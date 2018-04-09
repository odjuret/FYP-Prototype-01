package jimjam.dmusmartcampusapp;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jimmie on 07/03/2018.
 */
public class MainActivityTest {

    //what is being tested
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    //variables
    private MainActivity mainActivity = null;
    private GoogleMap testMap = null;

    //before and after tests settings
    @Before
    public void setUp() throws Exception {
        mainActivity = mainActivityActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
        testMap = null;
    }



    //tests
    @Test
    public void onMapReady() throws Exception {
        SupportMapFragment supportMapFragment = (SupportMapFragment) mainActivity.getSupportFragmentManager().findFragmentById(R.id.mainMap);
        testMap = mainActivity.getMainMap();

        assertNotNull(supportMapFragment);
        assertNotNull(testMap);
    }

    @Test
    public void onCreate() throws Exception {
        View button1 = mainActivity.findViewById(R.id.btnMap);
        View button2 = mainActivity.findViewById(R.id.btnTour);

        assertNotNull(button1);
        assertNotNull(button2);
    }

    @Test
    public void isServicesOK() throws Exception {
        Boolean isSOK = mainActivity.isServicesOK();

        assertNotNull(isSOK);
    }

    @Test
    public void getLocationPermission() throws Exception {
        Boolean LocPerm = mainActivity.mLocationPermissionGranted;

        assertTrue(LocPerm);
    }

}