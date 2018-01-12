package com.example.data.repository.datasource;

import com.example.data.cache.FavoritesCache;
import com.example.data.entity.MovieEntity;
import java.io.IOException;
import java.util.List;

/**
 * Created by nguyen.van.mui on 12/01/2018.
 */

public class DiskMovieDataSourceImpl implements DiskMovieDataSource {
    private final FavoritesCache mFavoritesCache;

    public DiskMovieDataSourceImpl(FavoritesCache favoritesCache) {
        mFavoritesCache = favoritesCache;
    }

    @Override
    public void setFavorite(MovieEntity movie) {
        mFavoritesCache.setFavorite(movie);
    }

    @Override
    public boolean isFavorite(String id) {
        return mFavoritesCache.isFavorite(id);
    }

    @Override
    public List<MovieEntity> getFavorites() throws IOException {
        return mFavoritesCache.getFavorites();
    }

    @Override
    public void unFavorite(String id) {
        mFavoritesCache.unFavorite(id);
    }
}