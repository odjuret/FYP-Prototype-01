package jimjam.dmusmartcampusapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Polyline;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import jimjam.dmusmartcampusapp.models.CustomLatLng;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Created by Jimmie on 07/03/2018.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapActivityTest {

    @Rule
    public ActivityTestRule<MapActivity> mapActivityActivityTestRule = new ActivityTestRule<MapActivity>(MapActivity.class);

    //vars
    private MapActivity mapActivity = null;
    private GoogleMap googleMap = null;
    private CustomLatLng testCLL = new CustomLatLng(52.629682,-1.138504,
            "CC - Campus Centre::1","test."+"\n" +"test." +"\n" +"test.",
            "testest te4s test test test tetetetetest");
    private long waitingTime = DateUtils.SECOND_IN_MILLIS * 30;


    //before and after test settings
    @Before
    public void setUp() throws Exception {
        mapActivityActivityTestRule.launchActivity(new Intent());
        mapActivity = mapActivityActivityTestRule.getActivity();

        IdlingPolicies.setMasterPolicyTimeout(60,TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(26,TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        mapActivity = null;
        googleMap = null;
    }


    //tests
    @Test
    public void onMapReady() throws Exception {
        SupportMapFragment supportMapFragment = (SupportMapFragment) mapActivity.getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = mapActivity.getMapMap();

        assertNotNull(supportMapFragment);
        assertNotNull(googleMap);

        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void buttonsCheck() throws Exception {
        View button1 = mapActivity.findViewById(R.id.buttonShowDropDown);
        View button2 = mapActivity.findViewById(R.id.ic_gps);

        assertNotNull(button1);
        assertNotNull(button2);

        onView(withId(R.id.buttonShowDropDown)).check(matches(isDisplayed()));
        onView(withId(R.id.ic_gps)).check(matches(isDisplayed()));
    }

    @Test
    public void dropDownTextTest(){
        onView(withText("Select Campus Building / Place")).check(matches(isDisplayed()));
    }
    @Test
    public void dropdownFuncTest(){
        onView(withId(R.id.buttonShowDropDown)).perform(click());

        onView(withText("CC - Campus Centre")).check(matches(isDisplayed()));
    }

    @Test
    public void checkDB() throws Exception {
        SQLiteDatabase mDB = mapActivity.getmDatabase();

        assertNotNull(mDB);
    }

    @Test
    public void directionsTest() throws InterruptedException {

        //start
        onView(withId(R.id.buttonShowDropDown)).perform(click());
        onView(withText("CC - Campus Centre")).perform(click());

        // make sure espresso does not time out
        IdlingPolicies.setMasterPolicyTimeout(waitingTime*2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(waitingTime*2, TimeUnit.MILLISECONDS);

        //waiting
        IdlingResource idlingResource = new ElapsedTimeIdlingResource(waitingTime);
        IdlingRegistry.getInstance().register(idlingResource);

        Polyline poly = mapActivity.polyline;

        assertNotNull(poly);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

}