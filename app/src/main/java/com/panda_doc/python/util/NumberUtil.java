package com.panda_doc.python.util;

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

    /**
     * 通过经验计算等级
     *
     * @param expArr 每级的总经验，包括前面经验的和，经验值不清空
     */
    public static int getLevelByExp(int exp, int[] expArr) {
        for (int i = 0; i < expArr.length; i++) {
            if (exp < expArr[i]) {
                /** 等级为索引+1*/
                return i;
            }
        }
        /** 超上限返回最大值*/
        return expArr.length - 1;
    }

    /**
     * 根据等级获取当前级别所需经验
     */
    public static int getTotalExpByLevel(int level, int[] expArr) {
        if (level >= expArr.length) {
            return expArr[expArr.length - 1];
        }
        return expArr[level];
    }

}
