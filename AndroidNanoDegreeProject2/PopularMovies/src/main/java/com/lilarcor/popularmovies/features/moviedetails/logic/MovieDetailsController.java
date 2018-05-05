package com.lilarcor.popularmovies.features.moviedetails.logic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.moviereviews.logic.MovieReviewsController;
import com.lilarcor.popularmovies.features.moviereviews.ui.MovieReviewsActivity;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.core.weakwrapper.WeakWrapper;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.network.models.MovieReviewsDTO;
import com.lilarcor.popularmovies.framework.foundation.network.models.MovieVideosDTO;
import com.lilarcor.popularmovies.framework.foundation.threadutils.contracts.ThreadUtilsProvider;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.framework.movies.models.MovieReview;
import com.lilarcor.popularmovies.framework.movies.models.MovieVideo;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Marcel Braghetto on 19/07/15.
 *
 * Controller logic for parsing and rendering a movie
 * and its details (if possible).
 */
public class MovieDetailsController {
    //region Public fields
    public static final String ARG_MOVIE_ID = "MDCMI";
    //endregion

    //region Private fields
    private final Context mApplicationContext;
    private final MoviesProvider mMoviesProvider;
    private final AppStringsProvider mAppStringsProvider;
    private final NetworkRequestProvider mNetworkRequestProvider;
    private final ThreadUtilsProvider mThreadUtilsProvider;

    private ControllerDelegate mDelegate;
    private Movie mMovie;
    private int mMovieId;
    private MovieVideo mTrailerVideo;
    //endregion

    //region Public methods
    @Inject
    public MovieDetailsController(@NonNull Context applicationContext,
                                  @NonNull MoviesProvider moviesProvider,
                                  @NonNull AppStringsProvider appStringsProvider,
                                  @NonNull NetworkRequestProvider networkRequestProvider,
                                  @NonNull ThreadUtilsProvider threadUtilsProvider) {

        mDelegate = WeakWrapper.wrapEmpty(ControllerDelegate.class);

        mApplicationContext = applicationContext;
        mMoviesProvider = moviesProvider;
        mAppStringsProvider = appStringsProvider;
        mNetworkRequestProvider = networkRequestProvider;
        mThreadUtilsProvider = threadUtilsProvider;
    }

    /**
     * Initialise and connect the controller to the host environment. Also expect to
     * receive a bundle of arguments to indicate what 'filter' to apply.
     *
     * @param delegate to send commands to.
     * @param arguments to parse for configuration.
     */
    public void initController(@Nullable ControllerDelegate delegate, @Nullable Bundle arguments) {
        mDelegate = WeakWrapper.wrap(delegate, ControllerDelegate.class);
        initScreen(arguments);
    }

    /**
     * If the UI reports that the header image was not able to
     * load, show an indicator to reflect that.
     */
    public void headerImageFailedToLoad() {
        mDelegate.hideHeaderImageProgressIndicator();
        mDelegate.showHeaderImageFailedIndicator();
    }

    /**
     * User selects the toggle favourite button, to
     * add or remove this movie to their favourite
     * movies collection.
     */
    public void toggleFavouriteSelected() {
        String wordFavourites = mAppStringsProvider.getString(R.string.word_favourites);

        if(mMovie.isFavourite()) {
            mMovie.setIsFavourite(false);
            mMoviesProvider.removeMovieFromFavourites(mMovieId);
            mDelegate.showSnackbar(mAppStringsProvider.getString(R.string.favourite_removed_message, mMovie.getTitle(), wordFavourites));
        } else {
            mMovie.setIsFavourite(true);
            mMoviesProvider.addMovieToFavourites(mMovieId);
            mDelegate.showSnackbar(mAppStringsProvider.getString(R.string.favourite_added_message, mMovie.getTitle(), wordFavourites));
        }

        mDelegate.refreshFloatingActionButton(mMovie.isFavourite());
    }

    /**
     * User selects the play button in the video header section of
     * the screen and should trigger the video url to open.
     */
    public void videoHeaderButtonSelected() {
        if(mTrailerVideo != null) {
            startWatchVideoIntent(mTrailerVideo.getYouTubeVideoUrl());
        }
    }

    /**
     * User selected a specific video trailer play button in the
     * list of video trailers. The video should open for playing.
     *
     * @param videoUrl of the selected video trailer.
     */
    public void videoTrailerPlayButtonSelected(@NonNull String videoUrl) {
        startWatchVideoIntent(videoUrl);
    }

    /**
     * User selected a specific video trailer share button in the
     * list of video trailers. The video should be shared using
     * whatever methods of sharing available on the device.
     *
     * @param videoTitle for the selected video trailer.
     * @param videoUrl for the selected video trailer.
     */
    public void videoTrailerShareButtonSelected(@NonNull String videoTitle, @NonNull String videoUrl) {
        startShareVideoIntent(videoTitle, videoUrl);
    }

