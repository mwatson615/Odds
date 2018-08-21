package com.rosebay.odds;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.network.FirebaseClient;
import com.rosebay.odds.ui.mainOdds.MainOddsPresenterImpl;
import com.rosebay.odds.ui.mainOdds.MainOddsView;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import durdinapps.rxfirebase2.RxFirebaseQuery;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainOddsPresenterTest {

    @Mock
    RxFirebaseQuery mockQuery;
    @Mock
    DataSnapshot mockSnapshot;
    @Mock
    FirebaseClient mockFirebaseClient;
    @Mock
    MainOddsView mockView;
    @InjectMocks
    MainOddsPresenterImpl presenter = new MainOddsPresenterImpl();
    private List<DataSnapshot> testSnapshotList;
    private SingleOdd testSingleOdd;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testSingleOdd = createTestSingleOdd();
        testSnapshotList = createSnapshots();
        presenter.setViewInterface(mockView);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(h -> Schedulers.trampoline());
    }

    @Test
    public void testGetView() {
        assertEquals(mockView, presenter.getView());
    }

    @Test
    public void testFetchSingleOdd() {
        when(mockFirebaseClient.fetchSingleOdd(anyString())).thenReturn(Single.just(testSingleOdd));
        presenter.fetchSingleOdd("postId");
        verify(mockView).launchSingleOdd(testSingleOdd);
    }

    @Test(expected = Exception.class)
    public void testFetchSingleOddError() {
        when(mockFirebaseClient.fetchSingleOdd(anyString())).thenThrow(new Exception());
        presenter.fetchSingleOdd("postId");
        verify(mockView).onError();
    }

    @Ignore
    @Test
    public void testFetchSearchResults() {
        when(mockQuery.filterByRefs(any(DatabaseReference.class), any(Query.class))).thenReturn(mockQuery);
        presenter.fetchSearchResults("searchTerms");

        verify(mockView).setData(anyList());
    }

    @After
    public void tearDown() {
        presenter.onViewDetached();
    }

    public List<DataSnapshot> createSnapshots() {
        List<DataSnapshot> list = new ArrayList<>();
        list.add(mockSnapshot);
        return list;
    }

    private SingleOdd createTestSingleOdd() {
        SingleOdd singleOdd = new SingleOdd();
        singleOdd.setPercentage(50);
        singleOdd.setOddsAgainst(25);
        singleOdd.setDueDate("12/27/1978");
        singleOdd.setOddsFor(25);
        singleOdd.setDateSubmitted("01/01/2018");
        singleOdd.setImageUrl("https://images.pexels.com/photos/937465/pexels-photo-937465.jpeg?auto=compress&cs=tinysrgb&h=350");
        singleOdd.setDescription("The Buddha");
        singleOdd.setPostId("12345");
        return singleOdd;
    }

}
