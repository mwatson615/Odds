package com.rosebay.odds.network;


import com.rosebay.odds.BuildConfig;
import com.rosebay.odds.model.ImageResponse;
import com.rosebay.odds.util.Constants;

import io.reactivex.Observable;

public class ImageClient {

    private ImageApi mImageApi;

    public ImageClient(ImageApi imageApi) {
        mImageApi = imageApi;
    }

    public Observable<ImageResponse> fetchImages(String description, String queryTerms) {
        return mImageApi.getImages(
                BuildConfig.ApiKey, BuildConfig.CustomSearchEngine, description, queryTerms,
                Constants.IMAGE_SEARCH_NUM, Constants.IMAGE_SEARCH_TYPE);
    }

}
