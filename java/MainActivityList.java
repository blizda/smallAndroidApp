import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivityList extends AppCompatActivity {

    public final static String THIEF = "com.example.glados.myowngps.MainActivityList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        ListView listView = (ListView) findViewById(R.id.listView);
        Intent intent = getIntent();
        final ArrayList myNew = intent.getParcelableArrayListExtra("id");
        ArrayList timeSet = new ArrayList();
        for (Object time : myNew) {
            Date dat = new Date((Long) time);
            timeSet.add(dat);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, timeSet);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Intent answerIntent = new Intent();
                answerIntent.putExtra(THIEF, (Long) myNew.get((int) id));
                setResult(RESULT_OK, answerIntent);
                finish();
            }
        });

    }
}
