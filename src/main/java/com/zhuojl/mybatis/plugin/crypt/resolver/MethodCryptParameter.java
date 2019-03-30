package com.zhuojl.mybatis.plugin.crypt.resolver;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:38
 */
@AllArgsConstructor
@Getter
class MethodCryptParameter {

    private String paramName;
    private CryptField cryptField;
    private Class cls;
}
