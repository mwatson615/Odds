package com.rosebay.odds.network;

import com.rosebay.odds.model.SingleOdd;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CloudFunctionsApi {

    @POST("upVote")
    Single<SingleOdd> voteYes(@Body Map<String, String> postIdMap);

    @POST("downVote")
    Single<SingleOdd> voteNo(@Body Map<String, String> postIdMap);

    @GET("search")
    Single<List<SingleOdd>> search(@Query("searchTerms") String searchTerms);

}
