package io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode.SingleColumn;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode.Favourites;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 12/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class DefaultCollectionProviderTest {
    @Mock SharedPreferencesProvider mSharedPreferencesProvider;
    private DefaultCollectionProvider mProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mProvider = new DefaultCollectionProvider(mSharedPreferencesProvider);
    }

    @Test
    public void getCollectionDisplayMode() {
        // Setup
        when(mSharedPreferencesProvider.getString(eq("Collection.CollectionDisplayMode"), anyString())).thenReturn("SingleColumn");

        // Run
        CollectionDisplayMode mode = mProvider.getCollectionDisplayMode();

        // Verify
        verify(mSharedPreferencesProvider).getString("Collection.CollectionDisplayMode", "");
        assertThat(mode, is(SingleColumn));
    }

    @Test
    public void setCollectionDisplayMode() {
        // Run
        mProvider.setCollectionDisplayMode(SingleColumn);

        // Verify
        verify(mSharedPreferencesProvider).saveString("Collection.CollectionDisplayMode", "SingleColumn");
    }

    @Test
    public void getCollectionFilterMode() {
        // Setup
        when(mSharedPreferencesProvider.getString(eq("Collection.CollectionFilterMode"), anyString())).thenReturn("Favourites");

        // Run
        CollectionFilterMode mode = mProvider.getCollectionFilterMode();

        // Verify
        verify(mSharedPreferencesProvider).getString("Collection.CollectionFilterMode", "");
        assertThat(mode, is(Favourites));
    }

    @Test
    public void setCollectionFilterMode() {
        // Run
        mProvider.setCollectionFilterMode(Favourites);

        // Verify
        verify(mSharedPreferencesProvider).saveString("Collection.CollectionFilterMode", "Favourites");
    }
}