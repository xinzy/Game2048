package com.xinzy.game2048.util;

import android.util.SparseIntArray;

/**
 * Created by Xinzy on 2016/4/7.
 */
public class ColorUtil
{

    /**
     * 画布背景色   #BBADA0
     * 2          #EEE4DA
     * 4          #EDE0C8
     * 8          #F2B179
     * 16         #F59563
     * 32         #F67C5F
     * 64         #F65E3B
     * 128        #EDCF72
     * 256        #EDCC61
     * 512        #99CC00
     * 1024       #33B5E5
     * 2048       #0099CC
     * 4096       #AA66CC
     * 8192       #9933CC
     */
    private static SparseIntArray mColors;

    static
    {
        mColors = new SparseIntArray(16);
        mColors.put(0, 0xFFDAD0C4);
        mColors.put(2, 0xFFEEE4DA);
        mColors.put(4, 0xFFEDE0C8);
        mColors.put(8, 0xFFF2B179);
        mColors.put(16, 0xFFF59563);
        mColors.put(32, 0xFFF67C5F);
        mColors.put(64, 0xFFF65E3B);
        mColors.put(128, 0xFFEDCF72);
        mColors.put(256, 0xFFEDCC61);
        mColors.put(512, 0xFF99CC00);
        mColors.put(1024, 0xFF33B5E5);
        mColors.put(2048, 0xFF0099CC);
        mColors.put(4096, 0xFFAA66CC);
        mColors.put(8192, 0xFF9933CC);
    }

    public static final int getColor(int num)
    {
        return mColors.get(num, 0xFFFFFFFF);
    }

    public static final int getTextColor(int num)
    {
        return num >= 16 ? 0xFFFBF2E6 : 0xFF786B63;
    }
}
