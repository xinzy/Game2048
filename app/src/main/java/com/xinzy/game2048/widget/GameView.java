package com.xinzy.game2048.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.xinzy.game2048.util.Utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by Xinzy on 2016/4/7.
 */
public class GameView extends ViewGroup implements ViewTreeObserver.OnGlobalLayoutListener
{
    public static final int COLS = 4;
    public static final int ROWS = 4;

    private static final int CORNER_SIZE_IN_DP = 6;
    private static final int DIVIDE_SIZE_IN_DP = 8;
    private static final int COLOR_BACKGROUND = 0xFFBBADA0;

    private static final int ROLLBACK_SIZE = 16;    //最多可回滚16步

    private static final int RESPONSE_DIVIDE = 20;  // 滑动事件响应最小像素数
    private static final int SUCCESS_THRESHOLD = 2048;// 游戏胜利的阈值

    private int mWidth;     // View 宽度
    private int mHeight;    // View 高度

    private float startX;   // 手指按下时的X坐标
    private float startY;   // 手指按下时的Y坐标

    private int mDivideSize;// 每个格子间的间距
    private int mCellSize;  // 每个格子的尺寸

    private int mScore;
    private int maxScore;
    private boolean isGameover;
    private boolean isWin;

    private boolean canRollback;    //游戏是否支持回滚
    private Deque<Integer[]> savedStatesQuene;   //已保存状态

    private boolean isInited;
    private CellView[][] mCells;
    private CellView mTipView;

    private Context mContext;
    private OnStatusChangeListener mOnStatusChangeListener;

