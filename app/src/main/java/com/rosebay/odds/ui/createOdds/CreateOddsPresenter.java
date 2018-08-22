package com.rosebay.odds.ui.createOdds;

import com.rosebay.odds.model.SingleOdd;

public interface CreateOddsPresenter {

    void createOdds(SingleOdd singleOdd);

    void getImages(String description, String query);

    void addUserVote(SingleOdd singleOdd);

    void onViewAttached(CreateOddsView view);

}
