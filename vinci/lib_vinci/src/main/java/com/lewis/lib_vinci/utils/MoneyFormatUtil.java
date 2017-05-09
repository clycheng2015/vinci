package com.lewis.lib_vinci.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Date: 2016-09-13
 * Time: 16:11
 * 格式化显示数字
 */
public class MoneyFormatUtil {
    //保持两位小数
    private static String STYLE2UNIT = "0.00";
    private static DecimalFormat df = new DecimalFormat();

    public static String formatMoney2Unit(String num) {
        if (df == null) {
            df = new DecimalFormat();
        }
        df.applyPattern(STYLE2UNIT);
        return df.format(Double.parseDouble(num));
    }

    public static String formatMoney2Unit(long num) {
        if (df == null) {
            df = new DecimalFormat();
        }
        df.applyPattern(STYLE2UNIT);
        return df.format(num);
    }

    /**
     * @param inMoneys  传入的参数值
     * @return
     */
    public static String exChangeMoney(Float inMoneys) {
        String outMoney = null;
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
            String[] str = String.valueOf(inMoneys).split("\\.");
            if (Integer.parseInt(str[1]) > 0) {
                format.setMaximumFractionDigits(2);
            } else {
                format.setMaximumFractionDigits(0);
            }
            outMoney = format.format(inMoneys).toString().replace("￥", "");
        } catch (Exception e) {
            outMoney = "0";
        }
        return outMoney;
    }

    /**
     * 格式化货币
     *
     * @param inMoneys 传入的值
     * @return 返回的指
     */
    public static String exChangeMoney1(Float inMoneys) {
        String outMoney = null;
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
            String[] str = String.valueOf(inMoneys).split("\\.");
            if (Integer.parseInt(str[1]) > 0) {
                format.setMaximumFractionDigits(2);
            } else {
                format.setMaximumFractionDigits(0);
            }
            outMoney = format.format(inMoneys).toString().replace("￥", "");
        } catch (Exception e) {
            outMoney = "0";
        }
        return outMoney;
    }
}

