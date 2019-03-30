package com.zhuojl.mybatis.plugin.crypt.executor;

/**
 * 电话加解密执行者
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:32
 */
public class PhoneCryptExecutor implements CryptExecutor {

    public static final String ENCRYPT_STRING = "encrypt phone:";
    public static final String DECRYPT_STRING = "decrypt phone:";

    @Override
    public String encrypt(String str) {
        System.out.println(this.getClass().getSimpleName() + ENCRYPT_STRING + str);
        return ENCRYPT_STRING + str;
    }

    @Override
    public String decrypt(String str) {
        System.out.println(this.getClass().getSimpleName() + DECRYPT_STRING + str);
        return DECRYPT_STRING + str;
    }
}
