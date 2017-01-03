import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends AppCompatActivity implements LocationListener {

    static final private int CHOOSE_THIEF = 0;
    private LocationManager lm;
    private static final int PERMISSION_REQUEST = 1;
    private TextView gpsLockView;
    private DBRuner db;
    private SQLiteDatabase mGPSBaseWrite;
    private SQLiteDatabase mGPSBaseRead;
    private Cursor dbCollection;
    private ArrayList uniqInf = new ArrayList();
    private boolean isFerstStart = false;
    private double long_gps;
    private double wight_gps;
    private long dataBack;
    private boolean isDatabaseRead = false;
    private String ieml;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_THIEF) {
            if (resultCode == RESULT_OK) {
                dataBack = data.getLongExtra(MainActivityList.THIEF, 0);
                serchLongAndWight(dbCollection, dataBack);
                new MyImageLoad((ImageView) findViewById(R.id.imageView1))
                        .execute("https://maps.googleapis.com/maps/api/staticmap?zoom=17&size=800x800&scale=2&maptype=roadmap&markers=color:blue%7Clabel:S%7C"
                                + long_gps + "," + wight_gps + "&key=");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            isFerstStart = true;
        }
        if (!isDatabaseRead)
            readDb();
        if (ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE}, PERMISSION_REQUEST);
            getGPS();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            new MyImageLoad((ImageView) findViewById(R.id.imageView1))
                    .execute("https://maps.googleapis.com/maps/api/staticmap?zoom=17&size=800x800&scale=2&maptype=roadmap&markers=color:blue%7Clabel:S%7C"
                            + long_gps + "," + wight_gps + "&key=");
        }
    }

    public void openTable(View view) {
        Intent intent = new Intent(MainActivity.this, MainActivityList.class);
        if (!isDatabaseRead)
            putCursorToIntent(dbCollection);
        intent.putExtra("id", uniqInf);
        startActivityForResult(intent, CHOOSE_THIEF);
    }


    public void putCursorToIntent(Cursor mCurs) {
        for (int idx = 0; idx < mCurs.getCount() - 1; idx++) {
            mCurs.moveToNext();
            long time_id = mCurs.getLong(0);
            if (!uniqInf.contains(time_id))
                uniqInf.add(0, time_id);
        }
        isDatabaseRead = true;
    }

    public void serchLongAndWight(Cursor mCurs, long dataBack) {
        mCurs.moveToFirst();
        for (int idx = 0; idx < mCurs.getCount() - 1; idx++) {
            mCurs.moveToNext();
            long time_id = mCurs.getLong(0);
            if (time_id == dataBack) {
                long_gps = Double.parseDouble(mCurs.getString(1).substring(0, 5));
                wight_gps = Double.parseDouble(mCurs.getString(2).substring(0, 5));
                break;
            }
        }
    }

    public void getGPS(){
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        }else{
            lm.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);
        }
        gpsLockView = (TextView) findViewById(R.id.GPS);
        gpsLockView.setText((lm.getLastKnownLocation(GPS_PROVIDER).getLatitude() + "\n"
                + lm.getLastKnownLocation(GPS_PROVIDER).getLongitude() + "\n" +
                lm.getLastKnownLocation(GPS_PROVIDER).getTime()));
        uniqInf.add(lm.getLastKnownLocation(GPS_PROVIDER).getTime());
        long_gps = lm.getLastKnownLocation(GPS_PROVIDER).getLatitude();
        wight_gps = lm.getLastKnownLocation(GPS_PROVIDER).getLongitude();
        if (isFerstStart){
            writeInDb(lm.getLastKnownLocation(GPS_PROVIDER).getTime(),
                    lm.getLastKnownLocation(GPS_PROVIDER).getLatitude(), lm.getLastKnownLocation(GPS_PROVIDER).getLongitude());
            PostData pd = new PostData(lm.getLastKnownLocation(GPS_PROVIDER).getTime(),
                    lm.getLastKnownLocation(GPS_PROVIDER).getLatitude(), lm.getLastKnownLocation(GPS_PROVIDER).getLongitude(), ieml);
            pd.execute();
        }
    }

    private void writeInDb(final long time, final double latitude, final double longitud){
        class backgrountDB extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                if (db == null) {
                    db = new DBRuner(MainActivity.this);
                    Log.i("db", "null");
                }
                try {
                    mGPSBaseWrite = db.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(db.KEY_ID, time);
                    Log.i("inf_about_ins", time + " ");
                    cv.put(db.LONG, latitude);
                    cv.put(db.WIGHT, longitud);
                    mGPSBaseWrite.insert(db.TABLE, null, cv);
                }
                catch(Exception e){
                    Log.e("db_error", "somfing gous wrong");
                }
                return null;
            }
        }
        backgrountDB nl = new backgrountDB();
        nl.execute();
    }

    private void readDb() {
        class backgrountDB extends AsyncTask<Cursor, Void, Cursor> {
            Cursor myNew;
            SQLiteDatabase owenDb;
            @Override
            protected Cursor doInBackground(Cursor... params) {
                if (db == null) {
                    db = new DBRuner(MainActivity.this);
                }
                owenDb = db.getReadableDatabase();
                myNew = owenDb.query(db.TABLE, null, null, null, null, null, null);
                return myNew;
            }
            protected void onPostExecute(Cursor result) {
                dbCollection = result;
            }
        }
        backgrountDB nl = new backgrountDB();
        nl.execute();
    }

    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            uniqInf.add(location.getTime());
            gpsLockView.setText(location.getLatitude() + "\n" +
                    location.getLongitude() + "\n" +
                    location.getTime());
            writeInDb(location.getTime(), location.getLatitude(), location.getLongitude());
            Log.i("inf", "loc change");
            long_gps = location.getLatitude();
            wight_gps = location.getLongitude();
            PostData postData = new PostData(location.getTime(), long_gps, wight_gps, ieml);
            new MyImageLoad((ImageView) findViewById(R.id.imageView1))
                    .execute("https://maps.googleapis.com/maps/api/staticmap?zoom=17&size=800x800&scale=2&maptype=roadmap&markers=color:blue%7Clabel:S%7C"
                            +long_gps +","+ wight_gps +"&key=");
            postData.execute();

        }
    }

    public void onProviderDisabled(String provider)
    {

    }

    public void onProviderEnabled(String provider)
    {
    }

    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

}