package io.github.marcelbraghetto.deviantartreader.framework.artworks.content;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.Artwork;

/**
 * Created by Marcel Braghetto on 21/02/16.
 *
 * Helper class to take an XML RSS feed from the Deviant Art web site and parse it into
 * a collection of Artwork models.
 */
public class ArtworksParser {
    private static final String ns = null;

    private ArtworksParser() { }

    /**
     * Take the given source string which should be in the Deviant Art RSS format and attempt to
     * parse it into a collection of Artwork models.
     * @param source xml to parse.
     * @return list of artworks that were parsed, or null if an error occurred while parsing.
     */
    @Nullable
    public static List<Artwork> parse(@Nullable String source) {
        if(TextUtils.isEmpty(source)) {
            return null;
        }

        InputStream inputStream = new ByteArrayInputStream(source.getBytes());

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            parser.nextTag();

            return readFeed(parser);
        } catch(Exception e) {
            Log.e("ARTWORKS", e.getMessage());
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<Artwork> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Artwork> entries = new ArrayList<>();

        long now = System.currentTimeMillis();

        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                now++;

                Artwork entry = readEntry(parser, now);

                // Only keep artworks that are 'valid' which may have parsed successfully
                // but also have all our required fields.
                if(entry.isValid()) {
                    entries.add(entry);
                }
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off to their respective methods for processing. Otherwise, skips the tag.
    private static Artwork readEntry(XmlPullParser parser, long timestamp) throws XmlPullParserException, IOException {
        Artwork artwork = new Artwork();

        artwork.setTimestamp(timestamp);

        parser.require(XmlPullParser.START_TAG, ns, "item");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            switch (name) {
                case "guid":
                    // The guid doubles as both an id and the url to open to view the artwork.
                    String url = readTextNode(parser, name);
                    artwork.setGuid(url);
                    artwork.setWebUrl(url);
                    break;
                case "title":
                    artwork.setTitle(readTextNode(parser, name));
                    break;
                case "media:credit":
                    String author = readTextNode(parser, name);

                    // This is the author's profile link
                    if(author.startsWith("http:")) {
                        artwork.setAuthorImageUrl(author);
                    } else {
                        artwork.setAuthor(author);
                    }
                    break;
                case "media:description":
                    artwork.setDescription(readTextNode(parser, name));
                    break;
                case "media:content":
                    parser.require(XmlPullParser.START_TAG, ns, name);

                    String mediaType = parser.getAttributeValue(ns, "medium");

                    // There are media types other than images which we don't want.
                    if("image".equals(mediaType)) {
                        artwork.setImageUrl(parser.getAttributeValue(ns, "url"));
                        artwork.setImageWidth(Integer.parseInt(parser.getAttributeValue(ns, "width")));
                        artwork.setImageHeight(Integer.parseInt(parser.getAttributeValue(ns, "height")));
                    }

                    skip(parser);
                    break;
                case "pubDate":
                    artwork.setPublishDate(readTextNode(parser, name));
                    break;
                case "media:copyright":
                    artwork.setCopyright(readTextNode(parser, name));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return artwork;
    }

    private static String readTextNode(XmlPullParser parser, String nodeName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, nodeName);
        String result = extractTextValue(parser);
        parser.require(XmlPullParser.END_TAG, ns, nodeName);
        return result;
    }

    // For the tags title and summary, extracts their text values.
    private static String extractTextValue(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
