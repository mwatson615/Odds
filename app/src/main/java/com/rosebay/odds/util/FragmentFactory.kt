package com.rosebay.odds.util

import com.rosebay.odds.ui.createOdds.CreateOddsFragment
import com.rosebay.odds.ui.disclaimer.DisclaimerFragment
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsFragment
import com.rosebay.odds.ui.mainOdds.MainActivity
import com.rosebay.odds.ui.mainOdds.MainOddsFragment
import com.rosebay.odds.ui.myOdds.MyOddsFragment

class FragmentFactory : FragmentFactoryInt {

    private var creator = MainActivity.Companion

    override fun getCreateOddsFragment() {
        creator.create(CreateOddsFragment.newInstance())
    }

    override fun getMyOddsFragment() {
        creator.create(MyOddsFragment.newInstance())
    }

    override fun getFavoriteOddsFragment() {
        creator.create(FavoriteOddsFragment.newInstance())
    }

    override fun getMainOddsFragment() {
        creator.create(MainOddsFragment.newInstance())
    }

    override fun getDisclaimerFragment() {
        creator.create(DisclaimerFragment.newInstance())
    }
}