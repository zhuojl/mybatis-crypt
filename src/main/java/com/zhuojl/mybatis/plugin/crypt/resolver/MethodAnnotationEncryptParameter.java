package com.zhuojl.mybatis.plugin.crypt.resolver;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方法加密注解了的参数
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:38
 */
@AllArgsConstructor
@Getter
class MethodAnnotationEncryptParameter {

    private String paramName;
    private CryptField cryptField;
    private Class cls;
}
