package com.rosebay.odds.ui.mainOdds;


import com.rosebay.odds.model.SingleOdd;

import java.util.List;

public interface MainOddsView {

    void setData(List<SingleOdd> singleOddList);

    void onError();

    void onNoSearchResults();

    void showBackButtonOnSearchResults();

    void showInitialToolbarLayout();

    void onLoading();

    void launchSingleOdd(SingleOdd singleOdd);
}
