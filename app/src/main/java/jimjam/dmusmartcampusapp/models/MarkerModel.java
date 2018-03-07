package jimjam.dmusmartcampusapp.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import jimjam.dmusmartcampusapp.MapActivity;

/**
 * Created by Jimmie on 04/02/2018.
 */

public class MarkerModel {

    private ArrayList<CustomLatLng> markerList;

    public MarkerModel(ArrayList<CustomLatLng> markerList) {
        this.markerList = markerList;
    }

    public MarkerModel(){
        markerList = new ArrayList<>();


        CustomLatLng CC = new CustomLatLng(52.629682,-1.138504,
                "CC - Campus Centre::1","DMU Students' Union."+"\n" +"DMU Supplies Shop." +"\n" +"Food and Drinks.",
                "A great place to start the tour is at the Campus Centre building. " +
                        "The home of De Montfort Students' Union (DSU), Campus Centre is the hub for student life. " +
                        "On the ground floor, visitors can eat or drink at a café selling Starbucks coffee, at Subway or at Milly Lane’s, " +
                        "the campus’ newest eatery run by DSU, selling freshly handmade hot and cold food, coffee and smoothies.");
        CC.setInTour(true);
        this.markerList.add(CC);

        CustomLatLng GH = new CustomLatLng(52.629381,-1.138030,
                "GH - Gateway House::2","The Student Gateway." +"\n" +"Information and Help." +"\n"
                +"Finance Office." +"\n" +"Faculty of Technology Home." +"\n" +"Computer Labs." +"\n",
                "Next stop is the Gateway House where the student gateway is located. This is a central hub of activity for Technology students. " +
                        "As well as housing a variety of classrooms and studios primarily for our computing students, " +
                        "it is home to the Student Gateway, the Graduate School Office and the Faculty of Technology Advice Centre. " +
                        "The Student Gateway offers information, advice and guidance on a wide range of topics such as finance and welfare, " +
                        "jobs and careers, disability issues, counselling, mental health and wellbeing.");
        GH.setInTour(true);
        this.markerList.add(GH);

        CustomLatLng VP = new CustomLatLng(52.629777,-1.139679,
                "VP - Vijay Patel Building::3","Art & Design Courses." +"\n" +"DMU Food Village." +"\n"
                +"Riverside Cafe.",
                "DMU's new Vijay Patel Building, home to art and design courses, " +
                        "is the centrepiece of the £136 million Campus Transformation Project which will, " +
                        "providing DMU with one of the finest campuses in the country.");
        VP.setInTour(true);
        this.markerList.add(VP);

        CustomLatLng CH = new CustomLatLng(52.630757,-1.137346,
                "CH - Clephan Building::4","Humanities Courses." +"\n" +"Cultural Exchanges Festival."
                +"\n" +"Sports History and Culture.",
                "Home to the humanities subjects at DMU, Clephan has the latest audio-visual equipment and cinema screens. " +
                        "Clephan hosts the annual DMU Cultural Exchanges Festival and houses the Leicester Centre for Creative Writing, " +
                        "the Centre for Textual Studies, the Centre for Adaptations and the International Centre for Sports History and Culture.");
        CH.setInTour(true);
        this.markerList.add(CH);

        this.markerList.add(new CustomLatLng(52.629220,-1.139771,
                "Q  - Queens Building::5","Technology." +"\n" +"School of Engineering." +"\n"
                +"Centre of Sustainable Energy." +"\n" +"Leicester Media School.",
                "If you can read this message then the author of this app has 'fucked shit up'"));

        CustomLatLng KL = new CustomLatLng(52.629015, -1.139162,
                "KL - Kimberlin Library::6", "Main library of DMU \n" + "+1500 study places \n"
                +"650+ computer workstations \n" +"Open 24/7",
                "Kimberlin Library is at the heart of the student learning experience, both as a physical resource and a " +
                        "virtual online service that is accessible anywhere online. During term time it is open 24 hours a day, " +
                        "seven days a week, giving you access to more than half a million publications and a wide range of DVDs, " +
                        "as well as e-resources and thousands of electronic journals. We have 1,500 study places and 650 computer " +
                        "workstations across four sites on campus.");
        KL.setInTour(true);
        this.markerList.add(KL);

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
