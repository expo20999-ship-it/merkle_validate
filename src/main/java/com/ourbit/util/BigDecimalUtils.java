package com.ourbit.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * BigDecimal设置工具类
 *
 * @author OurbitOBOfficial
 * @date 2025/02/23 11:33 上午
 */
public class BigDecimalUtils {

    public static BigDecimal safeBigDecimal(BigDecimal value) {
        return Optional.ofNullable(value).orElse(BigDecimal.ZERO);
    }

    public static BigDecimal safeScaleBigDecimal(BigDecimal value, int scale, RoundingMode roundingMode) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value.setScale(scale, roundingMode);
    }

    public static BigDecimal safeBigDecimal(BigDecimal value, int scale, int roundingMode) {
        return safeBigDecimal(value).setScale(scale, roundingMode);
    }

    public static BigDecimal safeBigDecimalDefault(BigDecimal value, int scale) {
        return safeBigDecimal(value, scale, BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal safeBigDecimalStripTrailingZeros(BigDecimal value, int scale, int roundingMode) {
        return safeBigDecimal(value, scale, roundingMode).stripTrailingZeros();
    }

    public static BigDecimal safeSumBigDecimal(BigDecimal... values) {
        if (Objects.isNull(values) || values.length == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal b : values) {
            if (Objects.nonNull(b)) {
                result = result.add(b);
            }
        }
        return result;
    }

    public static BigDecimal safeMultiply(BigDecimal multiplicand, BigDecimal multiplier) {
        if (Objects.isNull(multiplicand) || Objects.isNull(multiplier)) {
            return BigDecimal.ZERO;
        }
        return safeBigDecimal(multiplicand).multiply(safeBigDecimal(multiplier));
    }

    public static BigDecimal safeSubtract(BigDecimal minuend, BigDecimal subtract) {
        BigDecimal tempMinuend = minuend;
        if (Objects.isNull(tempMinuend)) {
            tempMinuend = BigDecimal.ZERO;
        }
        BigDecimal tempSubtract = subtract;
        if (Objects.isNull(tempSubtract)) {
            tempSubtract = BigDecimal.ZERO;
        }

        return tempMinuend.subtract(tempSubtract);
    }

    public static BigDecimal parseString(String value) {
        if (StringUtils.isBlank(value)) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value);
    }

    public static String getBigDecimalPlainStr(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }

        return bigDecimal.stripTrailingZeros().toPlainString();
    }

}
