package com.zhuojl.mybatis.plugin.crypt.resolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.annotations.Param;

import com.zhuojl.mybatis.plugin.crypt.CryptUtil;
import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;
import com.zhuojl.mybatis.plugin.crypt.MethodCryptMetaData;
import com.zhuojl.mybatis.plugin.crypt.exception.MyRuntimeException;

/**
 * 方法签名工具类
 *
 * @author junliang.zhuo
 * @date 2019-03-29 13:40
 */


public class MethodCryptMetadataBuilder {

    private String statementId;
    private Boolean encryptWithOutAnnotation;
    private Boolean decryptWithOutAnnotation;

    public MethodCryptMetadataBuilder(String statementId, Boolean encryptWithOutAnnotation, Boolean decryptWithOutAnnotation) {
        this.statementId = statementId;
        this.encryptWithOutAnnotation = encryptWithOutAnnotation;
        this.decryptWithOutAnnotation = decryptWithOutAnnotation;
    }

    private static final EncryptResolver EMPTY_ENCRYPT_RESOLVER = new EmptyEncryptResolver();
    private static final DecryptResolver EMPTY_DECRYPT_RESOLVER = new EmptyDecryptResolver();

    /**
     * 理想情况下，参数/结果需要加解密，都必须要加注解才行，但是实际情况是，项目中基本都会封装基本的增删该查，而且不会有Param注解， 所以对不可能对全部的增删改查方法去加注解，还得改xml。所以会特殊区分实体bean和List<bean>的参数，避免改动太大。
     */
    public MethodCryptMetaData build() {
        MethodCryptMetaData methodCryptMetaData = new MethodCryptMetaData();
        Method m = getMethod();
        methodCryptMetaData.encryptResolver = buildEncryptResolver(m);
        methodCryptMetaData.decryptResolver = buildDecryptResolver(m);
        return methodCryptMetaData;
    }

    private EncryptResolver buildEncryptResolver(Method m) {
        if (m == null) {
            return EMPTY_ENCRYPT_RESOLVER;
        }
        Parameter[] parameters = m.getParameters();
        // 如果方法没有参数
        if (parameters == null || parameters.length == 0) {
            return EMPTY_ENCRYPT_RESOLVER;
        }
        List<Parameter> parametersWithoutSpecial = Arrays.stream(parameters)
            .filter(parameter -> parameter.getType() != null)
            .collect(Collectors.toList());
        if (parametersWithoutSpecial == null || parametersWithoutSpecial.size() == 0) {
            return EMPTY_ENCRYPT_RESOLVER;
        }
        // 方法有加密注解的参数
        List<MethodCryptParameter> methodEncryptParamList = getCryptParams(m);

        if (decryptWithOutAnnotation && methodEncryptParamList.size() == 0) {
            // 如果有多个参数
            if (parametersWithoutSpecial.size() == 0) {
                return EMPTY_ENCRYPT_RESOLVER;
            }
            // 只有一个参数时间
            return new SimpleEncryptResolver();
        }
        if (methodEncryptParamList.size() > 0) {
            return new ListEncryptResolver(methodEncryptParamList);
        }
        return EMPTY_ENCRYPT_RESOLVER;
    }

    private DecryptResolver buildDecryptResolver(Method m) {
        if (m == null) {
            return EMPTY_DECRYPT_RESOLVER;
        }
        // 无返回
        if (m.getReturnType() == Void.class) {
            return EMPTY_DECRYPT_RESOLVER;
        }
        final CryptField cryptField = m.getAnnotation(CryptField.class);

        if (encryptWithOutAnnotation || cryptField != null) {
            return new SimpleDecryptResolver(cryptField);
        }

        return EMPTY_DECRYPT_RESOLVER;
    }

    private static List<MethodCryptParameter> getCryptParams(Method m) {
        Parameter[] parameters = m.getParameters();
        if (parameters == null || parameters.length == 0) {
            return new ArrayList<>();
        }
        List<MethodCryptParameter> paramList = new ArrayList<>();
        final Annotation[][] paramAnnotations = m.getParameterAnnotations();
        // get names from @CryptField annotations
        // 这里必须要配合Param注解一起使用，因为需要参数名称
        Param param = null;
        CryptField crypt = null;
        for (int i = 0; i < parameters.length; i++) {
            boolean isSpecial = false;
            if (isSpecial) {
                continue;
            }
            if (CryptUtil.inIgnoreClass(parameters[i].getType())) {
                continue;
            }
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof CryptField) {
                    crypt = (CryptField) annotation;
                }
                if (annotation instanceof Param) {
                    param = (Param) annotation;
                }
                if (crypt != null && param != null) {
                    paramList.add(new MethodCryptParameter(param.value(), crypt, parameters[i].getType()));
                    break;
                }
            }
            crypt = null;
            param = null;
        }
        return paramList;
    }


    private Method getMethod() {
        try {
            final Class clazz = Class.forName(statementId.substring(0, statementId.lastIndexOf(".")));
            final String methodName = statementId.substring(statementId.lastIndexOf(".") + 1);
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            return null;
        } catch (ClassNotFoundException e) {
            throw new MyRuntimeException();
        }
    }
}
