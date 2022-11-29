package com.loyer.common.core.utils.common;

import com.loyer.common.core.enums.CalculateType;
import com.loyer.common.core.enums.RegExp;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 计算工具类
 *
 * @author kuangq
 * @date 2020-12-16 10:04
 */
public class CalculateUtil {

    /**
     * 精度运算
     *
     * @author kuangq
     * @date 2019-11-17 21:58
     */
    public static double actuarial(Object num1, Object num2, CalculateType calculateType) {
        if (!CheckParamsUtil.regularCheck(RegExp.NUMBER, num1, num2)) {
            throw new BusinessException(HintEnum.HINT_1033, new Object[]{num1, num2});
        }
        BigDecimal bigDecimal1 = new BigDecimal(num1.toString());
        BigDecimal bigDecimal2 = new BigDecimal(num2.toString());
        switch (calculateType) {
            case ADD:
                return bigDecimal1.add(bigDecimal2).doubleValue();
            case SUBTRACT:
                return bigDecimal1.subtract(bigDecimal2).doubleValue();
            case MULTIPLY:
                return bigDecimal1.multiply(bigDecimal2).doubleValue();
            case DIVIDE:
                try {
                    return bigDecimal1.divide(bigDecimal2).doubleValue();
                } catch (ArithmeticException e) {
                    //处理异常：Non-terminating decimal expansion; no exact representable decimal result.（默认保留四位精度）
                    return bigDecimal1.divide(bigDecimal2, 4, RoundingMode.HALF_UP).doubleValue();
                }
            default:
                throw new BusinessException(HintEnum.HINT_1034);
        }
    }

    /**
     * 四舍五入的精度运算
     *
     * @author kuangq
     * @date 2020-08-16 20:00
     */
    public static double round(double decimal, int digit) {
        BigDecimal bigDecimal = new BigDecimal(decimal);
        BigDecimal divisor = new BigDecimal("1");
        return bigDecimal.divide(divisor, digit, RoundingMode.HALF_UP).doubleValue();
    }
}