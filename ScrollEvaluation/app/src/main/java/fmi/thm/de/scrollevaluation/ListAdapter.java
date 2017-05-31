package fmi.thm.de.scrollevaluation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yannick on 31.05.2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private ArrayList<String> items;

    public ListAdapter (ArrayList<String> i) {
        items = i;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItemLayout = inflater.inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(listItemLayout);

        return vh;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {

        holder.itemText.setText(items.get(position));

        //Hier kann man dann Einträge an ner bestimmten position farblich ändern z.B.

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    //Create a ViewHolder to save the view
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemText;

        public ViewHolder(View rowView) {
            super(rowView);
            itemText = (TextView) rowView.findViewById(R.id.itemText);

        }
    }

}
