package com.example.jonaslommelen.drivesafe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.BeerViewHolder> {
    private static final String TAG = BeerAdapter.class.getSimpleName();

    private int mNumberItems;
    private static int viewHolderCount;
    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public BeerAdapter(int numberOfItems, ListItemClickListener listener) {
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
        viewHolderCount = 0;
    }

    @Override
    public BeerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.number_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        BeerViewHolder viewHolder = new BeerViewHolder(view);
        viewHolder.viewHolderIndex.setText("ViewHolder index: " + viewHolderCount);
        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(BeerViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class BeerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView listItemNumberView;
        TextView viewHolderIndex;

        public BeerViewHolder(View itemView) {
            super(itemView);
            listItemNumberView = (TextView) itemView.findViewById(R.id.tv_item_name);
            viewHolderIndex = (TextView) itemView.findViewById(R.id.tv_item_volume);
            itemView.setOnClickListener(this);
        }


        void bind(int listIndex) {
            listItemNumberView.setText(String.valueOf(listIndex));
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
