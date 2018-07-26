package com.rosebay.odds

import android.os.Bundle
import android.support.annotation.RestrictTo
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.rosebay.odds.ui.OnUsernameSavedInterface

@RestrictTo(RestrictTo.Scope.TESTS)
class SingleFragmentActivity : AppCompatActivity(), OnUsernameSavedInterface {
    override fun onUsernameSaved() {
        Log.i("username saved", "testing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = FrameLayout(this)
        content.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        content.id = R.id.fragment_container
        setContentView(content)
    }

    fun setFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, "TEST")
                .commit()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }
}