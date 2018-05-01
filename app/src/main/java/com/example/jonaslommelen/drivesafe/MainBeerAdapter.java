package com.example.jonaslommelen.drivesafe;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonaslommelen.drivesafe.Data.BeerListContract;

public class MainBeerAdapter extends RecyclerView.Adapter<MainBeerAdapter.BeerViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private ListItemClickListener mListItemClickListener;

    public MainBeerAdapter(Context context, Cursor cursor, ListItemClickListener listener) {
        this.mContext = context;
        mCursor = cursor;
        mListItemClickListener = listener;
    }

    public interface ListItemClickListener{
        void onListItemClick(int position);
    }

    @Override
    public BeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.main_beer_list_item, parent, false);
        return new BeerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BeerViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;

        String name = mCursor.getString(mCursor.getColumnIndex(BeerListContract.BeerListEntry.COLUMN_BEER_NAME));
        int quantityInCl = mCursor.getInt(mCursor.getColumnIndex(BeerListContract.BeerListEntry.COLUMN_QUANTITY_IN_CL));

        holder.nameTextView.setText(name);
        holder.quantityInClTextView.setText(String.valueOf(quantityInCl));
        if(quantityInCl<26){
            holder.beerIconView.setImageResource(R.mipmap.twenty_five_cl);
        } else if(quantityInCl<40){
            holder.beerIconView.setImageResource(R.mipmap.thirty_cl);
        } else{
            holder.beerIconView.setImageResource(R.mipmap.fifty_cl);
        }


        long id = mCursor.getLong(mCursor.getColumnIndex(BeerListContract.BeerListEntry._ID));
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    class BeerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTextView;
        TextView quantityInClTextView;
        ImageView beerIconView;

        public BeerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_item_name);
            quantityInClTextView = (TextView) itemView.findViewById(R.id.tv_item_quantity);
            beerIconView = (ImageView) itemView.findViewById(R.id.icon_view);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mListItemClickListener.onListItemClick(clickedPosition);
        }

    }
}
