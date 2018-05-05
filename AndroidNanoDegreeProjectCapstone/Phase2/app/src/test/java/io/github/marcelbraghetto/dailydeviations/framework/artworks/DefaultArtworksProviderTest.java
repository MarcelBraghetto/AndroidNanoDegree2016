package io.github.marcelbraghetto.dailydeviations.framework.artworks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.service.ArtworksDataService;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 15/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class DefaultArtworksProviderTest {
    @Mock Context mContext;
    @Mock ContentResolver mContentResolver;
    @Mock SharedPreferencesProvider mSharedPreferencesProvider;
    @Mock Cursor mCursor;
    private DefaultArtworksProvider mProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mContext.getContentResolver()).thenReturn(mContentResolver);

        when(mContentResolver
                .query(any(Uri.class),
                        any(String[].class),
                        any(String.class),
                        any(String[].class),
                        any(String.class)))
                .thenReturn(mCursor);

        mProvider = new DefaultArtworksProvider(mContext, mSharedPreferencesProvider);
    }

    @Test
    public void hasSavedArtworksNoData() {
        // Setup
        when(mCursor.moveToFirst()).thenReturn(false);

        // Run
        boolean result = mProvider.hasSavedArtworks();

        // Verify
        verify(mContext).getContentResolver();
        verify(mCursor).moveToFirst();
        assertThat(result, is(false));
    }

    @Test
    public void hasSavedArtworksCountZero() {
        // Setup
        when(mCursor.moveToFirst()).thenReturn(true);
        when(mCursor.getInt(0)).thenReturn(0);

        // Run
        boolean result = mProvider.hasSavedArtworks();

        // Verify
        verify(mContext).getContentResolver();
        verify(mCursor).moveToFirst();
        verify(mCursor).getInt(0);
        assertThat(result, is(false));
    }

    @Test
    public void hasSavedArtworksCountPositive() {
        // Setup
        when(mCursor.moveToFirst()).thenReturn(true);
        when(mCursor.getInt(0)).thenReturn(10);

        // Run
        boolean result = mProvider.hasSavedArtworks();

        // Verify
        verify(mContext).getContentResolver();
        verify(mCursor).moveToFirst();
        verify(mCursor).getInt(0);
        assertThat(result, is(true));
    }

    @Test
    public void getArtworksCategoryFavourites() {
        // Run
        Uri uri = mProvider.getArtworks(CollectionFilterMode.Favourites);

        // Verify
        assertThat(uri.toString(), is("content://io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksContentProvider/artworks_favourites"));
    }

    @Test
    public void getArtworksCategoryAll() {
        // Run
        Uri uri = mProvider.getArtworks(CollectionFilterMode.All);

        // Verify
        assertThat(uri.toString(), is("content://io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksContentProvider/artworks"));
    }

    @Test
    public void saveArtworks() {
        // Setup
        ArgumentCaptor<Uri> uriCaptor = ArgumentCaptor.forClass(Uri.class);
        ArgumentCaptor<ContentValues[]> contentValuesCaptor = ArgumentCaptor.forClass(ContentValues[].class);

        List<Artwork> artworks = new ArrayList<>();
        artworks.add(new Artwork());
        artworks.add(new Artwork());

        when(mContentResolver.bulkInsert(any(Uri.class), any(ContentValues[].class))).thenReturn(artworks.size());
        when(mSharedPreferencesProvider.getInteger("ArtworksCommonProperties.UnseenArtworksCount", 0)).thenReturn(1);

        // Run
        int result = mProvider.saveArtworks(artworks);

        // Verify
        verify(mContext).getContentResolver();
        verify(mContentResolver).bulkInsert(uriCaptor.capture(), contentValuesCaptor.capture());
        assertThat(uriCaptor.getValue().toString(), is("content://io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksContentProvider/artworks"));
        assertThat(contentValuesCaptor.getValue().length, is(2));
        verify(mSharedPreferencesProvider).saveInteger("ArtworksCommonProperties.UnseenArtworksCount", 3);
        assertThat(result, is(2));
    }

    @Test
    public void resetNumUnseenArtworks() {
        // Run
        mProvider.resetNumUnseenArtworks();

        // Verify
        verify(mSharedPreferencesProvider).saveInteger("ArtworksCommonProperties.UnseenArtworksCount", 0);
    }

    @Test
    public void getNumUnseenArtworks() {
        // Run
        mProvider.getNumUnseenArtworks();

        // Verify
        verify(mSharedPreferencesProvider).getInteger("ArtworksCommonProperties.UnseenArtworksCount", 0);
    }

    @Test
    public void refreshData() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);

        // Run
        mProvider.refreshData();

        // Verify
        verify(mContext).startService(intentCaptor.capture());
        Intent intent = intentCaptor.getValue();
        assertThat(intent.getComponent().getClassName(), is("io.github.marcelbraghetto.dailydeviations.framework.artworks.service.ArtworksDataService"));
        ArtworksDataService.RefreshReason reason = (ArtworksDataService.RefreshReason)intent.getSerializableExtra("ArtworksDataService.Reason");
        assertThat(reason, is(ArtworksDataService.RefreshReason.ForceRefresh));
    }

    @Test
    public void getArtworkNotFound() {
        // Setup
        when(mCursor.moveToFirst()).thenReturn(false);

        // Run
        Artwork result = mProvider.getArtwork("guid");

        // Verify
        verify(mContext).getContentResolver();
        verify(mCursor).moveToFirst();
        assertThat(result, nullValue());
    }

    @Test
    public void getArtworkFound() {
        // Setup
        when(mCursor.moveToFirst()).thenReturn(true);

        // Run
        Artwork result = mProvider.getArtwork("guid");

        // Verify
        verify(mContext).getContentResolver();
        verify(mCursor).moveToFirst();
        verify(mCursor).close();

        assertThat(result, notNullValue());
    }
}