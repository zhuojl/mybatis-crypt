package com.zhuojl.mybatis.plugin.crypt;

import java.util.Arrays;
import java.util.Objects;

import org.apache.ibatis.session.Configuration;

/**
 * 加解密工具类 插曲是：反射设置boolean异常，见 https://stackoverflow.com/questions/42590792/cannot-set-boolean-value-through-reflection
 *
 * @author junliang.zhuo
 * @date 2019-03-30 16:34
 */
public class CryptTestUtil {

    public static void adjustProperties(Configuration configuration, Boolean encryptWithOutAnnotation, Boolean decryptWithOutAnnotation) {
        if (configuration.getInterceptors().get(0) instanceof CryptInterceptor) {
            CryptInterceptor cryptInterceptor = (CryptInterceptor) configuration.getInterceptors().get(0);
            Arrays.stream(CryptInterceptor.class.getDeclaredFields())
                .forEach(field -> {
                    try {
                        if (Objects.equals("encryptWithOutAnnotation", field.getName())) {
                            field.setAccessible(true);
                            field.set(cryptInterceptor, Boolean.valueOf(encryptWithOutAnnotation));
                        } else if (Objects.equals("decryptWithOutAnnotation", field.getName())) {
                            field.setAccessible(true);
                            field.set(cryptInterceptor, Boolean.valueOf(decryptWithOutAnnotation));
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        }
    }

    public static void cleanCache() {
        CryptInterceptor.METHOD_ENCRYPT_MAP.clear();
    }
}
