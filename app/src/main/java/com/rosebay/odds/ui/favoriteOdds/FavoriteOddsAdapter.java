package com.rosebay.odds.ui.favoriteOdds;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rosebay.odds.R;
import com.rosebay.odds.model.SingleOdd;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteOddsAdapter extends RecyclerView.Adapter<FavoriteOddsAdapter.FavoriteOddsViewHolder> {
    private List<SingleOdd> mOddsList;
    private Context mContext;

    FavoriteOddsAdapter(List<SingleOdd> oddsList, Context context) {
        mOddsList = oddsList;
        mContext = context;
    }

    @NonNull
    @Override
    public FavoriteOddsAdapter.FavoriteOddsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_single_odd, parent, false);
        return new FavoriteOddsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteOddsAdapter.FavoriteOddsViewHolder holder, int position) {
        SingleOdd singleOdd = mOddsList.get(position);
        holder.mCreatedByTextView.setText(mContext.getResources().getString(R.string.created_by, singleOdd.getUsername()));
        holder.mDescription.setText(singleOdd.getDescription());
        if (TextUtils.isEmpty(singleOdd.getDueDate())) {
            holder.mDueDateTextView.setVisibility(View.GONE);
        } else {
            holder.mDueDateTextView.setText(mContext.getResources().getString(R.string.due_date_answer, singleOdd.getDueDate()));
        }
        holder.mOddsAgainstTextView.setText(String.valueOf(singleOdd.getOddsAgainst()));
        holder.mOddsForTextView.setText(String.valueOf(singleOdd.getOddsFor()));
        holder.mPercentageTextView.setText(mContext.getResources().getString(R.string.percentage_text, singleOdd.getPercentage()));
        Uri uri = Uri.parse(singleOdd.getImageUrl());
        mContext = holder.mImageView.getContext();
        Picasso.with(mContext).load(uri).fit().into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return null != mOddsList ? mOddsList.size() : 0;
    }

    class FavoriteOddsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageURLItem)
        ImageView mImageView;
        @BindView(R.id.descriptionItem)
        TextView mDescription;
        @BindView(R.id.createdByTextView)
        TextView mCreatedByTextView;
        @BindView(R.id.dueDateTextView)
        TextView mDueDateTextView;
        @BindView(R.id.percentageSingleOdd)
        TextView mPercentageTextView;
        @BindView(R.id.oddsForTextView)
        TextView mOddsForTextView;
        @BindView(R.id.oddsAgainstTextView)
        TextView mOddsAgainstTextView;

        public FavoriteOddsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
