package com.zhuojl.mybatis.plugin.crypt.handler;

import java.util.Arrays;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;

/**
 * 处理 Array 对象的加解密
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:40
 */
public class ArrayCryptHandler implements CryptHandler<Object> {

    @Override
    public Object encrypt(Object object, CryptField cryptField) {
        if (object == null) {
            return null;
        }
        return Arrays.stream((Object[]) object)
            .map(item -> CryptHandlerFactory.getCryptHandler(item, cryptField).encrypt(item, cryptField))
            .toArray();
    }

    @Override
    public Object decrypt(Object param, CryptField cryptField) {
        if (param == null) {
            return null;
        }
        return Arrays.stream((Object[]) param)
            .map(item -> CryptHandlerFactory.getCryptHandler(item, cryptField).decrypt(item, cryptField))
            .toArray();
    }


}
