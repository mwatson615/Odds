package com.rosebay.odds;

import com.rosebay.odds.network.FirebaseClient;
import com.rosebay.odds.ui.disclaimer.DisclaimerPresenterImpl;
import com.rosebay.odds.ui.disclaimer.DisclaimerView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DisclaimerPresenterTest {

    @Mock
    FirebaseClient mockFirebaseClient;
    @Mock
    DisclaimerView mockView;
    @InjectMocks
    DisclaimerPresenterImpl presenter;
    private TestScheduler scheduler;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        scheduler = new TestScheduler();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(h -> Schedulers.trampoline());
    }

    @Test
    public void testGetView() {
        assertEquals(mockView, presenter.getView());
    }

    @Test
    public void testSaveUsername() {
        when(mockFirebaseClient.addUser(anyString())).thenReturn(Completable.complete());
        presenter.saveUsername("username");
        scheduler.triggerActions();
        verify(mockView).onUserSaved(anyString());
    }

    @Test
    public void testSaveUsernameError() {
        when(mockFirebaseClient.addUser(anyString())).thenReturn(Completable.error(new Exception()));
        presenter.saveUsername("username");
        scheduler.triggerActions();
        verify(mockView).onError();
    }

    @After
    public void tearDown() {
        presenter.onViewDetached();
    }

}
