package com.rosebay.odds.ui.favoriteOdds;

import com.rosebay.odds.model.SingleOdd;

import java.util.List;

public interface FavoriteOddsView {

    void setData(List<SingleOdd> singleOdds);

    void onError();

    void noFavorites();

    void onLoading();

}
