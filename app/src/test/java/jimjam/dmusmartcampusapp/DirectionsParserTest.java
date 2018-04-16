package jimjam.dmusmartcampusapp;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jimmie on 08/03/2018.
 */
@RunWith(RobolectricTestRunner.class)
public class DirectionsParserTest {

    //variables to be tested
    private DirectionsParser parser;
    private JSONObject jsonObject = new JSONObject();
    private List<List<HashMap<String, String>>> testlist = new ArrayList<>();

    //constructing the JSONObject
    private JSONObject polyline = new JSONObject();
    private JSONObject points = new JSONObject();
    private JSONArray stepsArr = new JSONArray();
    private JSONObject steps = new JSONObject();
    private JSONArray legsArr = new JSONArray();
    private JSONObject legs = new JSONObject();
    private JSONArray routesArr = new JSONArray();


    @Before
    public void setUp() throws Exception {
        parser = new DirectionsParser();

        points.put("points", "wwnwFbpubMdBtARN|AhAvBjB");
        polyline.put("polyline", points);

        stepsArr.put(polyline);
        steps.put("steps", stepsArr);
        steps.put("testfield333", "testvalue333");

        legsArr.put(steps);
        legs.put("legs", legsArr);
        legs.put("testfield222", "testvalue222");

        routesArr.put(legs);

        jsonObject.put("routes", routesArr);
        jsonObject.put("testfield", "testvalue");

        testlist = parser.parse(jsonObject);
    }

    @Test
    public void jsonTypeCheck(){
        assertNotNull(jsonObject);
        assertThat(jsonObject, instanceOf(JSONObject.class));
    }

    @Test
    public void parsedTypeCheck() throws Exception {
        assertNotNull(testlist);
        assertThat(testlist, instanceOf(List.class));
    }

    @Test
    public void parserStructureTest() {

        for (List<HashMap<String, String>> path : testlist) {

            for (HashMap<String, String> point : path) {

                assertThat(point.get("lat"), instanceOf(String.class));
                assertThat(point.get("lon"), instanceOf(String.class));
            }
        }
        //System.err.println(tempList.toString());
    }


    @Test
    public void parserAccuracyTest() {

        for (List<HashMap<String, String>> path : testlist) {

            for (HashMap<String, String> point : path) {

                double lat = Double.parseDouble(point.get("lat"));
                double lon = Double.parseDouble(point.get("lon"));

                assertEquals(lon, -74, 0.1);
                assertEquals(lat, 40.7, 0.1);
            }
        }
    }



}