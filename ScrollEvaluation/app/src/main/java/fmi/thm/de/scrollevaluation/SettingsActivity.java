package fmi.thm.de.scrollevaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Yannick on 04.06.2017.
 */

public class SettingsActivity extends AppCompatActivity {

    private Spinner typeSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        //set Adapter for spinner
        typeSpinner = (Spinner) findViewById(R.id.listTypeSpinner);
        ArrayAdapter<CharSequence> adapter  = ArrayAdapter.createFromResource(this, R.array.listOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                returnResults();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }


    private void returnResults() {

        //TODO: Settings for scroll types

        String listType = (String) typeSpinner.getSelectedItem();

        Intent intent = new Intent();
        intent.putExtra("listType", listType);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

}
