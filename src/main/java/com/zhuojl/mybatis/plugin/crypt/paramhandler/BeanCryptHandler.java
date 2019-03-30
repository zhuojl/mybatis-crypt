package com.zhuojl.mybatis.plugin.crypt.paramhandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;
import com.zhuojl.mybatis.plugin.crypt.exception.MyRuntimeException;

/**
 * TODO
 *
 * @author junliang.zhuo
 * @date 2019-03-29 11:40
 */
public class BeanCryptHandler implements CryptHandler<Object> {

    private static final ConcurrentHashMap<Class, List<CryptFiled>> CLASS_ENCRYPT_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class, List<CryptFiled>> CLASS_DECRYPT_MAP = new ConcurrentHashMap<>();

    @Override
    public Object encrypt(Object bean, CryptField cryptField) {
        List<CryptFiled> filedList = CLASS_ENCRYPT_MAP.computeIfAbsent(bean.getClass(), this::getEncryptFields);
        filedList.forEach(cryptFiled -> {
            try {
                cryptFiled.field.setAccessible(true);
                Object obj = cryptFiled.field.get(bean);
                cryptFiled.field.set(bean, CryptHandlerFactory.getCryptHandler(obj, cryptFiled.cryptField).encrypt(obj, cryptFiled.cryptField));
            } catch (IllegalAccessException e) {
                throw new MyRuntimeException();
            }
        });
        return bean;
    }

    private List<CryptFiled> getEncryptFields(Class cls) {
        List<CryptFiled> filedList = new ArrayList<>();
        if (cls == null) {
            return filedList;
        }

        Field[] objFields = cls.getDeclaredFields();
        for (Field field : objFields) {
            CryptField cryptField = field.getAnnotation(CryptField.class);
            if (cryptField != null && cryptField.encrypt()) {
                filedList.add(new CryptFiled(cryptField, field));
            }
        }
        return filedList;
    }

    private List<CryptFiled> getDecryptFields(Class cls) {
        List<CryptFiled> filedList = new ArrayList<>();
        if (cls == null) {
            return filedList;
        }

        Field[] objFields = cls.getDeclaredFields();
        for (Field field : objFields) {
            CryptField cryptField = field.getAnnotation(CryptField.class);
            if (cryptField != null && cryptField.decrypt()) {
                filedList.add(new CryptFiled(cryptField, field));
            }
        }
        return filedList;
    }

    @Override
    public Object decrypt(Object param, CryptField cryptField) {
        List<CryptFiled> filedList = CLASS_DECRYPT_MAP.computeIfAbsent(param.getClass(), this::getEncryptFields);
        filedList.forEach(cryptFiled -> {
            try {
                cryptFiled.field.setAccessible(true);
                Object obj = cryptFiled.field.get(param);
                cryptFiled.field.set(param, CryptHandlerFactory.getCryptHandler(obj, cryptFiled.cryptField).decrypt(obj, cryptFiled.cryptField));
            } catch (IllegalAccessException e) {
                throw new MyRuntimeException();
            }
        });
        return param;
    }

    private class CryptFiled {

        private CryptFiled(CryptField cryptField, Field field) {
            this.cryptField = cryptField;
            this.field = field;
        }

        private Field field;
        private CryptField cryptField;
    }
}
