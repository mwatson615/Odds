package com.rosebay.odds;

import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.network.CloudFunctionsClient;
import com.rosebay.odds.network.FirebaseClient;
import com.rosebay.odds.ui.mainOdds.MainOddsPresenterImpl;
import com.rosebay.odds.ui.mainOdds.MainOddsView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainOddsPresenterTest {

    @Mock
    FirebaseClient mockFirebaseClient;
    @Mock
    CloudFunctionsClient mockCloudClient;
    @Mock
    MainOddsView mockView;
    @InjectMocks
    MainOddsPresenterImpl presenter = new MainOddsPresenterImpl();
    private List<SingleOdd> testList;
    private List<SingleOdd> emptyList;
    private SingleOdd testSingleOdd;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testSingleOdd = createTestSingleOdd();
        testList = createList();
        emptyList = new ArrayList<>();
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

    @Test
    public void testFetchSearchResults() {
        when(mockCloudClient.search(anyString())).thenReturn(Single.just(testList));
        presenter.fetchSearchResults("searchTerms");
        verify(mockView).setData(testList);
    }

    @Test
    public void testFetchSearchResultsEmpty() {
        when(mockCloudClient.search(anyString())).thenReturn(Single.just(emptyList));
        presenter.fetchSearchResults("searchTerms");
        verify(mockView).onNoSearchResults();
    }

    @Test(expected = Exception.class)
    public void testFetchSearchResultsError() {
        when(mockCloudClient.search(anyString())).thenThrow(new Throwable());
        presenter.fetchSearchResults("searchTerms");
        verify(mockView).onError();
    }

    @After
    public void tearDown() {
        presenter.onViewDetached();
    }

    public List<SingleOdd> createList() {
        List<SingleOdd> list = new ArrayList<>();
        list.add(createTestSingleOdd());
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
