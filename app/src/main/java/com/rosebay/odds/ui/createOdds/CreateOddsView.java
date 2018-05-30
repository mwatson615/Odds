package com.rosebay.odds.ui.createOdds;


import java.util.List;

public interface CreateOddsView {

    void loadImages(List<String> imageItemList);

    void onError();

    void onSearch();

    void onSave(String description);

    void clearTextFields();

    void closeKeyboard();

}
