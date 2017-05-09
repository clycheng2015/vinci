package com.lewis.lib_vinci.utils;

import android.text.TextUtils;
import android.util.Log;


import com.lewis.lib_vinci.LibConstants;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * 项目名称：app_android_dealer
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2017-03-02
 *
 * @version ${VSERSION}
 */


public class StringUtils {

    // ---------------------------------------------------------------------
    // General convenience methods for working with Strings
    // ---------------------------------------------------------------------

    /**
     * Check that the given CharSequence is neither <code>null</code> nor of
     * length 0. Note: Will return <code>true</code> for a CharSequence that
     * purely consists of whitespace.
     * <p>
     * <p>
     * <pre>
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     *
     * @param str the CharSequence to check (may be <code>null</code>)
     * @return <code>true</code> if the CharSequence is not null and has length
     * @see #
     */
    public static boolean hasLength(CharSequence str) { // NO_UCD (use private)
        return (str != null && str.length() > 0);
    }

    /**
     * Check that the given String is neither <code>null</code> nor of length 0.
     * Note: Will return <code>true</code> for a String that purely consists of
     * whitespace.
     *
     * @param str the String to check (may be <code>null</code>)
     * @return <code>true</code> if the String is not null and has length
     * @see #hasLength(CharSequence)
     */
    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    // ---------------------------------------------------------------------
    // Convenience methods for working with String arrays
    // ---------------------------------------------------------------------

    /**
     * Copy the given Collection into a String array. The Collection must
     * contain String elements only.
     *
     * @param collection the Collection to copy
     * @return the String array (<code>null</code> if the passed-in Collection
     * was <code>null</code>)
     */
    public static String[] toStringArray(Collection<String> collection) { // NO_UCD
        // (use
        // private)
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }

