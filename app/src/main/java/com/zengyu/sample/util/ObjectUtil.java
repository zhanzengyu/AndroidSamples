package com.zengyu.sample.util;

public class ObjectUtil {
    public static String wrapObjectToString(Object... arrays) {
        if (arrays == null) {
            return "";
        }

        StringBuilder sBuilder = new StringBuilder();
        for(Object element : arrays) {
            sBuilder.append(element);
        }

        return sBuilder.toString();
    }
}
