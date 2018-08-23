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

    void setCreationInfo(String username, String creationDate);

    void onRemovedFromFavorites();

    void setFavoritesBtn(Boolean isFavorite);

}
