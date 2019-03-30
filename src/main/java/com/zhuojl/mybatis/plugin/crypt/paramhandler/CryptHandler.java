package com.zhuojl.mybatis.plugin.crypt.paramhandler;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:40
 */
public interface CryptHandler<T> {

    Object encrypt(T param, CryptField cryptField);

    Object decrypt(T param, CryptField cryptField);
}