    public void reviewsMoreButtonSelected() {
        Intent intent = new Intent(mApplicationContext, MovieReviewsActivity.class);
        intent.putExtra(MovieReviewsController.ARG_MOVIE_ID, mMovie.getMovieId());
        mDelegate.startActivityIntent(intent);
    }

    /**
     * Called when the host component is about to be destroyed.
     */
    public void disconnect() {
        mDelegate = WeakWrapper.wrapEmpty(ControllerDelegate.class);
    }
    //endregion

    //region Private methods
    /**
     * A bundle should have been passed in via the fragments arguments and should
     * contain the relevant key/value pair(s).
     *
     * @param arguments containing the properties of this controller instance.
     */
    private void initScreen(Bundle arguments) {
        // If the bundle arguments were null or don't contain the filter key, then the developer did something dumb, so punish them!
        if(arguments == null || !arguments.containsKey(ARG_MOVIE_ID)) {
            throw new IllegalArgumentException("Invalid use of MovieDetailsController - you MUST pass a valid movie id in via argument bundle.");
        }

        mMovieId = arguments.getInt(ARG_MOVIE_ID);
        mMovie = mMoviesProvider.getMovieWithId(mMovieId);

        // Movie couldn't be found in the database - this is
        // extremely unlikely be programmatically possible.
        if(mMovie == null) {
            mDelegate.finishActivity();
            return;
        }

        mDelegate.hideHeaderVideoPlayButton();
        mDelegate.hideHeaderImageFailedIndicator();
        mDelegate.showHeaderImageProgressIndicator();
        mDelegate.setActivityTitle(mMovie.getTitle());
        mDelegate.hideVideos();
        mDelegate.hideAllReviews();
        mDelegate.hideMoreReviewsButton();
        mDelegate.refreshFloatingActionButton(mMovie.isFavourite());

        if(TextUtils.isEmpty(mMovie.getBackdropImageUrl())) {
            headerImageFailedToLoad();
        } else {
            mDelegate.loadHeaderImage(mMovie.getBackdropImageUrl());
        }

        int voteAverage = Math.round((mMovie.getVoteAverage() / 10f) * 100f);
        mDelegate.setVoteAverageText(mAppStringsProvider.getString(R.string.movie_details_vote_average, voteAverage));
        mDelegate.setVoteTotalText(mAppStringsProvider.getString(R.string.movie_details_vote_total, mMovie.getVoteCount()));

        StringBuilder sb = new StringBuilder();

        String releaseDateText;

        try {
            Date releaseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(mMovie.getReleaseDate());
            releaseDateText = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.ENGLISH).format(releaseDate);
        } catch (ParseException e) {
            releaseDateText = null;
        }

        if(releaseDateText != null) {
            sb.append(mAppStringsProvider.getString(R.string.movie_details_release_date, mMovie.getTitle(), releaseDateText));
            sb.append("\n\n");
        }

        sb.append(mMovie.getOverview());

        mDelegate.setAboutText(sb.toString());

