package com.zhuojl.mybatis.plugin.crypt.executor;

/**
 * 姓名加解密执行者
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:32
 */
public class NameCryptExecutor implements CryptExecutor {

    public static final String ENCRYPT_STRING = "encrypt name:";
    public static final String DECRYPT_STRING = "decrypt name:";

    @Override
    public String encrypt(String str){
        System.out.println(this.getClass().getSimpleName() + ENCRYPT_STRING+ str);
        return ENCRYPT_STRING + str;
    }

    @Override
    public String decrypt(String str) {
        System.out.println(this.getClass().getSimpleName() + ":decrypt name: " + str);
        return DECRYPT_STRING + str;
    }
}
