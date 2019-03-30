package com.zhuojl.mybatis.plugin.crypt.resolver;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;
import com.zhuojl.mybatis.plugin.crypt.handler.CryptHandlerFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 简单解密处理者
 *
 * @author junliang.zhuo
 * @date 2019-03-29 13:12
 */
@Getter
@AllArgsConstructor
public class SimpleMethodDecryptResolver implements MethodDecryptResolver {

    private CryptField cryptField;

    @Override
    public Object processDecrypt(Object param) {
        return CryptHandlerFactory.getCryptHandler(param, cryptField).decrypt(param, cryptField);
    }

}