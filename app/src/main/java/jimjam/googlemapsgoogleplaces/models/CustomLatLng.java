package jimjam.googlemapsgoogleplaces.models;

/**
 * Created by Jimmie on 04/02/2018.
 */

public class CustomLatLng {

    private final String title;
    private final String snippet;
    public final double latitude;
    public final double longitude;


    public CustomLatLng(double latitude, double longitude, String title, String snippet) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.snippet = snippet;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
