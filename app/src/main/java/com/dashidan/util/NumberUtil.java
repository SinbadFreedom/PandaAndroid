package com.dashidan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}
