package io.github.marcelbraghetto.dailydeviations.framework.artworks.models;

import android.content.ContentValues;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_AUTHOR;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_AUTHOR_IMAGE_URL;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_COPYRIGHT;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_DESCRIPTION;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_FAVOURITE;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_GUID;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_IMAGE_HEIGHT;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_IMAGE_URL;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_IMAGE_WIDTH;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_PUBLISH_DATE;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_TIMESTAMP;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_TITLE;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_WEB_URL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Marcel Braghetto on 14/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class ArtworkTest {
    private Artwork createPopulatedArtwork() {
        Artwork artwork = new Artwork();

        artwork.setGuid("guid");
        artwork.setTitle("title");
        artwork.setAuthor("author");
        artwork.setAuthorImageUrl("author image url");
        artwork.setDescription("description");
        artwork.setImageUrl("image url");
        artwork.setPublishDate("publish date");
        artwork.setCopyright("copyright");
        artwork.setWebUrl("web url");
        artwork.setImageWidth(100);
        artwork.setImageHeight(100);
        artwork.setTimestamp(1L);
        artwork.setFavourite(true);

        return artwork;
    }

    @Test
    public void publishDateFormat() {
        // Verify
        assertThat(Artwork.PUBLISH_DATE_FORMAT, is("EEE, dd MMM yyyy HH:mm:ss zzz"));
    }

    @Test
    public void verifyDefaultProperties() {
        // Setup
        Artwork artwork = new Artwork();

        // Verify
        assertThat(artwork.getGuid(), is(""));
        assertThat(artwork.getTitle(), is(""));
        assertThat(artwork.getAuthor(), is(""));
        assertThat(artwork.getAuthorImageUrl(), is(""));
        assertThat(artwork.getDescription(), is(""));
        assertThat(artwork.getImageUrl(), is(""));
        assertThat(artwork.getPublishDate(), is(""));
        assertThat(artwork.getCopyright(), is(""));
        assertThat(artwork.getWebUrl(), is(""));
        assertThat(artwork.getImageWidth(), is(0));
        assertThat(artwork.getImageHeight(), is(0));
        assertThat(artwork.getTimestamp(), is(0L));
        assertThat(artwork.isFavourite(), is(false));
    }

    @Test
    public void verifySetters() {
        // Setup
        Artwork artwork = createPopulatedArtwork();

        // Run
        artwork.setGuid("guid");
        artwork.setTitle("title");
        artwork.setAuthor("author");
        artwork.setAuthorImageUrl("author image url");
        artwork.setDescription("description");
        artwork.setImageUrl("image url");
        artwork.setPublishDate("publish date");
        artwork.setCopyright("copyright");
        artwork.setWebUrl("web url");
        artwork.setImageWidth(100);
        artwork.setImageHeight(100);
        artwork.setTimestamp(1L);
        artwork.setFavourite(true);

        // Verify
        assertThat(artwork.getGuid(), is("guid"));
        assertThat(artwork.getTitle(), is("title"));
        assertThat(artwork.getAuthor(), is("author"));
        assertThat(artwork.getAuthorImageUrl(), is("author image url"));
        assertThat(artwork.getDescription(), is("description"));
        assertThat(artwork.getImageUrl(), is("image url"));
        assertThat(artwork.getPublishDate(), is("publish date"));
        assertThat(artwork.getCopyright(), is("copyright"));
        assertThat(artwork.getWebUrl(), is("web url"));
        assertThat(artwork.getImageWidth(), is(100));
        assertThat(artwork.getImageHeight(), is(100));
        assertThat(artwork.getTimestamp(), is(1L));
        assertThat(artwork.isFavourite(), is(true));
    }

    @Test
    public void isValid() {
        // Run / Verify
        Artwork artwork = new Artwork();

        assertThat(artwork.isValid(), is(false));
        artwork.setGuid("");
        assertThat(artwork.isValid(), is(false));
        artwork.setGuid(null);
        assertThat(artwork.isValid(), is(false));
        artwork.setGuid("guid");

        artwork.setTitle("");
        assertThat(artwork.isValid(), is(false));
        artwork.setTitle(null);
        assertThat(artwork.isValid(), is(false));
        artwork.setTitle("title");

        artwork.setImageUrl("");
        assertThat(artwork.isValid(), is(false));
        artwork.setImageUrl(null);
        assertThat(artwork.isValid(), is(false));
        artwork.setImageUrl("image url");

        artwork.setImageWidth(0);
        assertThat(artwork.isValid(), is(false));
        artwork.setImageWidth(10);

        artwork.setImageHeight(0);
        assertThat(artwork.isValid(), is(false));
        artwork.setImageHeight(10);

        assertThat(artwork.isValid(), is(true));
    }

    @Test
    public void setGuidRemoveSpecialStrings() {
        // Setup
        Artwork artwork = new Artwork();
        String input = "http:// https:/some guid    here";

        // Run
        artwork.setGuid(input);

        // Verify
        assertThat(artwork.getGuid(), is("____some_guid____here"));
    }

    @Test
    public void getContentValues() {
        // Setup
        Artwork artwork = createPopulatedArtwork();

        // Run
        ContentValues values = artwork.getContentValues();

        // Verify
        assertThat(values.size(), is(13));
        assertThat(values.getAsString(COLUMN_GUID), is("guid"));
        assertThat(values.getAsString(COLUMN_TITLE), is("title"));
        assertThat(values.getAsString(COLUMN_AUTHOR), is("author"));
        assertThat(values.getAsString(COLUMN_AUTHOR_IMAGE_URL), is("author image url"));
        assertThat(values.getAsString(COLUMN_DESCRIPTION), is("description"));
        assertThat(values.getAsString(COLUMN_IMAGE_URL), is("image url"));
        assertThat(values.getAsInteger(COLUMN_IMAGE_WIDTH), is(100));
        assertThat(values.getAsInteger(COLUMN_IMAGE_HEIGHT), is(100));
        assertThat(values.getAsString(COLUMN_PUBLISH_DATE), is("publish date"));
        assertThat(values.getAsString(COLUMN_COPYRIGHT), is("copyright"));
        assertThat(values.getAsString(COLUMN_WEB_URL), is("web url"));
        assertThat(values.getAsLong(COLUMN_TIMESTAMP), is(1L));
        assertThat(values.getAsBoolean(COLUMN_FAVOURITE), is(true));
    }
}