package jimjam.dmusmartcampusapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jimmie on 06/03/2018.
 */

public class MySqliteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "MySqliteOpenHelper";

    private static final String database_name = "markersdb.db";
    private String table_name = "markers";
    private static final int database_version = 3;

    private String column_title = "title", column_id = "id", column_snippet = "snippet", column_tourinfo = "tourinfo",
    column_longitude = "longitude", column_latitude = "latitude";

    private String database_create_statement =
            "create table "+ table_name+" ( "+column_id + " integer primary key autoincrement, "
                    + column_title+ " text not null,"
                    + column_latitude + " text not null,"
                    + column_longitude + " text not null,"
                    + column_snippet + " text not null,"
                    + column_tourinfo + " text not null"
                    + "  );";


    public MySqliteOpenHelper(Context context){
        super(context, database_name, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(database_create_statement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ table_name + ";" );
        onCreate(db);
    }


    public boolean addRow(String lat, String lon, String titlle, String snippet, String tinfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_latitude, lat);
        contentValues.put(column_longitude, lon);
        contentValues.put(column_title, titlle);
        contentValues.put(column_snippet, snippet);
        contentValues.put(column_tourinfo, tinfo);

        Log.d(TAG, "addDataInt: adding" + lat + lon + titlle + snippet +tinfo +" to " + table_name);
        long result = db.insert(table_name, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


}
