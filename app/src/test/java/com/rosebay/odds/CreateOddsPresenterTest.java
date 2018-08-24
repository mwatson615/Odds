package com.rosebay.odds;


import com.rosebay.odds.localStorage.VoteDao;
import com.rosebay.odds.model.ImageItem;
import com.rosebay.odds.model.ImageResponse;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.model.Vote;
import com.rosebay.odds.network.ImageClient;
import com.rosebay.odds.ui.createOdds.CreateOddsPresenterImpl;
import com.rosebay.odds.ui.createOdds.CreateOddsView;

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

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateOddsPresenterTest {

    @Mock
    CreateOddsView mockView;
    @Mock
    ImageClient mockImageClient;
    @Mock
    VoteDao mockVoteDao;
    @InjectMocks
    CreateOddsPresenterImpl presenter;
    TestScheduler testScheduler;
    ImageResponse imageResponse;
    SingleOdd testSingleOdd;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testSingleOdd = createTestSingleOdd();
        imageResponse = createImageResponse();
        testScheduler = new TestScheduler();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(h -> Schedulers.trampoline());
    }

    @Test
    public void testNotNull() {
        assertNotNull(presenter);
        assertNotNull(imageResponse);
        assertNotNull(mockImageClient);
        assertNotNull(mockView);
        assertNotNull(testScheduler);
    }

    @Test
    public void testGetImages() {
        when(mockImageClient.fetchImages(anyString(), anyString())).thenReturn(Observable.just(imageResponse));
        presenter.getImages("one", "two");
        testScheduler.triggerActions();
        verify(mockView).onSearch();
        verify(mockView).loadImages(anyList());
    }

    @Test(expected = Exception.class)
    public void testGetImagesError() {
        when(mockImageClient.fetchImages(anyString(), anyString())).thenThrow(new Exception());
        presenter.getImages("one", "two");
        testScheduler.triggerActions();
        verify(mockView).onError();
    }

    @Test
    public void testAddUserVote() {
        when(mockVoteDao.createVoteEntry(any(Vote.class))).thenReturn(1L);
        presenter.addUserVote(testSingleOdd);
        testScheduler.triggerActions();
        verify(mockVoteDao).createVoteEntry(any(Vote.class));
    }

    @After
    public void tearDown() {
        presenter.onViewDetached();
    }

    public ImageResponse createImageResponse() {
        ImageResponse response = new ImageResponse();
        List<ImageItem> imageItems = new ArrayList<>();
        ImageItem item = new ImageItem();
        item.setLink("url");
        imageItems.add(item);
        response.setItems(imageItems);
        return response;
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
