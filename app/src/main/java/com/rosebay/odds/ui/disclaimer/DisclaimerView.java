package com.rosebay.odds.ui.disclaimer;

public interface DisclaimerView {

    void onUsernameAvailable(String username);

    void onUsernameTaken();

    void onError();

    void showSearchLayout();

    void showProgressLayout();

    void onUserSaved(String username);
}
