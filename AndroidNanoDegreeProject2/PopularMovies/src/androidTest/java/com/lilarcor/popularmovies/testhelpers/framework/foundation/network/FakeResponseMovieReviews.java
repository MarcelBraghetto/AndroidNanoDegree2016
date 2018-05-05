package com.lilarcor.popularmovies.testhelpers.framework.foundation.network;

/**
 * Created by Marcel Braghetto on 12/08/15.
 *
 * Fake response for a movie reviews data collection.
 */
public final class FakeResponseMovieReviews {
    private FakeResponseMovieReviews() { }
    public static final String CONTENT =
        "{\n" +
        "\t\"id\": 135397,\n" +
        "\t\"page\": 1,\n" +
        "\t\"results\": [\n" +
        "\t\t{\n" +
        "\t\t\t\"author\": \"Number 1 fan\",\n" +
        "\t\t\t\"content\": \"I was a huge fan of the original 3 movies!\",\n" +
        "\t\t\t\"id\": \"55910381c3a36807f900065d\",\n" +
        "\t\t\t\"url\": \"http://j.mp/1GHgSxi\"\n" +
        "\t\t},\n" +
        "\t\t{\n" +
        "\t\t\t\"author\": \"Number 2 fan\",\n" +
        "\t\t\t\"content\": \"Overall action packed movie...\",\n" +
        "\t\t\t\"id\": \"559238f89251415df80000aa\",\n" +
        "\t\t\t\"url\": \"http://j.mp/1FMD5JI\"\n" +
        "\t\t}\n" +
        "\t],\n" +
        "\t\"total_pages\": 1,\n" +
        "\t\"total_results\": 2\n" +
        "}";
}
