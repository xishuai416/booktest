package com.wky.book.util;

/**
 * 金额转换工具类
 */

import java.text.NumberFormat;
import java.text.ParseException;

public class MoneyConvertUtil {

    /**
     * 分转元
     *
     * @param amount
     * @return
     */
    public static String fenToYuan(String amount) {
        NumberFormat format = NumberFormat.getInstance();
        try {
            Number number = format.parse(amount);
            double temp = number.doubleValue() / 100.0;
            format.setGroupingUsed(false);
            // 设置返回的小数部分所允许的最大位数
            format.setMaximumFractionDigits(2);
            amount = format.format(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return amount;
    }

    /**
     * 元转分
     *
     * @param amount
     * @return
     */
    public static String yuanToFen(String amount) {
        NumberFormat format = NumberFormat.getInstance();
        Number number = null;
        try {
            number = format.parse(amount);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double temp = number.doubleValue() * 100.0;
        format.setGroupingUsed(false);
        // 设置返回数的小数部分所允许的最大位数
        format.setMaximumFractionDigits(0);
        amount = format.format(temp);
        return amount;
    }
}
