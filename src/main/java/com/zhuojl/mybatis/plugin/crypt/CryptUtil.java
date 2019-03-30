package com.zhuojl.mybatis.plugin.crypt;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 12:49
 */
public class CryptUtil {

    private static final Set<Class> IGNORE_CLASS = new HashSet<>();
    static {
        IGNORE_CLASS.add(Byte.class);
        IGNORE_CLASS.add(Short.class);
        IGNORE_CLASS.add(Integer.class);
        IGNORE_CLASS.add(Long.class);
        IGNORE_CLASS.add(Float.class);
        IGNORE_CLASS.add(Double.class);
        IGNORE_CLASS.add(Boolean.class);
        IGNORE_CLASS.add(Character.class);
    }
    public static boolean inIgnoreClass(Class cls) {
        return IGNORE_CLASS.contains(cls);
    }

}
