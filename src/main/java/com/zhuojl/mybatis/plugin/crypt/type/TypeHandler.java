package com.zhuojl.mybatis.plugin.crypt.type;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 13:20
 */
public interface TypeHandler {

    String encrypt(String str);

    String decrypt(String str);
}
