package com.zhuojl.mybatis.plugin.crypt.resolver;

/**
 * 表示方法不需要解密
 *
 * @author junliang.zhuo
 * @date 2019-03-29 13:12
 */
public class EmptyMethodDecryptResolver implements MethodDecryptResolver {

    @Override
    public Object processDecrypt(Object param) {
        System.out.println("empty");
        return param;
    }

}