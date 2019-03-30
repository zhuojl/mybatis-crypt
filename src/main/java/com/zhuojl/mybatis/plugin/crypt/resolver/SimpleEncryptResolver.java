package com.zhuojl.mybatis.plugin.crypt.resolver;


import com.zhuojl.mybatis.plugin.crypt.paramhandler.CryptHandlerFactory;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 13:12
 */
public class SimpleEncryptResolver implements EncryptResolver {

    @Override
    public Object processEncrypt(Object param) {
        return CryptHandlerFactory.getCryptHandler(param, null).encrypt(param, null);
    }
}