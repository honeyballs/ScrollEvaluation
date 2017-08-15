package fmi.thm.de.scrollevaluation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Farea on 15.08.2017.
 */

public class ListAdapterChaotic extends RecyclerView.Adapter<ListAdapterChaotic.ItemViewHolder> {

    private final String TAG = "ListAdapterChaotic";

    private ArrayList<String> items;
    private long startTime = 0;
    private String testItem = "";

    public ListAdapterChaotic (ArrayList<String> i) {
        items = i;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItemLayout = inflater.inflate(R.layout.list_item_images, parent, false);

        ItemViewHolder vh = new ItemViewHolder(listItemLayout, new ItemViewHolder.ViewHolderClicks() {
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
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        int titleNr = position + 1;
        holder.title.setText(""+titleNr);

        if (testItem.length() == 0) {
            holder.image.setImageResource(R.drawable.scroll_image_not);
        } else if (position == (Integer.parseInt(testItem) -1)){
            holder.image.setImageResource(R.drawable.scroll_image_this);
        } else {
            holder.image.setImageResource(R.drawable.scroll_image_not);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView image;
        public TextView title;
        public ViewHolderClicks listener;

        public ItemViewHolder(View rowView, ViewHolderClicks listener) {
            super(rowView);
            this.listener = listener;
            title = (TextView) rowView.findViewById(R.id.imageText);
            image = (ImageView) rowView.findViewById(R.id.imageView);
            image.setOnClickListener(this);
        }

        //Listener to stop the timer
        @Override
        public void onClick(View v) {

            long endTime = System.currentTimeMillis();
            listener.getTimeTaken(endTime, title.getText().toString());

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
