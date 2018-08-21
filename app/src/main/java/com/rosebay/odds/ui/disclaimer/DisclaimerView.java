package com.rosebay.odds.ui.disclaimer;

public interface DisclaimerView {

    void onUsernameAvailable();

    void onUsernameTaken();

    void onError();

    void showSearchLayout();

    void showProgressLayout();

    void onUserSaved(String username);
}
