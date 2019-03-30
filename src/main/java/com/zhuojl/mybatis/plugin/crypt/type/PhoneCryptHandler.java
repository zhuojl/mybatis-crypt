package com.zhuojl.mybatis.plugin.crypt.type;

/**
 * 参数加解密实现类
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:32
 */
public class PhoneCryptHandler implements TypeHandler {

    @Override
    public String encrypt(String str) {
        System.out.println("crypt phone: " + str);
        return "crypt phone: " + str;
    }

    @Override
    public String decrypt(String str) {
        System.out.println("decrypt phone: " + str);
        return "decrypt phone: " + str;
    }
}
