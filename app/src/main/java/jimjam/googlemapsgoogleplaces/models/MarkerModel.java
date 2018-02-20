package jimjam.googlemapsgoogleplaces.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jimmie on 04/02/2018.
 * Portability/Modularity - If time permits add database functionality in this class
 */

public class MarkerModel {

    private ArrayList<CustomLatLng> markerList;

    public MarkerModel(ArrayList<CustomLatLng> markerList) {
        this.markerList = markerList;
    }

    public MarkerModel(){
        markerList = new ArrayList<>();
        this.markerList.add(new CustomLatLng(52.629381,-1.138030,
                "GH - Gateway House::1","The Student Gateway." +"\n" +"Information and Help." +"\n"
                +"Finance Office." +"\n" +"Faculty of Technology Home." +"\n" +"Computer Labs." +"\n",
                "test test test test test tesst test test test teste stest test test"));

        this.markerList.add(new CustomLatLng(52.629682,-1.138504,
                "CC - Campus Centre::2","DMU Students' Union."+"\n" +"DMU Supplies Shop." +"\n" +"Food and Drinks.",
                "test test test test test tesst test test test teste stest test test"));

        this.markerList.add(new CustomLatLng(52.629777,-1.139679,
                "VP - Vijay Patel Building::3","Art & Design Courses." +"\n" +"DMU Food Village." +"\n"
                +"Riverside Cafe.",
                "test test test test test tesst test test test teste stest test test"));

        this.markerList.add(new CustomLatLng(52.630757,-1.137346,
                "CH - Clephan Building::4","Humanities Courses." +"\n" +"Cultural Exchanges Festival."
                +"\n" +"Sports History and Culture.",
                "test test test test test tesst test test test teste stest test test"));

        this.markerList.add(new CustomLatLng(52.629220,-1.139771,
                "Q  - Queens Building::5","Technology." +"\n" +"School of Engineering." +"\n"
                +"Centre of Sustainable Energy." +"\n" +"Leicester Media School.",
                "test test test test test tesst test test test teste stest test test"));

    }

    public ArrayList<CustomLatLng> getMarkerList() {
        return markerList;
    }

    public ArrayList<String> getMarkerTitles() {
        ArrayList<String> tempList = new ArrayList<String>();
        for (CustomLatLng item: this.markerList) {
            tempList.add(item.getTitle());
        }

        return tempList;
    }

    public void setMarkerList(ArrayList<CustomLatLng> markerList) {
        this.markerList = markerList;
    }
}
