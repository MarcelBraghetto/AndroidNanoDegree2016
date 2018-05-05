package io.github.marcelbraghetto.dailydeviations.features.collection.logic;

import android.os.Bundle;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Marcel Braghetto on 12/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class CollectionArgumentsTest {
    @Rule public ExpectedException mExpectedException = ExpectedException.none();

    @Test
    public void basicInstance() {
        // Setup
        CollectionArguments arguments = new CollectionArguments(CollectionFilterMode.All);

        // Verify
        assertThat(arguments.getFilterMode(), is(CollectionFilterMode.All));
    }

    @Test
    public void instanceFromBundleMissingExtra() {
        // Setup
        mExpectedException.expect(UnsupportedOperationException.class);
        Bundle bundle = new Bundle();

        // Run
        new CollectionArguments(bundle);
    }

    @Test
    public void instanceFromValidBundle() {
        // Setup
        CollectionArguments input = new CollectionArguments(CollectionFilterMode.Favourites);
        Bundle bundle = input.toBundle();

        // Run
        CollectionArguments output = new CollectionArguments(bundle);

        // Verify
        assertThat(output.getFilterMode(), is(CollectionFilterMode.Favourites));
    }
}