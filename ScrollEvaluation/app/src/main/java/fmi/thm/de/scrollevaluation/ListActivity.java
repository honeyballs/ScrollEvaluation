package fmi.thm.de.scrollevaluation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;


/**
 * Created by Yannick on 31.05.2017.
 */

public class ListActivity extends AppCompatActivity {

    private RecyclerView list;
    private ListAdapter adapter;

    private ArrayList<String> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_layout);

        list = (RecyclerView) findViewById(R.id.recycler_view);

        //improves performance
        list.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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

    private void fillListWithNumbers(int length) {

        for (int i = 1; i<= length; i ++) {
            data.add("" + i);
        }

        adapter.notifyDataSetChanged();

    }
}
