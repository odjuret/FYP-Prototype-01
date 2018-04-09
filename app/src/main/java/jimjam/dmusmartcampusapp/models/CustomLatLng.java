package jimjam.dmusmartcampusapp.models;

/**
 * <p>This class acts as a holder for all marker information. The purpose of this is to make methods
 * and workflow in MapActivity easier to read and follow.
 * snippet field is the information showed in the CustomInfoWindow.
 * tourinfo field is the information showed in the guided tour methods custom dialogs in the MapActivity.
 * inTour boolean determines if this marker is to be included in the guided tour methods.</p>
 *
 * <p class="note"><strong>Note:</strong> The title should end with an ID number that represents
 * the order in which they appear during the touring methods.</p>
 *
 * @author Jimmie / p15241925.
 */

public class CustomLatLng {

    private final String title;
    private final String snippet;
    private String url;
    private String tourInfo;
    public final double latitude;
    public final double longitude;
    private Boolean inTour = false;


    /**
     * No constructor without parameters since many instances of this class will be made final/immutable
     * .
     * @param latitude          latitude coordinate of this marker
     * @param longitude         longitude coordinate of this marker
     * @param title             The title of the building / point of interest
     * @param snippet           information intended for the CustomInfoWindow
     * @param tourInfo          information intended for the custom dialog popupo windows for the guided
     *                          tour methods in MapActivity
     */
    public CustomLatLng(double latitude, double longitude, String title, String snippet, String tourInfo) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.snippet = snippet;
        this.tourInfo = tourInfo;
    }


    // Getter and setters
    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getTourInfo() {
        return tourInfo;
    }

    public void setTourInfo(String tourInfo) {
        this.tourInfo = tourInfo;
    }

    public void setInTour(Boolean bool){
        this.inTour = bool;
    }

    public Boolean getInTour() {
        return inTour;
    }

    // Url field is intended to be the url string which will be connected through onclick of the custominfowindow
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
