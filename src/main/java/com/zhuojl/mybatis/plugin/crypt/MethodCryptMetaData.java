package com.zhuojl.mybatis.plugin.crypt;

import com.zhuojl.mybatis.plugin.crypt.resolver.DecryptResolver;
import com.zhuojl.mybatis.plugin.crypt.resolver.EncryptResolver;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:36
 */
public class MethodCryptMetaData {

    public EncryptResolver encryptResolver;

    public DecryptResolver decryptResolver;

    public Object encrypt(Object object){
        if (object == null) {
            return object;
        }
        return encryptResolver.processEncrypt(object);
    }

    public Object decrypt(Object object){
        if (object == null) {
            return object;
        }
        return decryptResolver.processDecrypt(object);
    }

}
