package io.github.marcelbraghetto.jokemachine.lib.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Marcel Braghetto on 18/01/16.
 *
 * Joke manager to help with retrieving a joke to display for use by the Google App Engine end
 * point of our server.
 *
 * When someone requests a joke DTO, we will randomly return one from our list collection.
 *
 * The randomisation is very basic so sometimes you will 'randomly' get the same joke back in
 * sequence. Could extend this code with user sessions that track what they've seen randomly etc
 * but its out of scope for this assignment project.
 */
public enum JokesManager {
    Instance;

    private final List<JokeDTO> mJokes;
    private final Random mRandom;

    /**
     * Create a joke manager and populate it with a collection of funny but utterly clean jokes...
     */
    JokesManager() {
        mRandom = new Random();
        mJokes = new ArrayList<>();

        mJokes.add(new JokeDTO("Why did the belt go to jail?", "It held up a pair of pants."));
        mJokes.add(new JokeDTO("What do you give a lemon in distress?", "Lemonade."));
        mJokes.add(new JokeDTO("What do you call a bear with no teeth?", "A gummy bear."));
        mJokes.add(new JokeDTO("Where do you put barking dogs?", "In a barking lot."));
        mJokes.add(new JokeDTO("Why didn't the skeleton go to the dance?", "He had no body to go with."));
        mJokes.add(new JokeDTO("What do you call a penguin in the desert?", "Lost."));
        mJokes.add(new JokeDTO("What has four wheels and flies?", "A garbage truck."));
        mJokes.add(new JokeDTO("Why couldn't the kid see the pirate movie?", "It was rated ARRR!"));
        mJokes.add(new JokeDTO("Why did the melon jump into the lake?", "It wanted to be a watermelon."));
        mJokes.add(new JokeDTO("How many tickles does it take to make a squid laugh?", "Ten-tickles."));
        mJokes.add(new JokeDTO("What do you call an alligator wearing a vest?", "An investigator."));
        mJokes.add(new JokeDTO("What do you call a vampire who makes pancakes?", "Count Spatula."));
        mJokes.add(new JokeDTO("Why do cowboys ride horses?", "Because they are too heavy to carry."));
        mJokes.add(new JokeDTO("How does the ocean say hello?", "It waves."));
        mJokes.add(new JokeDTO("What do sea monsters eat for lunch?", "Fish and ships."));
    }

    /**
     * Retrieve a random joke from the pool of jokes ...
     * @return new random joke DTO containing a joke.
     *
     */
    public JokeDTO getRandomJoke() {
        return mJokes.get(mRandom.nextInt(mJokes.size()));
    }
}
