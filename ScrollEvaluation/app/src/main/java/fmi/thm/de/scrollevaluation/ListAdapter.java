package fmi.thm.de.scrollevaluation;

import android.content.DialogInterface;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yannick on 31.05.2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private final String TAG = "ListAdapter";

    private ArrayList<String> items;
    private long startTime = 0;
    private String testItem = "";

    public ListAdapter (ArrayList<String> i) {
        items = i;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItemLayout = inflater.inflate(R.layout.list_item, parent, false);

        ViewHolder vh = new ViewHolder(listItemLayout, new ViewHolder.ViewHolderClicks() {
            @Override
            public void getTimeTaken(long endTime, String item) {

                if (item.equals(testItem)){

                    long totalTime = endTime - startTime;
                    int seconds = (int) (totalTime / 1000);
                    int minutes = seconds/60;
                    String displayTime = String.format("%d:%02d", minutes, seconds);
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                    builder.setTitle("Result");
                    builder.setMessage("You took "+ displayTime + " to scroll to the given element.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }

            }
        });

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


    //Create a ItemViewHolder to save the view
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView itemText;
        public ViewHolderClicks listener;

        public ViewHolder(View rowView, ViewHolderClicks listener) {
            super(rowView);
            this.listener = listener;
            rowView.setOnClickListener(this);
            itemText = (TextView) rowView.findViewById(R.id.itemText);

        }

        //Listener to stop the timer
        @Override
        public void onClick(View v) {

            long endTime = System.currentTimeMillis();
            listener.getTimeTaken(endTime, itemText.getText().toString());

        }

        public  interface ViewHolderClicks {
             void getTimeTaken(long endTime, String item);
        }

    }


    public void setStartTime(long time) {
        this.startTime = time;
    }

    public void setNrOfTestitem(String item) {
        testItem = item;
    }

}
