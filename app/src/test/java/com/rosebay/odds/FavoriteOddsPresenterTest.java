package com.rosebay.odds;

import com.rosebay.odds.localStorage.FavoriteDao;
import com.rosebay.odds.model.Favorite;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.network.FirebaseClient;
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsPresenterImpl;
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteOddsPresenterTest {

    @Mock
    FavoriteDao mockFavoriteDao;
    @Mock
    FirebaseClient mockFirebaseClient;
    @Mock
    FavoriteOddsView mockView;
    @InjectMocks
    FavoriteOddsPresenterImpl presenter;
    private List<Favorite> testList;
    private SingleOdd testSingleOdd;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testList = createTestFavList();
        testSingleOdd = createTestSingleOdd();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(h -> Schedulers.trampoline());
        when(mockFavoriteDao.getUserFavorites()).thenReturn(testList);
    }

    @Test
    public void testGetView() {
        assertEquals(mockView, presenter.getView());
    }

    @Test
    public void testGetAllFavoritesYes() {
        when(mockFirebaseClient.fetchSingleOdd(testList.get(0).getPostId())).thenReturn(Single.just(testSingleOdd));
        presenter.getAllFavorites();
        verify(mockView).setData(anyList());
    }

    @Test
    public void testGetAllFavoritesNull() {
        when(mockFavoriteDao.getUserFavorites()).thenReturn(Collections.emptyList());
        presenter.getAllFavorites();
        verify(mockView).noFavorites();
    }

    @Test
    public void testFetchFromFirebase() {
        when(mockFirebaseClient.fetchSingleOdd(anyString())).thenReturn(Single.just(testSingleOdd));
        presenter.getAllFavorites();
        verify(mockView).setData(anyList());
    }

    @Test(expected = Exception.class)
    public void testFetchFromFirebaseError() {
        when(mockFirebaseClient.fetchSingleOdd(anyString())).thenThrow(new Exception());
        presenter.getAllFavorites();
        verify(mockView).onError();
    }

    @After
    public void tearDown() {
        presenter.onViewDetached();
    }

    public List<Favorite> createTestFavList() {
        Favorite favorite = new Favorite();
        favorite.setPostId("postId");
        favorite.setUsername("username");
        favorite.setUid(1);
        List<Favorite> favoriteList = new ArrayList<>();
        favoriteList.add(favorite);
        return favoriteList;
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
        singleOdd.setPostId("postId");
        singleOdd.setUsername("username");
        return singleOdd;
    }

}
