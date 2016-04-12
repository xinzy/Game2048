package com.xinzy.game2048;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xinzy.game2048.util.Logger;
import com.xinzy.game2048.widget.GameView;
import com.xinzy.game2048.widget.ScoreTextView;

public class GameActivity extends Activity implements View.OnClickListener, GameView.OnStatusChangeListener
{
    private static final String KEY_LAST_MAX_SCORE = "max_score";

    private GameView mGameview;
    private ImageButton startButton;
    private ScoreTextView scoreText;
    private ScoreTextView maxScoreText;

    private int maxScore;
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mGameview = (GameView) findViewById(R.id.gameView);
        mGameview.setOnStatusChangeListener(this);
        startButton = (ImageButton) findViewById(R.id.startBtn);
        startButton.setOnClickListener(this);
        scoreText = (ScoreTextView) findViewById(R.id.scoreText);
        maxScoreText = (ScoreTextView) findViewById(R.id.maxScoreText);

        maxScore = PreferenceManager.getDefaultSharedPreferences(this).getInt(KEY_LAST_MAX_SCORE, 0);
        maxScoreText.setScore(maxScore);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.startBtn:
            mGameview.start();
            scoreText.setScore(0);
            break;
        }
    }

    @Override
    public void onBackPressed()
    {
        long time = SystemClock.uptimeMillis();
        if (time - lastTime > 2000)
        {
            Toast.makeText(this, R.string.press_to_exit, Toast.LENGTH_LONG).show();
            lastTime = time;
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onScoreAdded(GameView.Direct direct, int score)
    {
        scoreText.addScore(score);
    }

    @Override
    public void gameover(boolean win)
    {
        int score = mGameview.getScore();
        Logger.d("game over and score is " + score);

        if (score > maxScore)
        {
            maxScoreText.setScore(score);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(KEY_LAST_MAX_SCORE, score).apply();
            maxScore = score;
        }
    }
}
