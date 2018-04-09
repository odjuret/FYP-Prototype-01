package jimjam.dmusmartcampusapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * <p>Database handler class. Handles creation, version control and transactions of databases.
 * Will create a local folder storing text files representing databases and
 * tables contained within. Folder is unreachable to a unmodified Android operating system, the majority of users
 * can not access it. A arbitrary database solution for the purpose of this project.</p>
 *
 * <p>When an instance of this class is created the onCreate method is called. Creating the local
 * database folders and files.</p>
 *
 * <p class="note"><strong>Note:</strong> this class assumes
 * monotonically increasing version numbers for upgrades.</p>
 *
 * @author Jimmie / p15241925
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


    /**
     * default constructor. calls the super SQLiteOpenHelper constructor.
     * Below info taken from said constructor:
     *
     * "Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called."
     *
     * database_version is the version number of the database (starting at 1); if the database is older,
     *     {@link #onUpgrade} will be used to upgrade the database
     *
     * @param context       the applications context
     */
    public MySqliteOpenHelper(Context context){
        super(context, database_name, null, database_version);
    }

    /**
     * called the first time the database is created.
     *
     * @param db        the database to be created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(database_create_statement);
    }

    /**
     * Upon instantiation of this class. If an existing database with the same name exists it will
     * check the version number and discard older version to create a new updated version.
     *
     *
     * @param db                The database to be checked
     * @param oldVersion        old version of database
     * @param newVersion        new version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ table_name + ";" );
        onCreate(db);
    }

    /**
     * populate the markers table with a row. 1 row represents 1 CustomLatLng object.
     *
     * @param lat           latitude coordinates as a string
     * @param lon           longitude coordinates as aatring
     * @param titlle        title of the marker
     * @param snippet       marker snippet information
     * @param tinfo         touring popup window information
     * @return              .insert method will return -1 if unsuccessful
     */
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
