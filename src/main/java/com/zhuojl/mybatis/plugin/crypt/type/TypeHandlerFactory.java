package com.zhuojl.mybatis.plugin.crypt.type;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;
import com.zhuojl.mybatis.plugin.crypt.exception.MyRuntimeException;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 20:31
 */
public class TypeHandlerFactory {

    private static TypeHandler PHONE_CRYPT_HANDLER = new PhoneCryptHandler();
    private static TypeHandler NAME_CRYPT_HANDLER = new NameCryptHandler();

    /**
     * 根据cryptField中不同的配置
     */
    public static TypeHandler getTypeHandler(CryptField cryptField) {
        TypeHandler typeHandler;
        if (cryptField.value() == TypeEnum.NAME) {
            typeHandler = NAME_CRYPT_HANDLER;
        } else if (cryptField.value() == TypeEnum.PHONE) {
            typeHandler = PHONE_CRYPT_HANDLER;
        } else {
            throw new MyRuntimeException();
        }
        return typeHandler;
    }
}
