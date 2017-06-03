package com.example.amirv.geoquiz;

/**
 * Created by amirv on 6/1/17.
 */

public class Question {
    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mAlreadyAnswered = false;
    private boolean mAnsweredCorrectly;

    public Question(int mTextResId, boolean mAnswerTrue) {
        this.mTextResId = mTextResId;
        this.mAnswerTrue = mAnswerTrue;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public boolean isAlreadyAnswered() {
        return mAlreadyAnswered;
    }
    public void answeredWith(boolean userAnswer) {
        mAnsweredCorrectly = userAnswer == isAnswerTrue();
        mAlreadyAnswered = true;
    }

    public boolean isAnsweredCorrectly() {
        return mAnsweredCorrectly;
    }
}
