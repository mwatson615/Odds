package com.rosebay.odds.ui.singleOdd;


import com.rosebay.odds.model.SingleOdd;

public interface SingleOddPresenter {

    void addToFavorites(String username, String postId);

    void voteYes(String postId);

    void voteNo(String postId);

    void checkForFavorite(String postId);

    void checkIfVoted(String postId);

    void loadOddsData(SingleOdd singleOdd);

    void removeFromFavorites(String postId);

}
