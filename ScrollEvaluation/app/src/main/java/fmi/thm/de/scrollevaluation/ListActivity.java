package fmi.thm.de.scrollevaluation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Created by Yannick on 31.05.2017.
 */

public class ListActivity extends AppCompatActivity implements SensorEventListener, GestureDetector.OnGestureListener {

    private static final String TAG = "ListActivity";

    private static final int REQUEST_CODE = 1337;

    //bundle keys
    public static final String SCROLL_KEY = "scrollType";
    public static final String LIST_KEY = "listType";

    //list types
    private static final String NUMERICAL = "Numerical (ordered)";
    private static final String ALPHABETICAL = "Alphabetical (semi ordered)";
    private static final String UNORDERED = "Images (chaotic)";

    //scrolling methods
    private static final String STANDARD = "Standard";
    private static final String VOLUME = "Volume Buttons";
    private static final String TILT_BACK_FORTH = "Tilt Back & Forth";
    private static final String DOT = "Dot";
    private static final String WHEEL = "Scrollwheel";
    private static final String TILT_LEFT_RIGHT = "Tilt Left & Right" ;

    private RecyclerView list;
    private RecyclerView.Adapter adapter;

    private ArrayList<String> data;

    private MyLayoutManager layoutManager;
    private int scrollMultiplier = 150;

    private String currentList = NUMERICAL;
    private String currentScroll = STANDARD;

    //TextField to show the current scrolling method
    private TextView scrollView;

    //Tilt Shit
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

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

        scrollView = (TextView) findViewById(R.id.scrollingTextView);

        list = (RecyclerView) findViewById(R.id.recycler_view);

        //Tilt Shit
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        initListeners();

        //improves performance
        list.setHasFixedSize(true);

        layoutManager = new MyLayoutManager(this);
        list.setLayoutManager(layoutManager);

        data = new ArrayList<>();

