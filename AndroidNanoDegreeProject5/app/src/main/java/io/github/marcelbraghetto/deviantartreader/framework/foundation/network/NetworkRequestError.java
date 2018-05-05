package io.github.marcelbraghetto.deviantartreader.framework.foundation.network;

/**
 * Created by Marcel Braghetto on 3/12/15.
 *
 * Simple type describing a network request failure error reason.
 */
public enum NetworkRequestError {
    ConnectionProblem,
    ServerError,
    ParsingError
}
