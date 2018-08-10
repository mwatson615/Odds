package com.rosebay.odds.ui.singleOdd;


public interface SingleOddView {

    void setPercentage(int percentage);

    void setOddsFor(int oddsFor);

    void setOddsAgainst(int oddsAgainst);

    void setImageUrl(String imageUrl);

    void setDescription(String description);

    void onAddedToFavorites();

    void onError();

    void onVoteSuccess();

    void disableVoteButtons();

    void enableVoteButtons();

    void disableFavoritesButton();

    void enableFavoritesButton();

    void setCreationInfo(String username, String creationDate);

}
