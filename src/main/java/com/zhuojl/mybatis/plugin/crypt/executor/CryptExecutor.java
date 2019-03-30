package com.zhuojl.mybatis.plugin.crypt.executor;

/**
 * 加解密执行者，可能是加密手机号码，可能是加密姓名等
 *
 * @author junliang.zhuo
 * @date 2019-03-29 13:20
 */
public interface CryptExecutor {

    String encrypt(String str);

    String decrypt(String str);
}
