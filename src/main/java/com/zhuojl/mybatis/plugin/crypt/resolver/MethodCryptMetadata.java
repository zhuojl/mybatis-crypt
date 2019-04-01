package com.zhuojl.mybatis.plugin.crypt.resolver;

import lombok.Getter;
import lombok.Setter;

/**
 * 方法加解密 元数据
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:36
 */
@Setter
@Getter
public class MethodCryptMetadata {

    /**
     * 方法加密处理和
     */
    public MethodEncryptResolver methodEncryptResolver;

    /**
     * 方法解密处理者
     */
    public MethodDecryptResolver methodDecryptResolver;

    public Object encrypt(Object object){
        if (object == null) {
            return object;
        }
        return methodEncryptResolver.processEncrypt(object);
    }

    public Object decrypt(Object object){
        if (object == null) {
            return object;
        }
        return methodDecryptResolver.processDecrypt(object);
    }

}
