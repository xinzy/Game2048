package com.xinzy.game2048.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Created by Xinzy on 2016/4/7.
 */
public class Utils
{

    private static float SCALE = 0.0f;

    public static final float getScale(Context context)
    {
        if (SCALE == 0.0f)
        {
            SCALE  = context.getResources().getDisplayMetrics().density;
        }
        return SCALE;
    }

    public static final Point getScreenSize(Context context)
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Point p = new Point();
        p.set(dm.widthPixels, dm.heightPixels);
        return p;
    }

    public static final int dp2px(Context context, int dp)
    {
        final float scale = getScale(context);
        return (int) (dp * scale + 0.5f);
    }
}
