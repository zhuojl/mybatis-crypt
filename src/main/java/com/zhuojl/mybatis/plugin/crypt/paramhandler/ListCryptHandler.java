package com.zhuojl.mybatis.plugin.crypt.paramhandler;

import java.util.List;
import java.util.stream.Collectors;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:40
 */
public class ListCryptHandler implements CryptHandler<List> {

    @Override
    public Object encrypt(List list, CryptField cryptField) {
        if (!needCrypt(list)) {
            return list;
        }
        return list.stream()
            .map(item -> CryptHandlerFactory.getCryptHandler(item, cryptField).encrypt(item, cryptField))
            .collect(Collectors.toList());
    }

    @Override
    public Object decrypt(List param, CryptField cryptField) {
        if (!needCrypt(param)) {
            return param;
        }
        return param.stream()
            .map(item -> CryptHandlerFactory.getCryptHandler(item, cryptField).decrypt(item, cryptField))
            .collect(Collectors.toList());
    }

    private boolean needCrypt(List list) {
        return list != null && list.size() != 0;
    }
}
