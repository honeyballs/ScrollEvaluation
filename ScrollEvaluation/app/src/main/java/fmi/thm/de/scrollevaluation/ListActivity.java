package fmi.thm.de.scrollevaluation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;


/**
 * Created by Yannick on 31.05.2017.
 */

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";

    private static final int REQUEST_CODE = 1337;

    private RecyclerView list;
    private ListAdapter adapter;

    private ArrayList<String> data;

    private MyLayoutManager layoutManager;
    private int scrollMultiplier = 150;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        list = (RecyclerView) findViewById(R.id.recycler_view);

        //improves performance
        list.setHasFixedSize(true);

        layoutManager = new MyLayoutManager(this);
        list.setLayoutManager(layoutManager);

        data = new ArrayList<>();
        adapter = new ListAdapter(data);

        list.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

        this.fillListWithNumbers(150);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.settings_item:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE ) {
            if (resultCode == RESULT_OK) {

                Bundle settings = data.getExtras();
                String listType = settings.getString("listType");
                Log.e(TAG, listType);

            }
        }

    }

    private void fillListWithNumbers(int length) {

        for (int i = 1; i<= length; i ++) {
            data.add("" + i);
        }

        adapter.notifyDataSetChanged();

    }

    //Override Volume Keys to scroll
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                scrollListUp();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                scrollListDown();
                return true;
            default:
                return super.onKeyDown(keyCode, event);

        }
    }


    //Reset multiplier when button is released
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            scrollMultiplier = 150;
        }

        return super.onKeyUp(keyCode, event);
    }



    private void scrollListDown() {

        scrollMultiplier++;
        list.smoothScrollBy(0, 1 * scrollMultiplier);
        Log.e(TAG, "" + scrollMultiplier);

    }

    private void scrollListUp() {
        scrollMultiplier++;
        list.smoothScrollBy(0, -1 * scrollMultiplier);

    }

    //TODO: more adapters/methods for list creation
}
