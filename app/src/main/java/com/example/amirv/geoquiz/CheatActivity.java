package com.example.amirv.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String EXTRA_CHEAT_TOKENS_REMAINING = "com.bignerdranch.android.geoquiz.cheatTokensRemaining";

    private static final String KEY_IS_CHEATER = "is_cheater";
    private static final String KEY_REMAINING_TOKENS = "remaining_tokens";

    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView mRemainingTokensTextView;
    private Boolean mIsCheater = false;
    private int mCheatTokensRemaining = 3;


    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int cheatTokensRemaining) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_CHEAT_TOKENS_REMAINING, cheatTokensRemaining);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,
                false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);


        setMembersFromIntent();

        findViews();


        if (savedInstanceState != null) {
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
            mCheatTokensRemaining = savedInstanceState.getInt(KEY_REMAINING_TOKENS, 3);
            if(mIsCheater){
                showAnswer();
            }
        }



        updateTokensRemainingTextView();


        if (mCheatTokensRemaining > 0) {
            mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAnswer();

                    mCheatTokensRemaining -= 1;
                    updateTokensRemainingTextView();
                }
            });
        } else {
            mShowAnswerButton.setEnabled(false);
        }


        ((TextView)findViewById(R.id.api_level)).setText("Api Level " + Build.VERSION.SDK_INT);
    }


    private void showAnswer() {
        if (mAnswerIsTrue) {
            mAnswerTextView.setText(R.string.true_button);
        } else {
            mAnswerTextView.setText(R.string.false_button);
        }

        hideAnswerButton();

        mIsCheater = true;
        setAnswerShownResult(true);

    }

    private void hideAnswerButton() {
        int cx = mShowAnswerButton.getWidth() / 2;
        int cy = mShowAnswerButton.getHeight() / 2;
        float radius = mShowAnswerButton.getWidth();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || mIsCheater) {
            mShowAnswerButton.setVisibility(View.INVISIBLE);
        } else {
            Animator anim = ViewAnimationUtils
                    .createCircularReveal(mShowAnswerButton, cx, cy,
                            radius, 0);
            anim.addListener(new
                                     AnimatorListenerAdapter() {
                                         @Override
                                         public void onAnimationEnd(Animator
                                                                            animation) {
                                             super.onAnimationEnd(animation);
                                             mShowAnswerButton.setVisibility(View.INVISIBLE);
                                         }
                                     });
            anim.start();
        }
    }


    private void updateTokensRemainingTextView() {
        mRemainingTokensTextView.setText(getString(R.string.remaining_tokens, mCheatTokensRemaining));
    }

    private void findViews() {
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mRemainingTokensTextView = (TextView) findViewById(R.id.tokens_remaining);
    }

    private void setMembersFromIntent() {
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mCheatTokensRemaining = getIntent().getIntExtra(EXTRA_CHEAT_TOKENS_REMAINING, 3);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_CHEATER, mIsCheater);
        outState.putInt(KEY_REMAINING_TOKENS, mCheatTokensRemaining);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN,
                isAnswerShown);
        setResult(RESULT_OK, data);
    }
}
