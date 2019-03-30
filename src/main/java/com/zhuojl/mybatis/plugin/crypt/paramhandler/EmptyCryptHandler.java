package com.zhuojl.mybatis.plugin.crypt.paramhandler;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:40
 */
public class EmptyCryptHandler implements CryptHandler<Object> {

    @Override
    public Object encrypt(Object param, CryptField cryptField) {
        System.out.println(this.getClass().getSimpleName() + ":crypt: " + param);
        return param;
    }

    @Override
    public Object decrypt(Object param, CryptField cryptField) {
        System.out.println(this.getClass().getSimpleName() + ":decrypt: " + param);
        return param;
    }
}
