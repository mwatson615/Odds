package com.rosebay.odds.ui.myOdds;


import com.rosebay.odds.model.SingleOdd;

import java.util.List;

public interface MyOddsView {

    void onResponse(List<SingleOdd> response);

    void onNoResponse();

    void onError();

    void onLoading();
}
