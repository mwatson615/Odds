package com.rosebay.odds;

import com.rosebay.odds.localStorage.FavoriteDao;
import com.rosebay.odds.localStorage.VoteDao;
import com.rosebay.odds.model.Favorite;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.model.Vote;
import com.rosebay.odds.network.CloudFunctionsClient;
import com.rosebay.odds.network.FirebaseClient;
import com.rosebay.odds.ui.singleOdd.SingleOddPresenterImpl;
import com.rosebay.odds.ui.singleOdd.SingleOddView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SingleOddPresenterTest {

    @Mock
    FirebaseClient mockFirebaseClient;
    @Mock
    CloudFunctionsClient mockCloudClient;
    @Mock
    VoteDao mockVoteDao;
    @Mock
    FavoriteDao mockFavoriteDao;
    @Mock
    SingleOddView mockView;
    @InjectMocks
    SingleOddPresenterImpl presenter = new SingleOddPresenterImpl();
    private TestScheduler scheduler;
    private SingleOdd testSingleOdd;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testSingleOdd = createTestSingleOdd();
        scheduler = new TestScheduler();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(h -> Schedulers.trampoline());
    }

    @Test
    public void testNotNull() {
        assertNotNull(mockCloudClient);
        assertNotNull(mockFirebaseClient);
        assertNotNull(mockVoteDao);
        assertNotNull(presenter);
        assertNotNull(mockView);
    }

    @Test
    public void testGetView() {
        assertEquals(mockView, presenter.getView());
    }

    @Test
    public void testAddToFavorites() {
        when(mockFavoriteDao.createFavorite(any(Favorite.class))).thenReturn(1L);
        presenter.addToFavorites("username", "postId");
        scheduler.triggerActions();
        verify(mockView).onAddedToFavorites();
    }

    @Test(expected = Throwable.class)
    public void testAddToFavoritesError() {
        when(mockFavoriteDao.createFavorite(any(Favorite.class))).thenThrow(new Throwable());
        presenter.addToFavorites("username", "postId");
        scheduler.triggerActions();
        verify(mockView).enableFavoritesButton();
        verify(mockView).onError();
    }

    @Test
    public void testVoteYes() {
        when(mockCloudClient.submitVoteYes(anyMap())).thenReturn(Single.just(testSingleOdd));
        presenter.voteYes("postId");
        scheduler.triggerActions();
        verify(mockView).disableVoteButtons();
        verify(mockView).setOddsAgainst(testSingleOdd.getOddsAgainst());
        verify(mockView).setOddsFor(testSingleOdd.getOddsFor());
        verify(mockView).setPercentage(testSingleOdd.getPercentage());
    }

    @Test(expected = Throwable.class)
    public void testVoteYesError() {
        when(mockCloudClient.submitVoteYes(anyMap())).thenThrow(new Throwable());
        presenter.voteYes("postId");
        scheduler.triggerActions();
        verify(mockView).onError();
        verify(mockView).enableVoteButtons();
    }

    @Test
    public void testVoteNo() {
        when(mockCloudClient.submitNoVote(anyMap())).thenReturn(Single.just(testSingleOdd));
        presenter.voteNo("postId");
        scheduler.triggerActions();
        verify(mockView).disableVoteButtons();
        verify(mockView).setOddsAgainst(testSingleOdd.getOddsAgainst());
        verify(mockView).setOddsFor(testSingleOdd.getOddsFor());
        verify(mockView).setPercentage(testSingleOdd.getPercentage());
    }

    @Test(expected = Throwable.class)
    public void testVoteNoError() {
        when(mockCloudClient.submitNoVote(anyMap())).thenThrow(new Throwable());
        presenter.voteNo("postId");
        scheduler.triggerActions();
        verify(mockView).onError();
        verify(mockView).enableVoteButtons();
    }

    @Test
    public void testAddUserVote() {
        when(mockVoteDao.createVoteEntry(any(Vote.class))).thenReturn(1L);
        presenter.addUserVote("postId", "username", true);
        scheduler.triggerActions();
        verify(mockView).onVoteSuccess();
    }

    @Test(expected = Exception.class)
    public void testAddUserVoteError() {
        when(mockVoteDao.createVoteEntry(any(Vote.class))).thenThrow(new Exception());
        presenter.addUserVote("postId", "username", true);
        scheduler.triggerActions();
        verify(mockView).onError();
    }

    @Test
    public void testCheckForFavoritesYes() {
        when(mockFavoriteDao.findFavByPostID(anyString())).thenReturn(Maybe.empty());
        presenter.checkForFavorite("postId");
        scheduler.triggerActions();
        verify(mockView).enableFavoritesButton();
    }

    @Test
    public void testCheckForFavoritesNo() {
        when(mockFavoriteDao.findFavByPostID(anyString())).thenReturn(Maybe.just("postId"));
        presenter.checkForFavorite("postId");
        scheduler.triggerActions();
        verify(mockView).disableFavoritesButton();
    }

    @Test(expected = Exception.class)
    public void testCheckForFavoritesError() {
        when(mockFavoriteDao.findFavByPostID(anyString())).thenThrow(new Exception());
        presenter.checkForFavorite("postId");
        scheduler.triggerActions();
        verify(mockView).onError();
    }

    @Test
    public void testCheckIfVotedNo() {
        when(mockVoteDao.findVoteByPostID(anyString())).thenReturn(Maybe.empty());
        presenter.checkIfVoted("postId");
        scheduler.triggerActions();
        verify(mockView).enableVoteButtons();
    }

    @Test
    public void testCheckIfVotedYes() {
        when(mockVoteDao.findVoteByPostID(anyString())).thenReturn(Maybe.just("userId"));
        presenter.checkIfVoted("postId");
        scheduler.triggerActions();
        verify(mockView).disableVoteButtons();
    }

    @Test(expected = Exception.class)
    public void testCheckIfVotedError() {
        when(mockVoteDao.findVoteByPostID(anyString())).thenThrow(new Exception());
        presenter.checkIfVoted("postId");
        scheduler.triggerActions();
        verify(mockView).onError();
    }

    @Test
    public void testLoadOddsData() {
        presenter.loadOddsData(testSingleOdd);
        scheduler.triggerActions();
        verify(mockView).setPercentage(testSingleOdd.getPercentage());
        verify(mockView).setOddsFor(testSingleOdd.getOddsFor());
        verify(mockView).setOddsAgainst(testSingleOdd.getOddsAgainst());
        verify(mockView).setDescription(testSingleOdd.getDescription());
        verify(mockView).setDueDate(testSingleOdd.getDueDate());
        verify(mockView).setImageUrl(testSingleOdd.getImageUrl());
    }

    @Test
    public void testLoadNumbersData() {
        presenter.loadNumbersData(testSingleOdd);
        scheduler.triggerActions();
        verify(mockView).setPercentage(testSingleOdd.getPercentage());
        verify(mockView).setOddsFor(testSingleOdd.getOddsFor());
        verify(mockView).setOddsAgainst(testSingleOdd.getOddsAgainst());
    }

    @After
    public void tearDown() {
        presenter.onViewDetached();
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
