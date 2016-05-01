package com.xinzy.game2048.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Xinzy on 2016/4/11.
 */
public class ScoreTextView extends TextView
{
    private int mScore;
    private ValueAnimator mAnimator;

    public ScoreTextView(Context context)
    {
        super(context);
        init(context);
    }

    public ScoreTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public ScoreTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        setScore(0);
    }

    public void setScore(int score)
    {
        this.mScore = score;
        setText(mScore + "");
        startAnim(0, mScore);
    }

    public void addScore(int score)
    {
        final int original = mScore;
        mScore += score;

        startAnim(original, mScore);
        setText(mScore + "");
    }

    private void startAnim(int start, int end)
    {
        mAnimator = new ValueAnimator();
        mAnimator.setIntValues(start, end);
        mAnimator.setDuration(300);
        mAnimator.addUpdateListener(new ScoreAddListener());
        mAnimator.start();
    }

    class ScoreAddListener implements ValueAnimator.AnimatorUpdateListener
    {
        @Override
        public void onAnimationUpdate(ValueAnimator animation)
        {
            int value = (int) animation.getAnimatedValue();
            setText(value + "");
        }
    }
}
