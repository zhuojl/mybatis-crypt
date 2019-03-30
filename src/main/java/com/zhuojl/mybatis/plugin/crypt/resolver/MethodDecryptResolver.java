package com.zhuojl.mybatis.plugin.crypt.resolver;

/**
 * 方法解密处理者
 *
 * @author junliang.zhuo
 * @date 2019-03-30 13:36
 */
public interface MethodDecryptResolver {

    Object processDecrypt(Object param);

}