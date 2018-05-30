package com.rosebay.odds;

import com.google.firebase.database.DatabaseReference;
import com.rosebay.odds.ui.myOdds.MyOddsPresenterImpl;
import com.rosebay.odds.ui.myOdds.MyOddsView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MyOddsPresenterTest {

    @Mock
    MyOddsView mockView;
    @InjectMocks
    MyOddsPresenterImpl presenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(h -> Schedulers.trampoline());
    }

    @Test
    public void testGetView() {
        assertEquals(mockView, presenter.getView());
    }

    @After
    public void tearDown() {
        presenter.onViewDetached();
    }

}
