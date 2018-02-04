package jimjam.googlemapsgoogleplaces.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jimmie on 04/02/2018.
 * Portability/Modularity - If time permits add database functionality in this class
 */

public class MarkerModel {

    private ArrayList<LatLng> markerList;

    public MarkerModel(ArrayList<LatLng> markerList) {
        this.markerList = markerList;
    }

    public MarkerModel(){
        this.markerList = null;
    }

    public ArrayList<LatLng> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(ArrayList<LatLng> markerList) {
        this.markerList = markerList;
    }
}
