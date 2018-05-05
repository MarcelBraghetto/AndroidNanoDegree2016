package io.github.marcelbraghetto.jokemachine.server;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import io.github.marcelbraghetto.jokemachine.lib.java.JokeDTO;
import io.github.marcelbraghetto.jokemachine.lib.java.JokesManager;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Api end point for retrieving jokes from our server.
 *
 * This end point is exposed through our server, but delegates the action of fetching a joke to
 * our Java library jokes manager.
 */
@Api(
        name = "jokesApi", version = "v1",
        namespace = @ApiNamespace(
            ownerDomain = "server.jokemachine.marcelbraghetto.github.io",
            ownerName = "server.jokemachine.marcelbraghetto.github.io")
)
public class JokesEndPoint {
    @ApiMethod(name = "getJoke", httpMethod = ApiMethod.HttpMethod.GET)
    public JokeDTO getJoke() {
        // Defer the fetching operation to the Java library Jokes manager.
        return JokesManager.Instance.getRandomJoke();
    }
}