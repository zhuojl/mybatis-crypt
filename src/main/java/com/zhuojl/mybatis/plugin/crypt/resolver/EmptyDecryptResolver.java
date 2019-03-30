package com.zhuojl.mybatis.plugin.crypt.resolver;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 13:12
 */
public class EmptyDecryptResolver implements DecryptResolver {

    @Override
    public Object processDecrypt(Object param) {
        System.out.println("empty");
        return param;
    }

}