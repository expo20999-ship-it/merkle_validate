package com.ourbit.util;

/**
 * NumberUtils
 * @author OurbitOBOfficial
 * @date 2025/03/23 21:31
 */
public class NumberUtils {
    /**
     * 获取数字为2的几次幂
     * @param number 数字
     * @return {@link int }
     * @author OurbitOBOfficial
     * @date 2025/03/23 21:30
     */
    public static int isTimesTwo(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("The Number Cannot Be Less Than Zero!!!");
        }
        if (!(number > 0 && (number & (number - 1)) == 0)) {
            return Integer.toBinaryString(number).length();
        }
        return Integer.toBinaryString(number).length() - 1;
    }
}