        startVideosRequest();
        startReviewsRequest();
    }

    private void startWatchVideoIntent(String videoUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(videoUrl));
        mDelegate.startActivityIntent(intent);
    }

    private void startShareVideoIntent(String videoTitle, String videoUrl) {
        String shareTitle = mAppStringsProvider.getString(R.string.movie_details_share_title, mMovie.getTitle(), videoTitle);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
        intent.putExtra(Intent.EXTRA_TEXT, videoUrl);
        intent.setType("text/plain");
        mDelegate.startActivityIntent(intent);
    }

    private void startVideosRequest() {
        mNetworkRequestProvider.getMovieVideos(mMovieId, new Callback<MovieVideosDTO>() {
            @Override
            public void success(MovieVideosDTO movieVideoFeedDTO, Response response) {
                MovieVideo[] videos = movieVideoFeedDTO.getVideos();

                if (videos == null) {
                    return;
                }

                // We only care about movies that are hosted on YouTube
                List<MovieVideo> youTubeMovies = new ArrayList<>();

                for (MovieVideo video : videos) {
                    if ("YOUTUBE".equals(video.getVideoSite().toUpperCase(Locale.ENGLISH))) {
                        youTubeMovies.add(video);
                    }
                }

                mTrailerVideo = findTrailerVideo(youTubeMovies);

                mMoviesProvider.deleteMovieVideos(mMovieId);
                mMoviesProvider.saveMovieVideos(mMovieId, youTubeMovies);

                mThreadUtilsProvider.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mDelegate.showHeaderVideoPlayButton();
                        mDelegate.showAndPopulateVideos(mMoviesProvider.getMovieVideosUri(mMovieId));
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private MovieVideo findTrailerVideo(List<MovieVideo> videos) {
        if(videos.size() == 0) {
            return null;
        }

        MovieVideo result = videos.get(0);

        // If we find a movie whose type is 'trailer' then choose that one
        // in preference to any other.
        for(MovieVideo video : videos) {
            if ("TRAILER".equals(video.getVideoType().toUpperCase(Locale.ENGLISH))) {
                return video;
            }
        }

        // If we didn't find a 'trailer', then just return whatever movie
        // was first in the collection.
        return result;
    }

    private void startReviewsRequest() {
        mNetworkRequestProvider.getMovieReviews(mMovieId, new Callback<MovieReviewsDTO>() {
            @Override
            public void success(MovieReviewsDTO movieReviewsDTO, Response response) {
                final MovieReview[] reviews = movieReviewsDTO.getReviews();

                if (reviews == null) {
                    return;
                }

                mMoviesProvider.deleteMovieReviews(mMovieId);
                mMoviesProvider.saveMovieReviews(mMovieId, Arrays.asList(reviews));

                mThreadUtilsProvider.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {

                        // We only show up to 3 reviews in this screen and if
                        // there are more than 3 reviews, also provide a 'show
                        // all reviews' button so the user can open a different
                        // activity with all reviews in it.

                        if(reviews.length >= 1) {
                            mDelegate.showReview1(reviews[0].getAuthorName(), reviews[0].getContent());
                        }

                        if(reviews.length >= 2) {
                            mDelegate.showReview2(reviews[1].getAuthorName(), reviews[1].getContent());
                        }

                        if(reviews.length >= 3) {
                            mDelegate.showReview3(reviews[2].getAuthorName(), reviews[2].getContent());
                        }

                        if(reviews.length >= 4) {
                            mDelegate.showMoreReviewsButton();
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) { }
        });
    }
    //endregion

    //region Controller delegate contract
    public interface ControllerDelegate {
        /**
         * Assign the given title to the activity.
         *
         * @param title to display.
         */
        void setActivityTitle(@NonNull String title);

        /**
         * Initiate the loading of the large 'header'
         * image for the movie.
         *
         * @param imageUrl to load and display.
         */
        void loadHeaderImage(@NonNull String imageUrl);

        /**
         * Show the progress indicator while the header
         * image is loading.
         */
        void showHeaderImageProgressIndicator();

        /**
         * Hide the progress indicator for the header
         * image.
         */
        void hideHeaderImageProgressIndicator();

        /**
         * If there was a problem while loading the
         * header image, show the failure indicator.
         */
        void showHeaderImageFailedIndicator();

        /**
         * Hide the header image failure indicator.
         */
        void hideHeaderImageFailedIndicator();

        /**
         * Exit out of the activity.
         */
        void finishActivity();

        /**
         * Display a Snackbar to the user with the given text.
         *
         * @param text to display in the Snackbar.
         */
        void showSnackbar(@NonNull String text);

        /**
         * If the movie is a favourite, the floating action button
         * should show an icon to reflect this.
         *
         * @param isMovieFavourite if the movie is a user favourite.
         */
        void refreshFloatingActionButton(boolean isMovieFavourite);

        /**
         * Display the given vote average text.
         *
         * @param text to display.
         */
        void setVoteAverageText(@NonNull String text);

        /**
         * Display the given vote total text.
         *
         * @param text to display.
         */
        void setVoteTotalText(@NonNull String text);

        /**
         * Display the given about text.
         *
         * @param text to display.
         */
        void setAboutText(@NonNull String text);

        /**
         * Hide the video play button.
         */
        void hideHeaderVideoPlayButton();

        /**
         * Display the video play button to start
         * the trailer video for the movie.
         */
        void showHeaderVideoPlayButton();

        /**
         * Initiate an intent from the host activity.
         */
        void startActivityIntent(@NonNull Intent intent);

        /**
         * Hide the video trailers layout.
         */
        void hideVideos();

        /**
         * Show the video trailers layout.
         */
        void showAndPopulateVideos(@NonNull final Uri dataSourceUri);

        /**
         * Hide all the review layouts and their parent
         * container layout.
         */
        void hideAllReviews();

        /**
         * Show and populate the first user review.
         *
         * @param author text to display.
         * @param content text to display.
         */
        void showReview1(@NonNull String author, @NonNull String content);

        /**
         * Show and populate the second user review.
         *
         * @param author text to display.
         * @param content text to display.
         */
        void showReview2(@NonNull String author, @NonNull String content);

        /**
         * Show and populate the third user review.
         *
         * @param author text to display.
         * @param content text to display.
         */
        void showReview3(@NonNull String author, @NonNull String content);

        /**
         * Hide the button that would open all the user
         * reviews in another activity.
         */
        void hideMoreReviewsButton();

        /**
         * Show the button to allow the user to open
         * all the user reviews in a new activity.
         */
        void showMoreReviewsButton();
    }
    //endregion
}
