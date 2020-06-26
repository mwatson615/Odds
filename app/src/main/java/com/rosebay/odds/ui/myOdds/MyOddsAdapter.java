package com.rosebay.odds.ui.myOdds;


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

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOddsAdapter extends RecyclerView.Adapter<MyOddsAdapter.MyOddsViewHolder>{

    private List<SingleOdd> mOddsList;
    private Context mContext;

    MyOddsAdapter(List<SingleOdd> oddList, Context context) {
        mOddsList = oddList;
        mContext = context;
    }

    public String setDueDate(String dueDate) {
        return mContext.getString(R.string.due_date_answer, dueDate);
    }

    private String setPercentage(int percent) {
        return percent + mContext.getString(R.string.percent_sign);
    }

    @Override
    public MyOddsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_single_odd, parent, false);
        return new MyOddsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyOddsViewHolder holder, int position) {
        SingleOdd singleOdd = mOddsList.get(position);

        holder.mCreatedByTextView.setVisibility(View.GONE);
        holder.mDescription.setText(singleOdd.getDescription());
        if (singleOdd.getDueDate().equals("")) {
            holder.mDueDateTextView.setVisibility(View.GONE);
        } else {
            holder.mDueDateTextView.setText(setDueDate(singleOdd.getDueDate()));
        }

        holder.mOddsAgainstTextView.setText(String.valueOf(singleOdd.getOddsAgainst()));
        holder.mOddsForTextView.setText(String.valueOf(singleOdd.getOddsFor()));
        holder.mPercentageTextView.setText(String.valueOf(setPercentage(singleOdd.getPercentage())));
        Uri uri = Uri.parse(singleOdd.getImageUrl());
        mContext = holder.mImageView.getContext();
        Picasso.get().load(uri).fit().into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return null != mOddsList ? mOddsList.size() : 0;
    }

    class MyOddsViewHolder extends RecyclerView.ViewHolder {

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

        MyOddsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
