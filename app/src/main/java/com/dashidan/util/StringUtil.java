package com.dashidan.util;

import android.text.TextUtils;

public class StringUtil {
    /** String.trim()方法只能去掉半角空格，补充去掉全角符号*/
    public static String trim(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        char[] val = str.toCharArray();
        int st = 0;
        int len = val.length;
        /** 半角空格*/
        while ((st < len) && (val[st] <= ' ')) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        /** 全角空格*/
        while ((st < len) && (val[st] <= '　')) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= '　')) {
            len--;
        }
        return ((st > 0) || (len < val.length)) ? str.substring(st, len) : str;
    }
}
