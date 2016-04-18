package com.xinzy.game2048.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.xinzy.game2048.util.ColorUtil;
import com.xinzy.game2048.util.Utils;

/**
 * Created by Xinzy on 2016/4/7.
 */
public class CellView extends View
{
    public static final int CORNER_SIZE_IN_DP = 4;

    private GradientDrawable mBackground;
    private int mNumber;

    private Paint mPaint;
    private Rect mTargetRect;
    private String mText;
    private int mTextColor;
    private int mTextSize;

    private int mWidth;
    private int mHeight;

    private Animation mShowAnim;

    public CellView(Context context)
    {
        super(context);
        init(context);
    }

    public CellView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public CellView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        mBackground = new GradientDrawable();
        mBackground.setCornerRadius(Utils.dp2px(context, CORNER_SIZE_IN_DP));
        mBackground.setColor(ColorUtil.getColor(0));
        setBackground(mBackground);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setTextSize(48);

        mShowAnim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mShowAnim.setDuration(100);
    }

    public void setTextSizeInDp(int dp)
    {
        setTextSize(Utils.dp2px(getContext(), dp));
    }

    public void setTextSize(int px)
    {
        this.mTextSize = px;
    }

    public void setTextColor(int color)
    {
        mTextColor = color;
    }

    public String getText()
    {
        return mText;
    }

    public void setText(String text)
    {
        mText = text;
        invalidate();
    }

    public void clear()
    {
        setNumber(0);
    }

    public void setNumber(int mNumber)
    {
        this.mNumber = mNumber;
        redraw();
    }

    public void doubleNumber()
    {
        mNumber *= 2;
        redraw();
    }

    public void randomNumber(float salt)
    {
        int number = 2;
        if (Math.random() + salt > 0.75)
        {
            number = 4;
        }
        setNumber(number);

        startAnimation(mShowAnim);
    }

    public void randomNumber()
    {
       randomNumber(0f);
    }

    private void redraw()
    {
        setTextColor(ColorUtil.getTextColor(mNumber));
        mBackground.setColor(ColorUtil.getColor(mNumber));
        setBackground(mBackground);
        invalidate();
    }

    public int getNumber()
    {
        return mNumber;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        mWidth = getWidth();
        mHeight = getHeight();
        mTargetRect = new Rect(0, 0, mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        mPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(mTargetRect, mPaint);

        String text = mText;
        if (mNumber > 0)
        {
            text = mNumber + "";
        }

        if (!TextUtils.isEmpty(text))
        {
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mTextSize);

            Paint.FontMetricsInt metricsInt = mPaint.getFontMetricsInt();
            int baseline = (mTargetRect.bottom + mTargetRect.top - metricsInt.bottom - metricsInt.top) / 2;
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, mTargetRect.centerX(), baseline, mPaint);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null)
        {
            return false;
        } else if (! (o instanceof CellView))
        {
            return false;
        } else
        {
            CellView other = (CellView) o;
            return mNumber == other.getNumber();
        }
    }
}
