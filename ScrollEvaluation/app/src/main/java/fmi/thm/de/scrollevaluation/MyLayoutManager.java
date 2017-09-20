package fmi.thm.de.scrollevaluation;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Yannick on 09.06.2017.
 */

public class MyLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = false;

    public MyLayoutManager(Context context) {
        super(context);
    }
    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        boolean scroll = isScrollEnabled;
        setScrollEnabled(true);
        super.smoothScrollToPosition(recyclerView, state, position);
        setScrollEnabled(scroll);
    }
}
