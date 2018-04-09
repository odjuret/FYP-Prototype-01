package jimjam.dmusmartcampusapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * <p>Adapter for the view window intended to be used as the info window to appear when markers are
 * clicked on the google map.</p>
 *
 * @author Jimmie / p15241925
 */

public class CustomInfoWindowAdaper implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdaper(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view){

        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.title);

        if (!title.equals("")) {
            String[] stringArr = title.split("::");
            String textTitle = stringArr[0];
            tvTitle.setText(textTitle);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = (TextView) view.findViewById(R.id.snippet);

        if (!snippet.equals("")) {
            tvSnippet.setText(snippet);
        }

        String imageString = title.substring(0,2);
        if (imageString.contains(" ")) {
            imageString = imageString.substring(0, 1);
        }
        int imageId = mContext.getResources().getIdentifier(imageString.toLowerCase(),
                "drawable", mContext.getPackageName());
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(imageId);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