    public GameView(Context context)
    {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        mContext = context;

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(Utils.dp2px(context, CORNER_SIZE_IN_DP));
        drawable.setColor(COLOR_BACKGROUND);
        setBackground(drawable);

        mDivideSize = Utils.dp2px(context, DIVIDE_SIZE_IN_DP);
        setPadding(mDivideSize, mDivideSize, mDivideSize, mDivideSize);

        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void setOnStatusChangeListener(OnStatusChangeListener mOnStatusChangeListener)
    {
        this.mOnStatusChangeListener = mOnStatusChangeListener;
    }

    public int getScore()
    {
        return mScore;
    }

    public void start()
    {
        start(true);
    }

    private void start(boolean clear)
    {
        if (clear) clear();
        if ((isWin || isGameover) && mTipView != null) removeView(mTipView);

        isWin = false;
        isGameover = false;
        maxScore = 0;
        mScore = 0;
        if (savedStatesQuene != null && ! savedStatesQuene.isEmpty())
        {
            savedStatesQuene.clear();
        }
        generateOneNumber();
        generateOneNumber();
    }

    public void generateOneNumber()
    {
        int max = 1;
        List<Point> blankPoint = new ArrayList<>(16);
        for (int i = 0; i < ROWS; i++)
        {
            for (int j = 0; j < COLS; j++)
            {
                final int num = mCells[i][j].getNumber();
                if (num == 0)
                {
                    Point p = new Point(i, j);
                    blankPoint.add(p);
                } else if (max < num)
                {
                    max = num;
                }
            }
        }

        final int size = blankPoint.size();
        if (size > 0)
        {
            int position = (int) (Math.random() * size);
            Point p = blankPoint.get(position);
            mCells[p.x][p.y].randomNumber();
        }
        blankPoint.clear();
    }

    public void clear()
    {
        for (int i = 0; i < ROWS; i++)
        {
            for (int j = 0; j < COLS; j++)
            {
                mCells[i][j].clear();
            }
        }
    }

    public boolean checkGameover()
    {
        for (int i = 0; i < ROWS; i++)
        {
            for (int j = 0; j < COLS; j++)
            {
                if (mCells[i][j].getNumber() == 0) return false;
            }
        }

        if (mCells[0][0].equals(mCells[1][0])) return false;

        for (int i = 1; i < COLS; i++)
        {
            if (mCells[0][i].equals(mCells[0][i - 1])) return false;
        }

        for (int i = 1; i < ROWS; i++)
        {
            for (int j = 1; j < COLS; j++)
            {
                if (mCells[i][j].equals(mCells[i - 1][j]) || mCells[i][j].equals(mCells[i][j - 1]))
                    return false;
            }
        }

        return true;
    }

    public void gameover()
    {
        if (! isGameover)
        {
            isGameover = true;

            showTips("Game Over!");
        }
    }

    public void win()
    {
        if (! isWin)
        {
            isWin = true;

            showTips("You Win~");
        }
    }

    private void showTips(String text)
    {
        if (mTipView == null)
        {
            mTipView = new CellView(mContext);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(Utils.dp2px(mContext, CellView.CORNER_SIZE_IN_DP));
            drawable.setColor(0x60000000);
            mTipView.setBackground(drawable);
            mTipView.setTextSize(56);
            mTipView.setTextColor(0xFFE0E0E0);
            mTipView.setText(text);
        }

        MarginLayoutParams lp = new MarginLayoutParams(mWidth - mDivideSize * 2, mHeight - mDivideSize * 2);
        addView(mTipView, lp);

        requestLayout();
    }

    public boolean isGameover()
    {
        return isGameover;
    }

    public void setCanRollback(boolean canRollback)
    {
        this.canRollback = canRollback;
    }

    public void saveStates()
    {
        if (savedStatesQuene == null)
        {
            savedStatesQuene = new ArrayDeque<>(ROLLBACK_SIZE);
        }

        Integer[] savedStates = null;
        if (savedStatesQuene.size() == ROLLBACK_SIZE)
        {
            savedStates = savedStatesQuene.pollFirst();
        }
        if (savedStates == null)
        {
            savedStates = new Integer[ROWS * COLS];
        }

        for (int i = 0; i < ROWS; i++)
        {
            for (int j = 0; j < COLS; j++)
            {
                savedStates[ROWS * i + j] = mCells[i][j].getNumber();
            }
        }
        savedStatesQuene.add(savedStates);
    }

    public void rollback()
    {
        if (! isGameover && ! isWin && savedStatesQuene != null)
        {
            Integer[] savedStates = savedStatesQuene.pollLast();

            if (savedStates != null)
            {
                for (int i = 0; i < ROWS; i++)
                {
                    for (int j = 0; j < COLS; j++)
                    {
                        mCells[i][j].setNumber(savedStates[ROWS * i + j]);
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mWidth = mHeight = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

        mCellSize = (mWidth - getPaddingLeft() - getPaddingRight() - mDivideSize * (COLS - 1)) / COLS;
    }

    @Override
    protected MarginLayoutParams generateDefaultLayoutParams()
    {
        return new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
    }

    @Override
    protected MarginLayoutParams generateLayoutParams(LayoutParams p)
    {
        return new MarginLayoutParams(p);
    }

    @Override
    public MarginLayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(mContext, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            View child = getChildAt(i);

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int left = lp.leftMargin + paddingLeft;
            int top = lp.topMargin + paddingTop;
            int right = left + lp.width;
            int bottom = top + lp.height;

            child.layout(left, top, right, bottom);
        }
    }

    @Override
    public void onGlobalLayout()
    {
        initCells();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private void initCells()
    {
        if (!isInited)
        {
            isInited = true;
            mCells = new CellView[COLS][ROWS];
            for (int i = 0; i < COLS; i++)
            {
                for (int j = 0; j < ROWS; j++)
                {
                    mCells[i][j] = new CellView(mContext);
                    MarginLayoutParams lp = new MarginLayoutParams(mCellSize, mCellSize);
                    lp.leftMargin = j * (mDivideSize + mCellSize);
                    lp.topMargin = i * (mDivideSize + mCellSize);

                    addView(mCells[i][j], lp);
                }
            }
        }

        start(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (isGameover || isWin) return true;

        switch (event.getActionMasked())
        {
        case MotionEvent.ACTION_DOWN:

            startX = event.getX();
            startY = event.getY();
            break;

        case MotionEvent.ACTION_OUTSIDE:
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:

            float x = event.getX();
            float y = event.getY();

            float divideX = x - startX;
            float divideY = y - startY;

            double divide = Math.sqrt(Math.pow(divideX, 2) + Math.pow(divideY, 2)); //滑动距离
            if (divide >= RESPONSE_DIVIDE)
            {
                if (Math.abs(divideX) > Math.abs(divideY))  //左右滑动
                {
                    if (divideX > 0)
                    {
                        moveRight();
                    } else
                    {
                        moveLeft();
                    }
                } else  //上下滑动
                {
                    if (divideY > 0)
                    {
                        moveDown();
                    } else
                    {
                        moveUp();
                    }
                }
            }
            break;
        }

        return true;
    }

    public void moveLeft()
    {
        int score = 0;
        boolean moved = false;

        if (canRollback)
        {
            saveStates();
        }

        for (int i = 0; i < ROWS; i ++)
        {
            for (int j = 1; j < COLS; j++)
            {
                if (mCells[i][j].getNumber() != 0)
                {
                    for (int k = 0; k < j; k++)
                    {
                        if (noBlockForLeft(i, k, j))
                        {
                            if (mCells[i][k].getNumber() == 0)
                            {
                                mCells[i][k].setNumber(mCells[i][j].getNumber());
                                mCells[i][j].clear();

                                moved = true;
                            } else if (mCells[i][k].equals(mCells[i][j]))
                            {
                                mCells[i][k].doubleNumber();
                                mCells[i][j].clear();

                                final int value = mCells[i][k].getNumber();
                                if (maxScore < value)
                                {
                                    maxScore = value;
                                }
                                score += value;
                                moved = true;
                            }
                        }
                    }
                }
            }
        }

        if (moved)
        {
            update(Direct.Left, score);
        }
    }

    private boolean noBlockForLeft(int row, int start, int end)
    {
        for (int x = start + 1; x < end; x++)
        {
            if (mCells[row][x].getNumber() != 0)
            {
                return false;
            }
        }

        return true;
    }

    public void moveRight()
    {
        int score = 0;
        boolean moved = false;

        if (canRollback)
        {
            saveStates();
        }

        for (int i = 0; i < ROWS; i++)
        {
            for (int j = COLS - 2; j >= 0; j--)
            {
                if (mCells[i][j].getNumber() != 0)
                {
                    for (int k = COLS - 1; k > j; k--)
                    {
                        if (noBlockForRight(i, k, j))
                        {
                            if (mCells[i][k].getNumber() == 0)
                            {
                                mCells[i][k].setNumber(mCells[i][j].getNumber());
                                mCells[i][j].clear();

                                moved = true;
                            } else if (mCells[i][k].equals(mCells[i][j]))
                            {
                                mCells[i][k].doubleNumber();
                                mCells[i][j].clear();

                                final int value = mCells[i][k].getNumber();
                                if (maxScore < value)
                                {
                                    maxScore = value;
                                }
                                score += value;
                                moved = true;
                            }
                        }
                    }
                }
            }
        }

        if (moved)
        {
            update(Direct.Right, score);
        }
    }

    private boolean noBlockForRight(int row, int start, int end)
    {
        for (int x = start - 1; x > end; x--)
        {
            if (mCells[row][x].getNumber() != 0)
            {
                return false;
            }
        }

        return true;
    }

    public void moveUp()
    {
        int score = 0;
        boolean moved = false;

        if (canRollback)
        {
            saveStates();
        }

        for (int j = 0; j < COLS; j ++)
        {
            for (int i = 1; i < ROWS; i++)
            {
                if (mCells[i][j].getNumber() != 0)
                {
                    for (int k = 0; k < i; k++)
                    {
                        if (noBlockForUp(j, k, i))
                        {
                            if (mCells[k][j].getNumber() == 0)
                            {
                                mCells[k][j].setNumber(mCells[i][j].getNumber());
                                mCells[i][j].clear();

                                moved = true;
                            } else if (mCells[k][j].equals(mCells[i][j]))
                            {
                                mCells[k][j].doubleNumber();
                                mCells[i][j].clear();

                                final int value = mCells[k][j].getNumber();
                                if (maxScore < value)
                                {
                                    maxScore = value;
                                }
                                score += value;
                                moved = true;
                            }
                        }
                    }
                }
            }
        }

        if (moved)
        {
            update(Direct.Up, score);
        }
    }

    private boolean noBlockForUp(int col, int start, int end)
    {
        for (int x = start + 1; x < end; x++)
        {
            if (mCells[x][col].getNumber() != 0)
            {
                return false;
            }
        }

        return true;
    }

    public void moveDown()
    {
        int score = 0;
        boolean moved = false;

        if (canRollback)
        {
            saveStates();
        }

        for (int j = 0; j < COLS; j++)
        {
            for (int i = ROWS - 2; i >= 0; i--)
            {
                if (mCells[i][j].getNumber() != 0)
                {
                    for (int k = COLS - 1; k > i; k--)
                    {
                        if (noBlockForDown(j, k, i))
                        {
                            if (mCells[k][j].getNumber() == 0)
                            {
                                mCells[k][j].setNumber(mCells[i][j].getNumber());
                                mCells[i][j].clear();

                                moved = true;
                            } else if (mCells[k][j].equals(mCells[i][j]))
                            {
                                mCells[k][j].doubleNumber();
                                mCells[i][j].clear();

                                final int value = mCells[k][j].getNumber();
                                if (maxScore < value)
                                {
                                    maxScore = value;
                                }
                                score += mCells[k][j].getNumber();
                                moved = true;
                            }
                        }
                    }
                }
            }
        }

        if (moved)
        {
            update(Direct.Down, score);
        }

    }

    private boolean noBlockForDown(int col, int start, int end)
    {
        for (int x = start - 1; x > end; x--)
        {
            if (mCells[x][col].getNumber() != 0)
            {
                return false;
            }
        }

        return true;
    }

    private void update(Direct direct, int score)
    {
        if (mOnStatusChangeListener != null)
        {
            mOnStatusChangeListener.onScoreAdded(direct, score);
        }

        mScore += score;
        if (maxScore == SUCCESS_THRESHOLD)
        {
            win();
            if (mOnStatusChangeListener != null) mOnStatusChangeListener.gameover(true);
        } else
        {
            generateOneNumber();
            if (checkGameover())
            {
                gameover();
                if (mOnStatusChangeListener != null) mOnStatusChangeListener.gameover(false);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Parcelable ss = super.onSaveInstanceState();
        SavedState state = new SavedState(ss);

        state.maxScore = maxScore;
        state.mScore = mScore;

        for (int i = 0; i < ROWS; i++)
        {
            for (int j = 0; j < COLS; j++)
            {
                state.mCells[i][j] = mCells[i][j].getNumber();
            }
        }

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (! (state instanceof SavedState))
        {
            super.onRestoreInstanceState(state);
        } else
        {
            SavedState s = (SavedState) state;
            super.onRestoreInstanceState(s.getSuperState());

            mScore = s.mScore;
            maxScore = s.maxScore;
            for (int i = 0; i < ROWS; i++)
            {
                for (int j = 0; j < COLS; j++)
                {
                    mCells[i][j].setNumber(s.mCells[i][j]);
                }
            }
        }
    }

    static class SavedState extends BaseSavedState
    {
        int[][] mCells;
        int maxScore;
        int mScore;

        public SavedState(Parcel source)
        {
            super(source);
            mCells = new int[ROWS][COLS];

            maxScore = source.readInt();
            mScore = source.readInt();
            for (int i = 0; i < ROWS; i++)
            {
                source.readIntArray(mCells[i]);
            }
        }

        public SavedState(Parcelable superState)
        {
            super(superState);
            mCells = new int[ROWS][COLS];
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
        {
            public SavedState createFromParcel(Parcel in)
            {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size)
            {
                return new SavedState[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            super.writeToParcel(dest, flags);

            dest.writeInt(maxScore);
            dest.writeInt(mScore);
            for (int i = 0; i < ROWS; i++)
            {
                dest.writeIntArray(mCells[i]);
            }
        }
    }

    public enum Direct
    {
        /**
         * Move Left
         */
        Left,

        /**
         * Move Right
         */
        Right,

        /**
         * Move Up
         */
        Up,

        /**
         * Move Down
         */
        Down
    }

    public interface OnStatusChangeListener
    {
        void onScoreAdded(Direct direct, int score);

        void gameover(boolean win);
    }
}
