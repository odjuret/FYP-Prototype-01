package jimjam.dmusmartcampusapp;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jimmie on 08/03/2018.
 */
public class DirectionsParserTest {

    private static final String TAG = "DIRECTIONSPARSERTEST";
    DirectionsParser parser = new DirectionsParser();
    JSONObject jsonObject = null;
    List<List<HashMap<String, String>>> testlist = null;

    @Before
    public void setUp() throws Exception {
        jsonObject = new JSONObject("{\n" +
                "  \"geocoded_waypoints\" : [\n" +
                "    {\n" +
                "      \"geocoder_status\" : \"OK\",\n" +
                "      \"place_id\" : \"ChIJOwg_06VPwokRYv534QaPC8g\",\n" +
                "      \"types\" : [ \"locality\", \"political\" ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"geocoder_status\" : \"OK\",\n" +
                "      \"place_id\" : \"ChIJpVER8hFT5okRmVl96ahKjsw\",\n" +
                "      \"types\" : [ \"locality\", \"political\" ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"geocoder_status\" : \"OK\",\n" +
                "      \"place_id\" : \"ChIJXXN-Q-BE5IkRJ7azSE1832k\",\n" +
                "      \"types\" : [ \"locality\", \"political\" ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"geocoder_status\" : \"OK\",\n" +
                "      \"place_id\" : \"ChIJGzE9DS1l44kRoOhiASS_fHg\",\n" +
                "      \"types\" : [ \"locality\", \"political\" ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"routes\" : [\n" +
                "    {\n" +
                "      \"bounds\" : {\n" +
                "        \"northeast\" : {\n" +
                "          \"lat\" : 42.361087,\n" +
                "          \"lng\" : -71.0206956\n" +
                "        },\n" +
                "        \"southwest\" : {\n" +
                "          \"lat\" : 40.7087115,\n" +
                "          \"lng\" : -74.0065973\n" +
                "        }\n" +
                "      },\n" +
                "      \"copyrights\" : \"Map data ©2015 Google\",\n" +
                "      \"legs\" : [\n" +
                "        {\n" +
                "          \"distance\" : {\n" +
                "            \"text\" : \"117 mi\",\n" +
                "            \"value\" : 188034\n" +
                "          },\n" +
                "          \"duration\" : {\n" +
                "            \"text\" : \"2 hours 12 mins\",\n" +
                "            \"value\" : 7897\n" +
                "          },\n" +
                "          \"end_address\" : \"Hartford, CT, USA\",\n" +
                "          \"end_location\" : {\n" +
                "            \"lat\" : 41.7637157,\n" +
                "            \"lng\" : -72.6852925\n" +
                "          },\n" +
                "          \"start_address\" : \"New York, NY, USA\",\n" +
                "          \"start_location\" : {\n" +
                "            \"lat\" : 40.71265260000001,\n" +
                "            \"lng\" : -74.0065973\n" +
                "          },\n" +
                "          \"steps\" : [\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"240 ft\",\n" +
                "                \"value\" : 73\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"1 min\",\n" +
                "                \"value\" : 9\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 40.7130849,\n" +
                "                \"lng\" : -74.00721879999999\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Head \\u003cb\\u003enorthwest\\u003c/b\\u003e on \\u003cb\\u003eSteve Flanders Square\\u003c/b\\u003e toward \\u003cb\\u003eBroadway\\u003c/b\\u003e/\\u003cb\\u003eCanyon of Heroes\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eRestricted usage road\\u003c/div\\u003e\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"aunwFflubMy@fB[R\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 40.71265260000001,\n" +
                "                \"lng\" : -74.0065973\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"0.1 mi\",\n" +
                "                \"value\" : 222\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"2 mins\",\n" +
                "                \"value\" : 96\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 40.7114005,\n" +
                "                \"lng\" : -74.0086398\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eBroadway\\u003c/b\\u003e\",\n" +
                "              \"maneuver\" : \"turn-left\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"wwnwFbpubMdBtARN|AhAvBjB\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 40.7130849,\n" +
                "                \"lng\" : -74.00721879999999\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"0.2 mi\",\n" +
                "                \"value\" : 246\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"1 min\",\n" +
                "                \"value\" : 54\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 40.7119752,\n" +
                "                \"lng\" : -74.0058488\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003ePark Row\\u003c/b\\u003e\",\n" +
                "              \"maneuver\" : \"turn-left\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"gmnwF~xubMDc@@G?GAGAOOaAm@}Da@cCMq@Ik@\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 40.7114005,\n" +
                "                \"lng\" : -74.0086398\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"0.3 mi\",\n" +
                "                \"value\" : 463\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"2 mins\",\n" +
                "                \"value\" : 112\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 40.7094901,\n" +
                "                \"lng\" : -74.001672\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Slight \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eFrankfort St\\u003c/b\\u003e\",\n" +
                "              \"maneuver\" : \"turn-slight-right\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"{pnwFpgubM@K@CE{@ ...points truncated in this example\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 40.7119752,\n" +
                "                \"lng\" : -74.0058488\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"36 ft\",\n" +
                "                \"value\" : 11\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"1 min\",\n" +
                "                \"value\" : 25\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 40.7095586,\n" +
                "                \"lng\" : -74.0015825\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003ePearl St\\u003c/b\\u003e\",\n" +
                "              \"maneuver\" : \"turn-left\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"ianwFlmtbMMQ\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 40.7094901,\n" +
                "                \"lng\" : -74.001672\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"0.5 mi\",\n" +
                "                \"value\" : 746\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"1 min\",\n" +
                "                \"value\" : 60\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 40.709302,\n" +
                "                \"lng\" : -73.9941819\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Take the \\u003cb\\u003eFDR Drive N\\u003c/b\\u003e ramp\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"wanwFzltbMCS ...points truncated in this example\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 40.7095586,\n" +
                "                \"lng\" : -74.0015825\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            }\n" +
                "\n" +
                "\n" +
                "          ],\n" +
                "          \"via_waypoint\" : []\n" +
                "        },\n" +
                "        {\n" +
                "          \"distance\" : {\n" +
                "            \"text\" : \"87.3 mi\",\n" +
                "            \"value\" : 140570\n" +
                "          },\n" +
                "          \"duration\" : {\n" +
                "            \"text\" : \"1 hour 33 mins\",\n" +
                "            \"value\" : 5603\n" +
                "          },\n" +
                "          \"end_address\" : \"Providence, RI, USA\",\n" +
                "          \"end_location\" : {\n" +
                "            \"lat\" : 41.8238542,\n" +
                "            \"lng\" : -71.412656\n" +
                "          },\n" +
                "          \"start_address\" : \"Hartford, CT, USA\",\n" +
                "          \"start_location\" : {\n" +
                "            \"lat\" : 41.7637157,\n" +
                "            \"lng\" : -72.6852925\n" +
                "          },\n" +
                "          \"steps\" : [\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"164 ft\",\n" +
                "                \"value\" : 50\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"1 min\",\n" +
                "                \"value\" : 11\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 41.7641617,\n" +
                "                \"lng\" : -72.6852741\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Head \\u003cb\\u003enorth\\u003c/b\\u003e on \\u003cb\\u003eHungerford St\\u003c/b\\u003e toward \\u003cb\\u003eMichael J Fallon Way\\u003c/b\\u003e\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"g~{}F`jszLa@?u@C\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 41.7637157,\n" +
                "                \"lng\" : -72.6852925\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"364 ft\",\n" +
                "                \"value\" : 111\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"1 min\",\n" +
                "                \"value\" : 26\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 41.7642071,\n" +
                "                \"lng\" : -72.68661609999999\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eMichael J Fallon Way\\u003c/b\\u003e\",\n" +
                "              \"maneuver\" : \"turn-left\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"_a|}F|iszL?@CrBEvC\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 41.7641617,\n" +
                "                \"lng\" : -72.6852741\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"0.1 mi\",\n" +
                "                \"value\" : 219\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"1 min\",\n" +
                "                \"value\" : 32\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 41.7661707,\n" +
                "                \"lng\" : -72.68643109999999\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eBroad St\\u003c/b\\u003e\",\n" +
                "              \"maneuver\" : \"turn-right\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"ia|}FjrszL_GUg@Ci@Cu@G\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 41.7642071,\n" +
                "                \"lng\" : -72.68661609999999\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            }\n" +
                "\n" +
                "\n" +
                "          ],\n" +
                "          \"via_waypoint\" : []\n" +
                "        },\n" +
                "        {\n" +
                "          \"distance\" : {\n" +
                "            \"text\" : \"50.2 mi\",\n" +
                "            \"value\" : 80771\n" +
                "          },\n" +
                "          \"duration\" : {\n" +
                "            \"text\" : \"59 mins\",\n" +
                "            \"value\" : 3519\n" +
                "          },\n" +
                "          \"end_address\" : \"Boston, MA, USA\",\n" +
                "          \"end_location\" : {\n" +
                "            \"lat\" : 42.359824,\n" +
                "            \"lng\" : -71.05981249999999\n" +
                "          },\n" +
                "          \"start_address\" : \"Providence, RI, USA\",\n" +
                "          \"start_location\" : {\n" +
                "            \"lat\" : 41.8238542,\n" +
                "            \"lng\" : -71.412656\n" +
                "          },\n" +
                "          \"steps\" : [\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"98 ft\",\n" +
                "                \"value\" : 30\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"1 min\",\n" +
                "                \"value\" : 10\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 41.8236618,\n" +
                "                \"lng\" : -71.41291819999999\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Head \\u003cb\\u003esouthwest\\u003c/b\\u003e on \\u003cb\\u003eFulton St\\u003c/b\\u003e toward \\u003cb\\u003eEddy St\\u003c/b\\u003e\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"avg~FbxzrLd@r@\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 41.8238542,\n" +
                "                \"lng\" : -71.412656\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"distance\" : {\n" +
                "                \"text\" : \"249 ft\",\n" +
                "                \"value\" : 76\n" +
                "              },\n" +
                "              \"duration\" : {\n" +
                "                \"text\" : \"1 min\",\n" +
                "                \"value\" : 36\n" +
                "              },\n" +
                "              \"end_location\" : {\n" +
                "                \"lat\" : 41.8231424,\n" +
                "                \"lng\" : -71.4123232\n" +
                "              },\n" +
                "              \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eEddy St\\u003c/b\\u003e\",\n" +
                "              \"maneuver\" : \"turn-left\",\n" +
                "              \"polyline\" : {\n" +
                "                \"points\" : \"{tg~FvyzrLr@y@r@}@\"\n" +
                "              },\n" +
                "              \"start_location\" : {\n" +
                "                \"lat\" : 41.8236618,\n" +
                "                \"lng\" : -71.41291819999999\n" +
                "              },\n" +
                "              \"travel_mode\" : \"DRIVING\"\n" +
                "            }\n" +
                "\n" +
                "\n" +
                "          ],\n" +
                "          \"via_waypoint\" : []\n" +
                "        }\n" +
                "      ],\n" +
                "      \"unwanted\" : {\n" +
                "        \"unwanted\" : \"aunwFflubMd ...points truncated in this example\"\n" +
                "      },\n" +
                "      \"summary\" : \"I-95 N and CT-15 N\",\n" +
                "      \"warnings\" : [],\n" +
                "      \"waypoint_order\" : [ 1, 0 ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\" : \"OK\"\n" +
                "}");
        testlist = parser.parse(jsonObject);
    }

    @Test
    public void jsonfilecheck(){
        assertNotNull(jsonObject);
        assertThat(jsonObject, instanceOf(JSONObject.class));
    }

    @Test
    public void parse() throws Exception {
        assertNotNull(testlist);
    }

    @Test
    public void parserStructureTest() {
        for (List<HashMap<String, String>> path : testlist) {

            for (HashMap<String, String> point : path) {
                double lat = Double.parseDouble(point.get("lat"));
                double lon = Double.parseDouble(point.get("lon"));

                assertTrue(point.containsKey("lat"));
                assertTrue(point.containsKey("lon"));

                assertTrue(lat > 39.00);
                assertTrue(lon < -60.00);
            }
        }

    }



}