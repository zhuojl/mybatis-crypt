package com.zhuojl.mybatis.plugin.crypt.resolver;

/**
 * 方法加密处理者
 *
 * @author junliang.zhuo
 * @date 2019-03-30 13:36
 */
public interface MethodEncryptResolver {

    Object processEncrypt(Object param);
}