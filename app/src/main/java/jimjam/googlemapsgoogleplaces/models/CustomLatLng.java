package jimjam.googlemapsgoogleplaces.models;

/**
 * Created by Jimmie on 04/02/2018.
 */

public class CustomLatLng {

    private final String title;
    private final String snippet;
    private String url;
    private String tourInfo;
    public final double latitude;
    public final double longitude;


    public CustomLatLng(double latitude, double longitude, String title, String snippet, String tourInfo) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.snippet = snippet;
        this.tourInfo = tourInfo;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTourInfo() {
        return tourInfo;
    }

    public void setTourInfo(String tourInfo) {
        this.tourInfo = tourInfo;
    }
}
