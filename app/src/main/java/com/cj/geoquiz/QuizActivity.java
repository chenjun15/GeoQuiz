package com.cj.geoquiz;

import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String ANSWERED_ARRAY = "answered";
    private static final String CHEATED_ARRAY = "cheated";
    private static final String CHEATED_COUNT = "cheated_count";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int MOST_CHEAT_COUNT = 3;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private final Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private int mCheatedCount = 0;

    private boolean[] mAnswered = new boolean[mQuestionBank.length];
    private int mAnsweredCnt = 0;
    private int mCorrectCnt = 0;
    private boolean[] mCheated = new boolean[mQuestionBank.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCheatedCount = savedInstanceState.getInt(CHEATED_COUNT, 0);
            mAnswered = savedInstanceState.getBooleanArray(ANSWERED_ARRAY);
            mCheated = savedInstanceState.getBooleanArray(CHEATED_ARRAY);
        }

        mQuestionTextView = findViewById(R.id.question_text_view);

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        if (mCheatedCount == MOST_CHEAT_COUNT)
            mCheatButton.setEnabled(false);

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null)
                return;
            mCheated[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            if (mCheated[mCurrentIndex] && ++mCheatedCount == MOST_CHEAT_COUNT)
                mCheatButton.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState: ");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(CHEATED_COUNT, mCheatedCount);
        savedInstanceState.putBooleanArray(ANSWERED_ARRAY, mAnswered);
        savedInstanceState.putBooleanArray(CHEATED_ARRAY, mCheated);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        if (mAnswered[mCurrentIndex]) {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        } else {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);

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
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId;

        if (mCheated[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                ++mCorrectCnt;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
        mAnswered[mCurrentIndex] = true;

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();

        if (++mAnsweredCnt == mQuestionBank.length) {
            new AlertDialog.Builder(QuizActivity.this)
                    .setIcon(R.drawable.arrow_left)
                    .setMessage(mCorrectCnt + " / " + mQuestionBank.length)
                    .setPositiveButton("确定", null)
                    .create()
                    .show();
        }
    }
}
