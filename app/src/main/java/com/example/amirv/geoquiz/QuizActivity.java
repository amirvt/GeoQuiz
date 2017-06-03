package com.example.amirv.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String KEY_IS_CHEATER = "is_cheater";
    private static final String KEY_CHEAT_TOKENS_REMAINING = "cheat_tokens_remaining";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;
    private ImageButton mPrevQuestion;
    private Button mCheatButton;
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia,
                    true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast,
                    false),
            new Question(R.string.question_africa,
                    false),
            new Question(R.string.question_americas,
                    true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater = false;
    private int mCheatTokensRemaining = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            retrieveState(savedInstanceState);
        }

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mPrevQuestion = (ImageButton) findViewById(R.id.prev_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mCheatButton = (Button) findViewById(R.id.cheat_button);

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        mPrevQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevQuestion();
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CheatActivity.newIntent(
                        QuizActivity.this, mQuestionBank[mCurrentIndex].isAnswerTrue(),
                        mCheatTokensRemaining);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }

    private void retrieveState(Bundle savedInstanceState) {
        mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
        mCheatTokensRemaining = savedInstanceState.getInt(KEY_CHEAT_TOKENS_REMAINING, 3);
    }

    @Override
    protected void onStart() {
        super.onStart();
		Log.d(TAG, "onStart() called");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
		Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
		Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
		Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState called");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBoolean(KEY_IS_CHEATER, mIsCheater);
        outState.putInt(KEY_CHEAT_TOKENS_REMAINING, mCheatTokensRemaining);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT){
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            if (CheatActivity.wasAnswerShown(data)) {
                mCheatTokensRemaining -= 1;
            }
        }
    }

    private void prevQuestion() {
        mCurrentIndex = mod(mCurrentIndex - 1 , mQuestionBank.length);
        updateQuestion();
    }

    private void nextQuestion() {
        mCurrentIndex = mod(mCurrentIndex + 1 , mQuestionBank.length);
        updateQuestion();
    }

    private int mod(int x, int y)
    {
        int result = x % y;
        return result < 0? result + y : result;
    }

    private void updateQuestion() {
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
        if(mQuestionBank[mCurrentIndex].isAlreadyAnswered()){
            disableAnswerButtons();
        } else {
            enableAnswerButtons();
        }
        checkIfFinished();
    }

    private void checkIfFinished() {
        int score = 0;
        for (Question question : mQuestionBank) {
            if (!question.isAlreadyAnswered()){
                return;
            }

            if (question.isAnsweredCorrectly()){
                score += 1;
            }
        }
        Toast.makeText(QuizActivity.this,
                getString(R.string.score_text, score, mQuestionBank.length),
                Toast.LENGTH_LONG)
        .show();
    }

    private void enableAnswerButtons() {
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);
    }

    private void disableAnswerButtons() {
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    private void checkAnswer(boolean userPressedTrue){

        mQuestionBank[mCurrentIndex].answeredWith(userPressedTrue);
        int messageResId;
        if (mIsCheater){
            messageResId = R.string.judgment_toast;
        } else {
            messageResId = mQuestionBank[mCurrentIndex].isAnsweredCorrectly()
                    ? R.string.correct_toast : R.string.incorrect_toast;
        }
        Toast toast = Toast.makeText(QuizActivity.this,
                messageResId,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 50);
        toast.show();

        disableAnswerButtons();
    }
}
