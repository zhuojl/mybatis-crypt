package com.zhuojl.mybatis.plugin.crypt.handler;

import java.util.Collection;
import java.util.stream.Collectors;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;

/**
 * * 处理 Collection 对象的加解密
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:40
 */
public class CollectionCryptHandler implements CryptHandler<Collection> {


    @Override
    public Object encrypt(Collection collection, CryptField cryptField) {
        if (!needCrypt(collection)) {
            return collection;
        }
        return collection.stream()
            .map(item -> CryptHandlerFactory.getCryptHandler(item, cryptField).encrypt(item, cryptField))
            .collect(Collectors.toList());
    }

    @Override
    public Object decrypt(Collection param, CryptField cryptField) {
        if (!needCrypt(param)) {
            return param;
        }
        return param.stream()
            .map(item -> CryptHandlerFactory.getCryptHandler(item, cryptField).decrypt(item, cryptField))
            .collect(Collectors.toList());
    }


    private boolean needCrypt(Collection collection) {
        return collection != null && collection.size() != 0;
    }
}
