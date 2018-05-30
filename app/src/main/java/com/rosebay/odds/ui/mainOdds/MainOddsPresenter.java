package com.rosebay.odds.ui.mainOdds;


public interface MainOddsPresenter {

    void fetchOdds();

    void fetchSearchResults(String searchTerms);

    void fetchSingleOdd(String postId);

}
