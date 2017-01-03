import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBRuner extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "gpsDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE = "gpsTrack";
    public static final String KEY_ID = "_id";
    public static final String LONG = "Long";
    public static final String WIGHT = "Wight";

    public DBRuner(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + "(" + KEY_ID
                + " INTEGER PRIMARY KEY NOT NULL," + LONG + " REAL NOT NULL," + WIGHT + " REAL NOT NULL" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
