package com.zhuojl.mybatis.plugin.crypt.resolver;

import java.util.List;
import java.util.Map;

import com.zhuojl.mybatis.plugin.crypt.paramhandler.CryptHandlerFactory;

import lombok.AllArgsConstructor;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 13:11
 */
@AllArgsConstructor
public class ListEncryptResolver implements EncryptResolver {

    private List<MethodCryptParameter> methodCryptParameterList;

    @Override
    public Object processEncrypt(Object param) {
        Map map = (Map) param;
        methodCryptParameterList.forEach(item ->
            map.computeIfPresent(item.getParamName(), (key, oldValue) ->
                CryptHandlerFactory.getCryptHandler(oldValue, item.getCryptField()).encrypt(oldValue, item.getCryptField())
            )
        );
        return param;
    }

}
