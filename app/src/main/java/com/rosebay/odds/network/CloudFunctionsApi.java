package com.rosebay.odds.network;

import com.rosebay.odds.model.SingleOdd;

import java.util.Map;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CloudFunctionsApi {

    @POST("upVote")
    Single<SingleOdd> voteYes(@Body Map<String, String> postIdMap);

    @POST("downVote")
    Single<SingleOdd> voteNo(@Body Map<String, String> postIdMap);

}
