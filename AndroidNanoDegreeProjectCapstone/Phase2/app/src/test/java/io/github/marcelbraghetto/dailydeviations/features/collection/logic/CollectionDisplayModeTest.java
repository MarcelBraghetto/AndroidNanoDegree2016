package io.github.marcelbraghetto.dailydeviations.features.collection.logic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Marcel Braghetto on 12/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class CollectionDisplayModeTest {
    @Test
    public void fromStringNullInput() {
        // Input
        String input = null;

        // Run
        CollectionDisplayMode mode = CollectionDisplayMode.fromString(input);

        // Verify
        assertThat(mode, is(CollectionDisplayMode.MultiColumn));
    }

    @Test
    public void fromStringNoMatch() {
        // Input
        String input = "SomeRandomString";

        // Run
        CollectionDisplayMode mode = CollectionDisplayMode.fromString(input);

        // Verify
        assertThat(mode, is(CollectionDisplayMode.MultiColumn));
    }

    @Test
    public void fromStringMultiColumn() {
        // Input
        String input = "MultiColumn";

        // Run
        CollectionDisplayMode mode = CollectionDisplayMode.fromString(input);

        // Verify
        assertThat(mode, is(CollectionDisplayMode.MultiColumn));
    }

    @Test
    public void fromStringSingleColumn() {
        // Input
        String input = "SingleColumn";

        // Run
        CollectionDisplayMode mode = CollectionDisplayMode.fromString(input);

        // Verify
        assertThat(mode, is(CollectionDisplayMode.SingleColumn));
    }

}