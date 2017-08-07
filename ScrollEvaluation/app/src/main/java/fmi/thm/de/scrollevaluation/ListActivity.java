package fmi.thm.de.scrollevaluation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Created by Yannick on 31.05.2017.
 */

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";

    private static final int REQUEST_CODE = 1337;

    //list types
    private static final String NUMERICAL = "Numerical (ordered)";
    private static final String ALPHABETICAL = "Alphabetical (semi ordered)";
    private static final String UNORDERED = "Images (chaotic)";

    //scrolling methods
    private static final String STANDARD = "Standard";
    private static final String VOLUME = "Volume Buttons";
    private static final String DOT = "Dot";
    private static final String WHEEL = "Scrollwheel";

    private RecyclerView list;
    private RecyclerView.Adapter adapter;

    private ArrayList<String> data;

    private MyLayoutManager layoutManager;
    private int scrollMultiplier = 150;

    private String currentList = NUMERICAL;
    private String currentScroll = STANDARD;

    //Timer variables
    private String testElement="145";
    private long startTime = 0;
    private long endTime = 0;

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        setList();

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
            case R.id.time_item:
                startTest();
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
                String scrollType = settings.getString("scrollType");

                if (!currentList.equals(listType)) {
                    currentList = listType;
                    setList();
                    }

                if (!currentScroll.equals(scrollType)) {
                    currentScroll = scrollType;
                    //TODO: Change scrolling
                }

                Log.e(TAG, listType);

            }
        }

    }

    private void setList() {
        switch (currentList) {
            case NUMERICAL:
                data.clear();
                fillListWithNumbers(150);
                adapter = new ListAdapter(data);
                list.setAdapter(adapter);
                break;
            case ALPHABETICAL:
                data.clear();
                fillListWithContacts();
                adapter = new ListAdapterSemi(data);
                list.setAdapter(adapter);
                break;
            default:
                break;
        }
    }

    private void fillListWithNumbers(int length) {
        for (int i = 1; i<= length; i ++) {
            data.add("" + i);
        }
    }

    private void fillListWithContacts() {
        String[] contacts = getResources().getStringArray(R.array.nameList);
        Arrays.sort(contacts);
        for (int i = 0; i<contacts.length; i++) {
            if ( i == 0) {
                data.add(contacts[i].substring(0,1));
                data.add(contacts[i]);
            } else if (i == contacts.length -1) {
                data.add(contacts[i]);
            } else if (!(contacts[i].substring(0,1).equals(contacts[i + 1].substring(0,1)))) {
                data.add(contacts[i]);
                data.add(contacts[i+1].substring(0,1));
            } else {
                data.add(contacts[i]);
            }
        }
    }

    //Override Volume Keys to scroll
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (currentScroll.equals(VOLUME)) scrollListUp();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (currentScroll.equals(VOLUME)) scrollListDown();
                return true;
            default:
                return super.onKeyDown(keyCode, event);

        }
    }

    //Begin volume scroll functions
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
    //End volume scroll functions

    private void startTest() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test scrolling method");
        //TODO: choose element to scroll to at random

        if (currentList.equals(NUMERICAL)) {
            testElement = "145";
        } else if (currentList.equals(ALPHABETICAL)) {
            testElement="Terry Burgo";
        }

        builder.setMessage("As soon as you press \"Ok\" a timer starts. Scroll to the element \"" +testElement+ "\" and click it. Your time will be saved. Ready?");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (currentList.equals(NUMERICAL)) {
                    ListAdapter la = (ListAdapter) adapter;
                    startTime = System.currentTimeMillis();
                    la.setStartTime(startTime);
                    la.setNrOfTestitem(testElement);
                } else if (currentList.equals(ALPHABETICAL)) {
                    ListAdapterSemi las = (ListAdapterSemi) adapter;
                    startTime = System.currentTimeMillis();
                    las.setStartTime(startTime);
                    las.setNrOfTestitem(testElement);
                }

                Log.e(TAG, ""+startTime);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }

    //TODO: more adapters/methods for list creation
}
