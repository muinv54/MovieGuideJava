package com.esoxjem.movieguide.details;

import android.content.Context;
import com.esoxjem.movieguide.MovieModel;
import com.esoxjem.movieguide.Review;
import com.esoxjem.movieguide.Video;
import com.esoxjem.movieguide.mapper.MovieModelDataMapper;
import com.esoxjem.movieguide.util.RxUtils;
import com.example.data.cache.FavoritesCacheImpl;
import com.example.data.entity.mapper.MovieEntityDataMapper;
import com.example.data.repository.MovieDataRepository;
import com.example.data.repository.datasource.MovieDataSourceFactory;
import com.example.domain.interactor.FavoriteUseCase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

/**
 * @author arun
 */
class MovieDetailsPresenterImpl implements MovieDetailsPresenter {
    private MovieDetailsView view;
    private MovieDetailsInteractor movieDetailsInteractor;
    private FavoriteUseCase mFavoriteUseCase;
    private Disposable trailersSubscription;
    private Disposable reviewSubscription;
    private MovieModelDataMapper mMovieModelDataMapper;

    MovieDetailsPresenterImpl(MovieDetailsInteractor movieDetailsInteractor, Context context) {
        this.movieDetailsInteractor = movieDetailsInteractor;
        this.mFavoriteUseCase = new FavoriteUseCase(
                new MovieDataRepository(new MovieDataSourceFactory(new FavoritesCacheImpl(context)),
                        new MovieEntityDataMapper()));
        mMovieModelDataMapper = new MovieModelDataMapper();
    }

    @Override
    public void setView(MovieDetailsView view) {
        this.view = view;
    }

    @Override
    public void destroy() {
        view = null;
        RxUtils.unsubscribe(trailersSubscription, reviewSubscription);
    }

    @Override
    public void showDetails(MovieModel movie) {
        if (isViewAttached()) {
            view.showDetails(movie);
        }
    }

    private boolean isViewAttached() {
        return view != null;
    }

    @Override
    public void showTrailers(MovieModel movie) {
        trailersSubscription = movieDetailsInteractor.getTrailers(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGetTrailersSuccess, t -> onGetTrailersFailure());
    }

    private void onGetTrailersSuccess(List<Video> videos) {
        if (isViewAttached()) {
            view.showTrailers(videos);
        }
    }

    private void onGetTrailersFailure() {
        // Do nothing
    }

    @Override
    public void showReviews(MovieModel movie) {
        reviewSubscription = movieDetailsInteractor.getReviews(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGetReviewsSuccess, t -> onGetReviewsFailure());
    }

    private void onGetReviewsSuccess(List<Review> reviews) {
        if (isViewAttached()) {
            view.showReviews(reviews);
        }
    }

    private void onGetReviewsFailure() {
        // Do nothing
    }

    @Override
    public void showFavoriteButton(MovieModel movie) {
        boolean isFavorite = mFavoriteUseCase.isFavorite(movie.getId());
        if (isViewAttached()) {
            if (isFavorite) {
                view.showFavorited();
            } else {
                view.showUnFavorited();
            }
        }
    }

    @Override
    public void onFavoriteClick(MovieModel movie) {
        if (isViewAttached()) {
            boolean isFavorite = mFavoriteUseCase.isFavorite(movie.getId());
            if (isFavorite) {
                mFavoriteUseCase.unFavorite(movie.getId());
                view.showUnFavorited();
            } else {
                mFavoriteUseCase.setFavorite(mMovieModelDataMapper.transformModel(movie));
                view.showFavorited();
            }
        }
    }
}