    /**
     * Tokenize the given String into a String array via a StringTokenizer.
     * Trims tokens and omits empty tokens.
     * <p>
     * The given delimiters string is supposed to consist of any number of delimiter characters. Each of those
     * characters can be used to separate tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using <code>delimitedListToStringArray</code>
     *
     * @param str        the String to tokenize
     * @param delimiters the delimiter characters, assembled as String (each of those
     *                   characters is individually considered as delimiter).
     * @return an array of the tokens
     * @see StringTokenizer
     * @see String#trim()
     * @see #
     */
    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    /**
     * Tokenize the given String into a String array via a StringTokenizer.
     * <p>
     * The given delimiters string is supposed to consist of any number of delimiter characters. Each of those
     * characters can be used to separate tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using <code>delimitedListToStringArray</code>
     *
     * @param str               the String to tokenize
     * @param delimiters        the delimiter characters, assembled as String (each of those
     *                          characters is individually considered as delimiter)
     * @param trimTokens        trim the tokens via String's <code>trim</code>
     * @param ignoreEmptyTokens omit empty tokens from the result array (only applies to
     *                          tokens that are empty after trimming; StringTokenizer will not
     *                          consider subsequent delimiters as token in the first place).
     * @return an array of the tokens (<code>null</code> if the input String was <code>null</code>)
     * @see StringTokenizer
     * @see String#trim()
     * @see #
     */
    public static String[] tokenizeToStringArray( // NO_UCD (use private)
                                                  String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>toString()</code> implementations.
     *
     * @param coll   the Collection to display
     * @param delim  the delimiter to use (probably a ",")
     * @param prefix the String to start each element with
     * @param suffix the String to end each element with
     * @return the delimited String
     */
    public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) { // NO_UCD
        // (use
        // private)
        if (coll == null || coll.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>toString()</code> implementations.
     *
     * @param coll  the Collection to display
     * @param delim the delimiter to use (probably a ",")
     * @return the delimited String
     */
    public static String collectionToDelimitedString(Collection<?> coll, String delim) { // NO_UCD
        // (use
        // private)
        return collectionToDelimitedString(coll, delim, "", "");
    }

    /**
     * Convenience method to return a Collection as a CSV String. E.g. useful
     * for <code>toString()</code> implementations.
     *
     * @param coll the Collection to display
     * @return the delimited String
     */
    public static String collectionToCommaDelimitedString(Collection<?> coll) {
        return collectionToDelimitedString(coll, ",");
    }

    public static long parseLong(String data) {
        long res = 0;
        try {
            res = Long.parseLong(data);
        } catch (Exception e) {
            res = 0;
        }
        return res;
    }

    public static float parseFloat(String data) { // NO_UCD (unused code)
        float res = 0;
        try {
            res = Float.parseFloat(data);
        } catch (Exception e) {
            res = 0;
        }
        return res;
    }

    /**
     * 该函数仅仅用来格式化显示的金额
     *
     * @param str
     * @return
     */
    public static String formatMoneyAmount(String str) {
        // 金额小数点右边最多只能有两位（精确点分）,如 111.99
        String decimalStr = "";
        String integerStr = "";
        if (str.contains(".")) {
            int dotIndex = str.indexOf(".");
            decimalStr = str.substring(dotIndex + 1);
            integerStr = str.substring(0, dotIndex);
        } else {
            return str + ".00";
        }
        if (decimalStr.length() < 2) {
            decimalStr = decimalStr + "0";
        }
        return integerStr + "." + decimalStr;
    }

    /**
     * @param format
     * @param money
     * @return
     */
    public static String formatDecimal(String format, double money) {
        return new DecimalFormat(format).format(money);
    }

    /**
     * 元 转换成分，传入和返回的均为string，并且返回的必须是整数形式的string，以便传给服务器 比如 传入0.08元转换后返回8分
     *
     * @param amount
     * @return
     */
    public static String yuan2Fen(String amount) {
        String res = "0";
        if (TextUtils.isEmpty(amount)) {
            amount = "0";
        }
        try {
            res = String.valueOf(new BigDecimal(amount).multiply(new BigDecimal("100")).setScale(0));
        } catch (Exception e) {
        }
        return res;
    }

    public static BigDecimal fen2YuanBigDecimal(String price) {
        if (TextUtils.isEmpty(price)) {
            price = "0";
        }
        return new BigDecimal(price).divide(BigDecimal.valueOf(100)).setScale(2);
    }

    public static String fen2Yuan(String price) {
        if (TextUtils.isEmpty(price)) {
            price = "0";
        }
        return new BigDecimal(price).divide(BigDecimal.valueOf(100)).setScale(2).toString();
    }

    /**
     * 两个价格相加
     *
     * @param add1
     * @param add2
     * @return
     */
    public static String priceAdd(String add1, String add2) {
        if (TextUtils.isEmpty(add1)) {
            add1 = "0";
        }
        if (TextUtils.isEmpty(add2)) {
            add2 = "0";
        }
        BigDecimal bigAdd1 = new BigDecimal(add1);
        BigDecimal bigAdd2 = new BigDecimal(add2);
        return bigAdd1.add(bigAdd2).toString();
    }

    /**
     * 将积分对应的价格转化为积分数量
     *
     * @param scoreFen
     * @param radio
     * @return
     */
    public static String scoreFenToScore(String scoreFen, String radio) {
        if (TextUtils.isEmpty(scoreFen)) {
            scoreFen = "0";
        }
        if (TextUtils.isEmpty(radio)) {
            radio = "1";
        }
        BigDecimal bigScoreFen = new BigDecimal(scoreFen);
        BigDecimal bigRadio = new BigDecimal(radio);

        if (bigRadio.compareTo(BigDecimal.ZERO) == 0) { // 如果比例为0
            bigRadio = BigDecimal.ONE;
        }

        BigDecimal ret = bigScoreFen.divide(bigRadio);

        if (LibConstants.DEBUG) {
            Log.d("StringUtil","score="
                    + scoreFen
                    + "#radio="
                    + radio
                    + "#scorefen="
                    + ret.setScale(0, BigDecimal.ROUND_DOWN).toString());
        }

        return ret.setScale(0, BigDecimal.ROUND_DOWN).toString(); // 直接舍弃小数
    }

    /**
     * 根据比例将积分数量换算成金额
     *
     * @param score
     * @param radio
     * @return
     */
    public static String scroeToScoreFen(String score, String radio) {
        if (TextUtils.isEmpty(score)) {
            score = "0";
        }
        if (TextUtils.isEmpty(radio)) {
            radio = "1";
        }
        BigDecimal bigScore = new BigDecimal(score);
        BigDecimal bigRadio = new BigDecimal(radio);

        if (bigRadio.compareTo(BigDecimal.ZERO) == 0) { // 如果比例为0
            bigRadio = BigDecimal.ONE;
        }

        BigDecimal ret = bigScore.multiply(bigRadio);

        if (LibConstants.DEBUG) {
            Log.d("StringUtil","score="
                    + score
                    + "#radio="
                    + radio
                    + "#scorefen="
                    + ret.setScale(0, BigDecimal.ROUND_DOWN).toString());
        }

        return ret.setScale(0, BigDecimal.ROUND_DOWN).toString(); // 直接舍弃小数
    }

    /**
     * 格式化金额，传入的是以 元为单位，返回也以元为单位
     * 主要用途是将整数的元统一换成精确到分的元
     * 例如传入20，则返回20.00
     *
     * @param amount
     * @return
     */
    public static String formatAmount(String amount) {
        return fen2Yuan(yuan2Fen(amount));
    }

    /**
     * 去掉字符串中所有的空格字符
     *
     * @param str
     * @return
     */
    public static String trimAll(String str) {
        return str.replace(" ", "");
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串
     * 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static boolean isPhoneNumber(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][3578]\\d{9}";// "[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、7，8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

    /**
     * 初步判断是否是邮箱
     *
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        Pattern emailPattern = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        return emailPattern.matcher(str).matches();
    }

    /**
     * 初步判断是否是银行卡号
     * 银行卡号的范围规定为6~32位
     *
     * @param str
     * @return
     */
    public static boolean isBankCardNumber(String str) {
        Pattern bankcardPattern = Pattern.compile("^[1-9][0-9]{5,31}$");
        return bankcardPattern.matcher(str).matches();
    }

    public static String formatPhoneNumber(String number) {
        Log.d("StringUtil","formatPhoneNumber. number = " + number);
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        int length = number.length();
        StringBuffer mobile = new StringBuffer();
        // 处理手机号码，为了适配不同手机的手机号码格式，此处只取出号码中的数字进行处理
        for (int i = 0; i < length; i++) {
            char c = number.charAt(i);
            if (c >= '0' && c <= '9') {
                mobile.append(c);
            }
        }
        if (mobile.length() > 11) {
            mobile = new StringBuffer(mobile.substring(mobile.length() - 11));
        } else if (mobile.length() < 11) {
            return null;
        }
        mobile.insert(7, ' ');
        mobile.insert(3, ' ');
        Log.d("StringUtil","formatPhoneNumber. mobile = " + mobile);
        return mobile.toString();
    }

    public static String replaceBom(String input) {
        if (!TextUtils.isEmpty(input)) {
            // consume an optional byte order mark (BOM) if it exists
            // 此处对UTF8 BOM头进行去除处理，以免当String为JSON串的时候，在2.x平台用来构造JSONObject时会抛JSONException
            if (input != null && input.startsWith("\ufeff")) {
                input = input.substring(1);
            }
        }
        return input;
    }
    // CHECKSTYLE:ON

    /**
     * 简单粗暴的判断指定的字符串的编码格式
     *
     * @param chars 指定的字符串
     * @return 编码格式
     */
    public static String guessCharacterSet(CharSequence chars) {
        // Very crude at the moment
        for (int i = 0; i < chars.length(); i++) {
            if (chars.charAt(i) > 0xFF) { // SUPPRESS CHECKSTYLE: 简单粗暴的判断字符串的编码格式
                return "UTF-8";
            }
        }
        return null;
    }

    /**
     * 位移
     */
    public static String getShiftString(String oriString, int sub) {
        if (oriString.length() < (sub * 2))
            return oriString;
        String end = oriString.substring(oriString.length() - 3, oriString.length());
        return end + oriString.substring(0, oriString.length() - 4);
    }

    /**
     * N 取值
     */
    public static int getN(String clientId, int n) {
        char tem = clientId.charAt(0);
        return tem;
    }

    /**
     * 三位随机数
     *
     * @return
     */
    public static int getRandom() {
        return (int) (Math.random() * 900) + 100;
    }

    /**
     * 获取指定字符串的大小
     *
     * @param str     目标字符串
     * @param charset 编码格式 UTF-8
     * @return
     * @throws UnsupportedEncodingException
     */
    public static long sizeOfString(String str, String charset) throws UnsupportedEncodingException {
        if (TextUtils.isEmpty(str)) {
            return 0L;
        } else {
            int len = str.length();
            if (len < 100) {
                return (long) str.getBytes(charset).length;
            } else {
                long size = 0L;

                for (int i = 0; i < len; i += 100) {
                    int end = i + 100;
                    end = end < len ? end : len;
                    String temp = getSubString(str, i, end);
                    size += (long) temp.getBytes(charset).length;
                }
                return size;
            }
        }
    }

    /**
     * 获取指定字符串 起始 与 结束 位置的子字符串
     *
     * @param str
     * @param start 指定位置
     * @param end   结束位置
     * @return
     */
    public static String getSubString(String str, int start, int end) {
        return new String(str.substring(start, end));
    }

    /**
     * 将unicode  字符串转换成默认的字符串（utf-8）
     *
     * @param unicodeStr
     * @return
     */
    public static String unicode2String(String unicodeStr) {
        StringBuffer sb = new StringBuffer();
        String str[] = unicodeStr.toUpperCase().split("U");
        for (int i = 0; i < str.length; i++) {
            if (str[i].equals("")) continue;
            char c = (char) Integer.parseInt(str[i].trim(), 16);
            sb.append(c);
        }
        return sb.toString();
    }

}
