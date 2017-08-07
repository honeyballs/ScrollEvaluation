package fmi.thm.de.scrollevaluation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Farea on 07.08.2017.
 */

public class ListAdapterSemi extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "ListAdapterSemi";
    private final static int LETTER_VIEW_TYPE = 1;
    private final static int ITEM_VIEW_TYPE = 2;

    private ArrayList<String> items;
    private long startTime = 0;
    private String testItem = "";
    private int offset = 1;

    public ListAdapterSemi (ArrayList<String> i) {
        items = i;
    }

    @Override
    public int getItemViewType(int position) {

        if (items.get(position).length() > 1) {
            return ITEM_VIEW_TYPE;
        } else {
            return LETTER_VIEW_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case LETTER_VIEW_TYPE:
                View letterItemLayout = inflater.inflate(R.layout.list_item_semi_title, parent, false);
                DividerViewHolder dvh = new DividerViewHolder(letterItemLayout);
                return dvh;
            case ITEM_VIEW_TYPE:
                View listItemLayout = inflater.inflate(R.layout.list_item, parent, false);
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
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (items.get(position).length() > 1) {
            ItemViewHolder ivh = (ItemViewHolder) holder;
            ivh.itemText.setText(items.get(position));
        }  else {
            DividerViewHolder dvh = (DividerViewHolder) holder;
            dvh.letterText.setText(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView itemText;
        public ViewHolderClicks listener;

        public ItemViewHolder(View rowView, ViewHolderClicks listener) {
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

    public static class DividerViewHolder extends RecyclerView.ViewHolder {

        public TextView letterText;

        public DividerViewHolder(View itemView) {
            super(itemView);
            this.letterText = (TextView) itemView.findViewById(R.id.letterText);
        }
    }

    public void setStartTime(long time) {
        this.startTime = time;
    }

    public void setNrOfTestitem(String item) {
        testItem = item;
    }
}
