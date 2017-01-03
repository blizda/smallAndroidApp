import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class PostData extends AsyncTask {
    private Date dat;
    private Double longe;
    private Double wight;
    private String IEML;

    public PostData(Long dat, Double longe, Double wight, String IEML){
        this.dat = new java.sql.Date(dat);
        this.longe = longe;
        this.wight = wight;
        this.IEML = IEML;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String myURL = "http://gpsmaptest.lh1.in";

        String parammetrs ="IMEI=" + IEML + "&time=" + dat + "&long=" + longe + "&wight=" + wight +"&pass=fuohEW932nfwR";
        byte[] data = null;
        InputStream is = null;
        try {
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Length", "" + Integer.toString(parammetrs.getBytes().length));
            OutputStream os = conn.getOutputStream();
            data = parammetrs.getBytes("UTF-8");
            os.write(data);
            data = null;

            conn.connect();
            int responseCode= conn.getResponseCode();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if (responseCode == 200) {
                is = conn.getInputStream();
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                data = baos.toByteArray();
                String resultString = new String(data, "UTF-8");
                Log.i("put_data", resultString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    return null;
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }
}

