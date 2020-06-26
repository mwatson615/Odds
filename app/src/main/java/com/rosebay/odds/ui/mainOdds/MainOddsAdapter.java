package com.rosebay.odds.ui.mainOdds;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rosebay.odds.R;
import com.rosebay.odds.model.SingleOdd;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainOddsAdapter extends RecyclerView.Adapter<MainOddsAdapter.OddsViewHolder> {

    private List<SingleOdd> mSingleOdds = new ArrayList<>();
    private Context mContext;
    private ClickListener mClickListener;

    public MainOddsAdapter(List<SingleOdd> oddList, ClickListener listener) {
        mSingleOdds = oddList;
        mClickListener = listener;
    }

    private String setPercentage(int percent) {
        return percent + "%";
    }

    @Override
    public OddsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_single_odd, parent, false);
        return new OddsViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(OddsViewHolder holder, int position) {
        SingleOdd singleOdd = mSingleOdds.get(position);

        holder.descriptionTextView.setText(singleOdd.getDescription());
        holder.percentageTextView.setText(setPercentage(singleOdd.getPercentage()));
        Uri uri = Uri.parse(singleOdd.getImageUrl());
        mContext = holder.imageView.getContext();
        Picasso.get().load(uri).fit().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return null != mSingleOdds ? mSingleOdds.size() : 0;
    }

    class OddsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.descriptionItem)
        TextView descriptionTextView;
        @BindView(R.id.percentageItem)
        TextView percentageTextView;
        @BindView(R.id.imageURLItem)
        ImageView imageView;

        private ClickListener mListener;

        OddsViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(mSingleOdds.get(getAdapterPosition()));
        }
    }

        public interface ClickListener {
            void onItemClicked(SingleOdd singleOdd);
        }

}
