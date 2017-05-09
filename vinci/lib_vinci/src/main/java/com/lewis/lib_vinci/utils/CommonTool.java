
package com.lewis.lib_vinci.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验用户名、密码、手机号、身份证等格式的合法性
 */
public class CommonTool {
    private static CommonTool tools;

    final int[] wi = {
            7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1
    };
    // 校验码
    final int[] vi = {
            1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2
    };
    private int[] ai = new int[18];

    public  synchronized static CommonTool getInstance() {
        if (null == tools) {
            tools = new CommonTool();
        }
        return tools;
    }

    public boolean checkUsername(String name) {
        boolean flag = false;
        Pattern mobilePattern = Pattern.compile("1[0-9]{10}");
        if (!TextUtils.isEmpty(name) && mobilePattern.matcher(name).matches()) {
            flag = true;
        }
        return flag;
    }

    public boolean checkPassword(String password) {
        boolean flag = false;
//        String regex = "[A-Za-z0-9]{8,16}";
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
        Pattern passwordPattern = Pattern.compile(regex);
        Matcher matcher = passwordPattern.matcher(password);
        if (!TextUtils.isEmpty(password) && matcher.matches()) {
            flag = true;
        }
        return flag;
    }

    public boolean checkChineseName(String name) {

        boolean flag = true;
        Pattern pattern = Pattern.compile("[\u4E00-\u9FA5]");
        if(name.length()<2){//姓名长度至少为2
            flag  = false;
            return flag;
        }
        for (int i = 1; i <= name.length(); i++) {//姓名的每个字均为汉字
            String cc = name.substring(i-1,i);
            System.out.println(cc);
            Matcher matcher = pattern.matcher(cc);
            if(!matcher.matches()){
                flag  = false;
                return flag;
            }
        }
        return flag;
    }

    public String hidePhoneNum(String phoneNum) {
        return phoneNum.substring(0, 3) + "****" + phoneNum.substring(phoneNum.length()-4, phoneNum.length());
    }
    public String hideOilCardNum(String oilCard) {
        return oilCard.substring(0, 3) + "****" + oilCard.substring(14, 16);
    }
    public String hideUserCardNum(String userCard) {
        int cc = userCard.length();
        String aa  = "" ;
        for (int i = 0; i < cc-7; i++) {
            aa = aa+"*";
        }
        return userCard.substring(0, 4) + aa+ userCard.substring(cc-3, cc);
    }
    public String hideIdentityCard(String cardNum) {
        return cardNum.substring(0, 2) + "**************" + cardNum.substring(cardNum.length()-2, cardNum.length());
    }

    // 校验身份证的校验码
    public boolean verifyIDCard(String idcard) {
//        if(BuildConfig.DEBUG){
//            return true;
//        }
    	
    	
    	char[] idcards = idcard.substring(0, idcard.length()-1).toCharArray();
    	for (char c : idcards) {
			if ("x".equals(c+"") || "X".equals(c+"")) {
				return false;
			}
		}
        
        if (idcard.length() == 15) {
            idcard = uptoeighteen(idcard);
        }
        if (idcard.length() != 18) {
            return false;
        }
        String verify = idcard.substring(17, 18);
        if(verify.equalsIgnoreCase("x")){
        	verify = verify.toUpperCase();
        }
        if (verify.equals(getVerify(idcard))) {
            return true;
        }
        return false;
    }

    // 15位转18位
    private String uptoeighteen(String fifteen) {
        String eightcardid = fifteen.substring(0, 6);
        eightcardid = eightcardid + "19";
        eightcardid = eightcardid + fifteen.substring(6, 15);
        eightcardid = eightcardid + getVerify(eightcardid);
        return eightcardid;
    }

    // 计算最后一位校验值
    private String getVerify(String eighteen) {
        int remain = 0;
        if (eighteen.length() == 18) {
            eighteen = eighteen.substring(0, 17);
        }
        if (eighteen.length() == 17) {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                String k = eighteen.substring(i, i + 1);
                ai[i] = Integer.valueOf(k);
            }
            for (int i = 0; i < 17; i++) {
                sum += wi[i] * ai[i];
            }
            remain = sum % 11;
        }
        return remain == 2 ? "X" : String.valueOf(vi[remain]);

    }
}
