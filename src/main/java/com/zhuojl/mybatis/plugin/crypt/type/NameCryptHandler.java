package com.zhuojl.mybatis.plugin.crypt.type;

/**
 * 参数加解密实现类
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:32
 */
public class NameCryptHandler implements TypeHandler{

    @Override
    public String encrypt(String str){
        System.out.println(this.getClass().getSimpleName() + ":crypt name: " + str);
        return "crypt name: " + str;
    }

    @Override
    public String decrypt(String str) {
        System.out.println(this.getClass().getSimpleName() + ":decrypt name: " + str);
        return "decrypt name: " + str;
    }
}
