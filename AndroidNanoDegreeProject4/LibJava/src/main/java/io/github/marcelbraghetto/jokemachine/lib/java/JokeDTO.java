package io.github.marcelbraghetto.jokemachine.lib.java;

/**
 * Created by Marcel Braghetto on 18/01/16.
 *
 * Simple data transformation object model to represent a server side joke.
 *
 * Consumers of this DTO can do what they wish with it to represent it in platform specific ways
 * for example the Android library will transform a DTO into a Parcelable model.
 */
@SuppressWarnings("unused")
public class JokeDTO {
    private String mQuestion;
    private String mAnswer;

    public JokeDTO() {
        mQuestion = "";
        mAnswer = "";
    }

    /**
     * Construct a new Joke data transformation object with the given question and answer.
     *
     * This DTO can then be consumed and mutated as fit by other data classes.
     * @param question of the joke.
     * @param answer of the joke.
     */
    public JokeDTO(String question, String answer) {
        mQuestion = question;
        mAnswer = answer;
    }

    public void setQuestion(String question) {
        mQuestion = question;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setAnswer(String answer) {
        mAnswer = answer;
    }

    public String getAnswer() {
        return mAnswer;
    }
}
