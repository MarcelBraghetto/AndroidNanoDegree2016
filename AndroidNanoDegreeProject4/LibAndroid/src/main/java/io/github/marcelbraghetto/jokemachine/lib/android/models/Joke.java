package io.github.marcelbraghetto.jokemachine.lib.android.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.parceler.Parcel;

import io.github.marcelbraghetto.jokemachine.server.jokesApi.model.JokeDTO;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Simple model representing a joke with some integration helpers to translate joke DTOs from the
 * Google Cloud Engine server.
 *
 * This model uses the Parceler library to automate the parcelable contract.
 */
@Parcel
public class Joke {
    /* package */ String mQuestion;
    /* package */ String mAnswer;

    @NonNull
    public String getQuestion() {
        return mQuestion == null ? "" : mQuestion;
    }

    @NonNull
    public String getAnswer() {
        return mAnswer == null ? "" : mAnswer;
    }

    /**
     * Default constructor required for Parceler.
     */
    public Joke() {
        mQuestion = "";
        mAnswer = "";
    }

    /**
     * Construct a new joke with the given properties.
     * @param question for the joke.
     * @param answer for the joke.
     */
    public Joke(@Nullable String question, @Nullable String answer) {
        mQuestion = question;
        mAnswer = answer;
    }

    /**
     * Construct a new joke object based on the translation of an existing joke DTO that would be
     * been retrieved from the GCE system.
     * @param dto to translate.
     * @return new joke populated from the DTO.
     */
    @NonNull
    public static Joke fromDTO(@NonNull JokeDTO dto) {
        return new Joke(dto.getQuestion(), dto.getAnswer());
    }
}
