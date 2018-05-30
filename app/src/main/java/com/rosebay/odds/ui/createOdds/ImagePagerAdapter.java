package com.rosebay.odds.ui.createOdds;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.rosebay.odds.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private List<String> mImageList;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private ImageButton leftArrowButton;
    private ImageButton rightArrowButton;

    public ImagePagerAdapter(Context context, List<String> imageList) {
        mContext = context;
        mImageList = imageList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mImageList != null) {
            return mImageList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.item_image_pager, container, false);
        ImageView imageView = itemView.findViewById(R.id.imagePagerImageView);
        leftArrowButton = itemView.findViewById(R.id.arrowLeft);
        rightArrowButton = itemView.findViewById(R.id.arrowRight);
        if (position == 0) {
            leftArrowButton.setVisibility(View.INVISIBLE);
        } else if (position == mImageList.size() - 1) {
            rightArrowButton.setVisibility(View.INVISIBLE);
        }

        Picasso.with(mContext).load(mImageList.get(position)).fit().into(imageView);
        getCurrentUrl(position);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public String getCurrentUrl(int position) {
        return mImageList.get(position);
    }

}