        //retrieve saved data
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        if (prefs != null) {
            currentList = prefs.getString(LIST_KEY, NUMERICAL);
            currentScroll = prefs.getString(SCROLL_KEY, STANDARD);
        }

    }

    public void initListeners()
    {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onDestroy()
    {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        mSensorManager.unregisterListener(this);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListeners();

        updateScrollView();
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
                intent.putExtra(LIST_KEY, currentList);
                intent.putExtra(SCROLL_KEY, currentScroll);
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
    protected void onPause()
    {
        mSensorManager.unregisterListener(this);

        //save the current state
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LIST_KEY, currentList);
        editor.putString(SCROLL_KEY, currentScroll);
        editor.commit();

        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE ) {
            if (resultCode == RESULT_OK) {

                Bundle settings = data.getExtras();
                String listType = settings.getString(LIST_KEY);
                String scrollType = settings.getString(SCROLL_KEY);

                if (!currentList.equals(listType)) {
                    currentList = listType;
                    setList();
                    }

                if (!currentScroll.equals(scrollType)) {
                    currentScroll = scrollType;
                }

                Log.e(TAG, listType);

            }
        }

    }

    //update the TextView which shows the currently activated scrolling method
    private void updateScrollView() {
        scrollView.setText(this.currentScroll);
    }

    private void setList() {
        switch (currentList) {
            case NUMERICAL:
                data.clear();
                list.setLayoutManager(layoutManager);
                fillListWithNumbers(150);
                adapter = new ListAdapter(data);
                list.setAdapter(adapter);
                break;
            case ALPHABETICAL:
                data.clear();
                list.setLayoutManager(layoutManager);
                fillListWithContacts();
                adapter = new ListAdapterSemi(data);
                list.setAdapter(adapter);
                break;
            case UNORDERED:
                data.clear();
                list.setLayoutManager(new GridLayoutManager(this, 2));
                fillListWithNumbers(50);
                adapter = new ListAdapterChaotic(data);
                list.setAdapter(adapter);
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

    private void scrollListDown(int scrollMultiplier) {

        list.smoothScrollBy(0, 1 * scrollMultiplier);
        Log.e(TAG, "" + scrollMultiplier);

    }

    private void scrollListUp() {
        scrollMultiplier++;
        list.smoothScrollBy(0, -1 * scrollMultiplier);

    }

    private void scrollListUp(int scrollMultiplier) {
        list.smoothScrollBy(0, -1 * scrollMultiplier);

    }
    //End volume scroll functions

    //Begin Tilt Stuff
    float[] inclineGravity = new float[3];
    float[] mGravity;
    float[] mGeomagnetic;
    float orientation[] = new float[3];
    float pitch;
    float roll;

    @Override
    public void onSensorChanged(SensorEvent event) {
        //If type is accelerometer only assign values to global property mGravity
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            mGravity = event.values;
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            mGeomagnetic = event.values;

            if (currentScroll.equals(TILT_BACK_FORTH)) {
                float breakPoint = -0.6f;
                float ignoreRange = .4f;
                float magicNumberOne = 13f;
                float magicNumberTwo = 3f;
                if (isTiltDownward(breakPoint, ignoreRange)) {
                    Log.d("test", "downwards");
                    int scrollMultiplier = (int) Math.abs(Math.pow((pitch-breakPoint)*magicNumberOne, magicNumberTwo));
                    scrollListDown(scrollMultiplier);
                } else if (isTiltUpward(breakPoint, ignoreRange))
                {
                    Log.d("test", "upwards");
                    int scrollMultiplier = (int) Math.abs(Math.pow((pitch-breakPoint)*magicNumberOne, magicNumberTwo));
                    Log.d("Pitch", String.valueOf(scrollMultiplier));
                    scrollListUp(scrollMultiplier);
                }
            }
            if (currentScroll.equals(TILT_LEFT_RIGHT)) {
                float breakPoint = 0.0f;
                float ignoreRange = .4f;
                float magicNumberOne = 13f;
                float magicNumberTwo = 3f;
                if (isTiltLeft(breakPoint, ignoreRange)) {
                    Log.d("test", "downwards");
                    int scrollMultiplier = (int) Math.abs(Math.pow((roll-breakPoint)*magicNumberOne, magicNumberTwo));
                    Log.d("Roll", String.valueOf(scrollMultiplier));
                    scrollListDown(scrollMultiplier);
                } else if (isTiltRight(breakPoint, ignoreRange))
                {
                    Log.d("test", "upwards");
                    int scrollMultiplier = (int) Math.abs(Math.pow((roll-breakPoint)*magicNumberOne, magicNumberTwo));
                    Log.d("Roll", String.valueOf(scrollMultiplier));
                    scrollListUp(scrollMultiplier);
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    public boolean isTiltUpward(float breakPoint, float ignoreRange)
    {
        if (mGravity != null && mGeomagnetic != null)
        {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success)
            {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                /*
                * If the roll is positive, you're in reverse landscape (landscape right), and if the roll is negative you're in landscape (landscape left)
                *
                * Similarly, you can use the pitch to differentiate between portrait and reverse portrait.
                * If the pitch is positive, you're in reverse portrait, and if the pitch is negative you're in portrait.
                *
                * orientation -> azimut, pitch and roll
                *
                *
                */

                pitch = orientation[1];
                roll = orientation[2];

                return (pitch + ignoreRange/2) < breakPoint;
            }
        }

        return false;
    }

    public boolean isTiltDownward(float breakPoint, float ignoreRange)
    {
        if (mGravity != null && mGeomagnetic != null)
        {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success)
            {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                pitch = orientation[1];
                roll = orientation[2];

                return (pitch - ignoreRange/2) > breakPoint;
            }
        }

        return false;
    }


    public boolean isTiltRight(float breakPoint, float ignoreRange)
    {
        if (mGravity != null && mGeomagnetic != null)
        {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success)
            {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                pitch = orientation[1];
                roll = orientation[2];

                return (roll + ignoreRange/2) < breakPoint;
            }
        }

        return false;
    }

    public boolean isTiltLeft(float breakPoint, float ignoreRange)
    {
        if (mGravity != null && mGeomagnetic != null)
        {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success)
            {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                pitch = orientation[1];
                roll = orientation[2];

                return (roll - ignoreRange/2) > breakPoint;
            }
        }

        return false;
    }
    //End Tilt Stuff

    // Start Swipe Scroll

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d("v", String.valueOf(v));
        Log.d("v1", String.valueOf(v1));
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.d("mevent", String.valueOf(motionEvent));
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d("v", String.valueOf(v));
        Log.d("v1", String.valueOf(v1));
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    // End Swipe Scroll

    private void startTest() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test scrolling method");
        //TODO: choose element to scroll to at random

        if (currentList.equals(NUMERICAL)) {
            testElement = "145";
            builder.setMessage("As soon as you press \"Ok\" a timer starts. Scroll to the element \"" +testElement+ "\" and click it. Your time will be saved. Ready?");
        } else if (currentList.equals(ALPHABETICAL)) {
            testElement="Terry Burgo";
            builder.setMessage("As soon as you press \"Ok\" a timer starts. Scroll to the element \"" +testElement+ "\" and click it. Your time will be saved. Ready?");
        } else {
            testElement="47";
            builder.setMessage("As soon as you press \"Ok\" a timer starts. Scroll to the element that says \"This\" and click it. Your time will be saved. Ready?");

        }


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (currentList.equals(NUMERICAL)) {
                    list.scrollToPosition(0);
                    ListAdapter la = (ListAdapter) adapter;
                    startTime = System.currentTimeMillis();
                    la.setStartTime(startTime);
                    la.setNrOfTestitem(testElement);
                } else if (currentList.equals(ALPHABETICAL)) {
                    list.scrollToPosition(0);
                    ListAdapterSemi las = (ListAdapterSemi) adapter;
                    startTime = System.currentTimeMillis();
                    las.setStartTime(startTime);
                    las.setNrOfTestitem(testElement);
                } else if (currentList.equals(UNORDERED)) {
                    list.scrollToPosition(0);
                    ListAdapterChaotic lac = (ListAdapterChaotic) adapter;
                    startTime = System.currentTimeMillis();
                    lac.setStartTime(startTime);
                    lac.setNrOfTestitem(testElement);
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
