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
        this.markerList.add(new CustomLatLng(52.629381,-1.138030, "GH - Gateway House::1","ablbalabllbalablba"));
        this.markerList.add(new CustomLatLng(52.629381,-1.138030, "CC - Campus Centre::2","22222222"));
        this.markerList.add(new CustomLatLng(52.629381,-1.138030, "CH - Clephan Building::3","333333333"));
        this.markerList.add(new CustomLatLng(52.629381,-1.138030, "Q  - Queens Building::4","44444444444"));
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
