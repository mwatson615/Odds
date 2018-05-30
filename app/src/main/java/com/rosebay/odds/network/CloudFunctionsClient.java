package com.rosebay.odds.network;

import com.rosebay.odds.model.SingleOdd;

import java.util.Map;

import io.reactivex.Single;

public class CloudFunctionsClient {

    private CloudFunctionsApi mCloudFunctionsApi;

    public CloudFunctionsClient(CloudFunctionsApi cloudFunctionsApi) {
        mCloudFunctionsApi = cloudFunctionsApi;
    }

    public Single<SingleOdd> submitVoteYes(Map<String, String> postIdMap) {
        return mCloudFunctionsApi.voteYes(postIdMap);
    }

    public Single<SingleOdd> submitNoVote(Map<String, String> postIdMap) {
        return mCloudFunctionsApi.voteNo(postIdMap);
    }
}
