package com.example.jonaslommelen.drivesafe;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.BeerViewHolder> {
    private static final String TAG = BeerAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;
    private ListItemClickListener mListItemClickListener;

    public BeerAdapter(Context context, Cursor cursor, ListItemClickListener listener) {
        mContext = context;
        mCursor = cursor;
        mListItemClickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int position);
    }

    @Override
    public BeerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutIdForListItem = R.layout.beer_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new BeerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BeerViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;

        String name = mCursor.getString(mCursor.getColumnIndex("name"));

        holder.mNameTextView.setText(name);
        holder.itemView.setTag(mCursor.getString(mCursor.getColumnIndex("id")));

    }

    @Override
    public int getItemCount() {
        if(mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    public class BeerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mNameTextView;

        public BeerViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.tv_item_name);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mListItemClickListener.onListItemClick(clickedPosition);
        }

    }
}
