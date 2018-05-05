package io.github.marcelbraghetto.dailydeviations.features.info.logic;

import android.os.Bundle;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.dates.contracts.DateProvider;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 14/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class InfoViewModelTest {
    @Mock DateProvider mDateProvider;
    @Mock AnalyticsProvider mAnalyticsProvider;
    @Mock InfoViewModel.Actions mDelegate;
    private InfoViewModel mViewModel;
    private Artwork mArtwork;
    private Bundle mBundle;

    @Rule public ExpectedException mExpectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mArtwork = new Artwork();
        mArtwork.setTitle("title");
        mArtwork.setAuthorImageUrl("author image url");
        mArtwork.setPublishDate("01/06/2016");
        mArtwork.setAuthor("author");
        mArtwork.setDescription("description");
        mArtwork.setCopyright("copyright");

        mBundle = new Bundle();
        mArtwork.putInto(mBundle);

        mViewModel = new InfoViewModel(
                MainApp.getDagger().getAppStringsProvider(),
                mDateProvider,
                mAnalyticsProvider);
    }

    @Test
    public void beginBundleMissingArtwork() {
        // Setup
        mExpectedException.expect(UnsupportedOperationException.class);

        // Run
        mViewModel.begin(new Bundle(), mDelegate);
    }

    @Test
    public void beginMalformedPublishDate() {
        // Setup
        when(mDateProvider.parseDateTimeString(anyString(), anyString())).thenReturn(null);

        // Run
        mViewModel.begin(mBundle, mDelegate);

        // Verify
        verify(mAnalyticsProvider).trackScreenView("AuthorInfoScreen");
        assertThat(mViewModel.glue.authorImageUrl.get(), is("author image url"));
        assertThat(mViewModel.glue.titleText.get(), is("title"));
        assertThat(mViewModel.glue.authorText.get(), is("By author\\n"));
        verify(mDateProvider).parseDateTimeString(Artwork.PUBLISH_DATE_FORMAT, "01/06/2016");
        verify(mDateProvider, never()).formatDateTime(anyString(), any(DateTime.class));
        verify(mDelegate).populateWebView("description<br/><br/>copyright");
    }

    @Test
    public void beginWellFormedPublishDate() {
        // Setup
        DateTime dateTime = DateTime.now();
        when(mDateProvider.parseDateTimeString(anyString(), anyString())).thenReturn(dateTime);
        when(mDateProvider.formatDateTime(anyString(), any(DateTime.class))).thenReturn("formatted date time");

        // Run
        mViewModel.begin(mBundle, mDelegate);

        // Verify
        verify(mAnalyticsProvider).trackScreenView("AuthorInfoScreen");
        assertThat(mViewModel.glue.authorImageUrl.get(), is("author image url"));
        assertThat(mViewModel.glue.titleText.get(), is("title"));
        assertThat(mViewModel.glue.authorText.get(), is("By author\\nformatted date time"));
        verify(mDateProvider).parseDateTimeString(Artwork.PUBLISH_DATE_FORMAT, "01/06/2016");
        verify(mDateProvider).formatDateTime("dd/MM/yyyy", dateTime);
        verify(mDelegate).populateWebView("description<br/><br/>copyright");
    }

    @Test
    public void beginMissingDescription() {
        // Setup
        DateTime dateTime = DateTime.now();
        when(mDateProvider.parseDateTimeString(anyString(), anyString())).thenReturn(dateTime);
        when(mDateProvider.formatDateTime(anyString(), any(DateTime.class))).thenReturn("formatted date time");
        mArtwork.setDescription(null);
        mArtwork.setCopyright(null);

        // Run
        mViewModel.begin(mBundle, mDelegate);

        // Verify
        verify(mAnalyticsProvider).trackScreenView("AuthorInfoScreen");
        assertThat(mViewModel.glue.authorImageUrl.get(), is("author image url"));
        assertThat(mViewModel.glue.titleText.get(), is("title"));
        assertThat(mViewModel.glue.authorText.get(), is("By author\\nformatted date time"));
        verify(mDateProvider).parseDateTimeString(Artwork.PUBLISH_DATE_FORMAT, "01/06/2016");
        verify(mDateProvider).formatDateTime("dd/MM/yyyy", dateTime);
        verify(mDelegate).populateWebView("The author has not provided an image description.");
    }

    @Test
    public void closeSelected() {
        // Setup
        mViewModel.begin(mBundle, mDelegate);
        reset(mDelegate);

        // Run
        mViewModel.closeSelected();

        // Verify
        verify(mDelegate).finishActivity();
        verifyNoMoreInteractions(mDelegate);
    }
}