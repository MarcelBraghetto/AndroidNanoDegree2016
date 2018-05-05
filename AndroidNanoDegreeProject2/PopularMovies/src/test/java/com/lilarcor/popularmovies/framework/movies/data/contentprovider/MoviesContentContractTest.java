package com.lilarcor.popularmovies.framework.movies.data.contentprovider;

import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.testutils.RobolectricProperties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.*;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_AUTHORITY;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_ALL_FAVOURITE_MOVIES;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_MOVIES;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_POPULAR_MOVIES;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_TOP_RATED_MOVIES;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.Movies;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Marcel Braghetto on 26/07/15.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class MoviesContentContractTest {
    @Test
    public void testContentAuthority() {
        // Verify
        assertThat(CONTENT_AUTHORITY, is("com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider"));
    }

    @Test
    public void testContentPathMovies() {
        // Verify
        assertThat(CONTENT_PATH_MOVIES, is("movies"));
    }

    @Test
    public void testContentPathAllFavouriteMovies() {
        // Verify
        assertThat(CONTENT_PATH_ALL_FAVOURITE_MOVIES, is("movies/all_favourites"));
    }

    @Test
    public void testContentPathPopularMovies() {
        // Verify
        assertThat(CONTENT_PATH_POPULAR_MOVIES, is("popular_movies"));
    }

    @Test
    public void testContentPathTopRatedMovies() {
        // Verify
        assertThat(CONTENT_PATH_TOP_RATED_MOVIES, is("top_rated_movies"));
    }

    @Test
    public void testMoviesTable() {
        // Verify
        assertThat(Movies.TABLE_NAME, is("movies"));
        assertThat(Movies.COLUMN_MOVIE_TITLE, is("movie_title"));
        assertThat(Movies.COLUMN_MOVIE_OVERVIEW, is("movie_overview"));
        assertThat(Movies.COLUMN_MOVIE_BACKDROP_PATH, is("movie_backdrop_path"));
        assertThat(Movies.COLUMN_MOVIE_POSTER_PATH, is("movie_poster_path"));
        assertThat(Movies.COLUMN_MOVIE_RELEASE_DATE, is("movie_release_date"));
        assertThat(Movies.COLUMN_MOVIE_VOTE_AVERAGE, is("movie_vote_average"));
        assertThat(Movies.COLUMN_MOVIE_VOTE_COUNT, is("movie_vote_count"));
        assertThat(Movies.COLUMN_MOVIE_IS_FAVOURITE, is("is_favourite"));

        assertThat(Movies.getCreateTableSql(), is("CREATE TABLE movies (_id INTEGER PRIMARY KEY, movie_title TEXT NOT NULL, movie_overview TEXT NOT NULL, movie_backdrop_path TEXT NOT NULL, movie_poster_path TEXT NOT NULL, movie_release_date TEXT NOT NULL, movie_vote_average REAL NOT NULL, movie_vote_count INTEGER NOT NULL, is_favourite INTEGER NOT NULL DEFAULT 0);"));
        assertThat(Movies.getMovieExistsSql(), is("SELECT COUNT(_id) FROM movies WHERE _id = ?"));
        assertThat(Movies.getInsertSql(), is("INSERT INTO movies(_id, movie_title, movie_overview, movie_release_date, movie_poster_path, movie_backdrop_path, movie_vote_average, movie_vote_count, is_favourite) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)"));
        assertThat(Movies.getUpdateSql(), is("UPDATE movies SET movie_title= ?, movie_overview= ?, movie_release_date= ?, movie_poster_path= ?, movie_backdrop_path= ?, movie_vote_average= ?, movie_vote_count= ? WHERE _id = ?"));

        assertThat(Movies.getContentUriAllMovies().toString(), is("content://com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/movies"));
        assertThat(Movies.getContentUriAllFavouriteMovies().toString(), is("content://com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/movies/all_favourites"));
        assertThat(Movies.getContentUriSpecificMovie(11).toString(), is("content://com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/movies/11"));
        assertThat(Movies.getContentItemType(), is("vnd.android.cursor.item/com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/movies"));
        assertThat(Movies.getContentType(), is("vnd.android.cursor.dir/com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/movies"));

        Map<String, Integer> columnMap = Movies.getAllColumnsIndexMap();
        assertThat(columnMap.size(), is(9));
        assertThat(columnMap.get(Movies._ID), is(0));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_TITLE), is(1));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_OVERVIEW), is(2));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_BACKDROP_PATH), is(3));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_POSTER_PATH), is(4));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_RELEASE_DATE), is(5));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_VOTE_AVERAGE), is(6));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_VOTE_COUNT), is(7));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_IS_FAVOURITE), is(8));
    }

    @Test
    public void testPopularMoviesTable() {
        // Verify
        assertThat(PopularMovies.TABLE_NAME, is("popular_movies"));
        assertThat(PopularMovies.COLUMN_MOVIE_ID, is("movie_id"));
        assertThat(PopularMovies.COLUMN_RESULT_PAGE, is("result_page"));

        assertThat(PopularMovies.getCreateTableSql(), is("CREATE TABLE popular_movies (_id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER NOT NULL, result_page INTEGER NOT NULL, UNIQUE (movie_id) ON CONFLICT REPLACE)"));
        assertThat(PopularMovies.getInsertSql(), is("INSERT INTO popular_movies(movie_id, result_page) VALUES(?, ?)"));

        assertThat(PopularMovies.getContentUriAllPopularMovies().toString(), is("content://com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/popular_movies"));
        assertThat(PopularMovies.getContentType(), is("vnd.android.cursor.dir/com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/popular_movies"));

        Map<String, Integer> columnMap = PopularMovies.getAllColumnsIndexMap();
        assertThat(columnMap.size(), is(11));
        assertThat(columnMap.get(PopularMovies.COLUMN_MOVIE_ID), is(1));
        assertThat(columnMap.get(PopularMovies.COLUMN_RESULT_PAGE), is(2));
        assertThat(columnMap.get(Movies._ID), is(3));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_TITLE), is(4));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_OVERVIEW), is(5));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_BACKDROP_PATH), is(6));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_POSTER_PATH), is(7));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_RELEASE_DATE), is(8));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_VOTE_AVERAGE), is(9));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_VOTE_COUNT), is(10));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_IS_FAVOURITE), is(11));
    }

    @Test
    public void testTopRatedMoviesTable() {
        // Verify
        assertThat(TopRatedMovies.TABLE_NAME, is("top_rated_movies"));
        assertThat(TopRatedMovies.COLUMN_MOVIE_ID, is("movie_id"));
        assertThat(TopRatedMovies.COLUMN_RESULT_PAGE, is("result_page"));

        assertThat(TopRatedMovies.getCreateTableSql(), is("CREATE TABLE top_rated_movies (_id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER NOT NULL, result_page INTEGER NOT NULL, UNIQUE (movie_id) ON CONFLICT REPLACE)"));
        assertThat(TopRatedMovies.getInsertSql(), is("INSERT INTO top_rated_movies(movie_id, result_page) VALUES(?, ?)"));

        assertThat(TopRatedMovies.getContentUriAllTopRatedMovies().toString(), is("content://com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/top_rated_movies"));
        assertThat(TopRatedMovies.getContentType(), is("vnd.android.cursor.dir/com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/top_rated_movies"));

        Map<String, Integer> columnMap = TopRatedMovies.getAllColumnsIndexMap();
        assertThat(columnMap.size(), is(11));
        assertThat(columnMap.get(PopularMovies.COLUMN_MOVIE_ID), is(1));
        assertThat(columnMap.get(PopularMovies.COLUMN_RESULT_PAGE), is(2));
        assertThat(columnMap.get(Movies._ID), is(3));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_TITLE), is(4));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_OVERVIEW), is(5));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_BACKDROP_PATH), is(6));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_POSTER_PATH), is(7));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_RELEASE_DATE), is(8));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_VOTE_AVERAGE), is(9));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_VOTE_COUNT), is(10));
        assertThat(columnMap.get(Movies.COLUMN_MOVIE_IS_FAVOURITE), is(11));
    }
}