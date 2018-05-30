package com.rosebay.odds.network;


import com.rosebay.odds.model.ImageResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ImageApi {

    @GET("v1")
    Observable<ImageResponse> getImages(@Query("key") String key,
                                        @Query("cx") String customSearch,
                                        @Query("q") String description,
                                        @Query("hq") String queryTerms,
                                        @Query("num") int count,
                                        @Query("searchType") String searchType
                                        );

}
