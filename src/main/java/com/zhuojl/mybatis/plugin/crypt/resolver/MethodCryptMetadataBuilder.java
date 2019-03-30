package com.zhuojl.mybatis.plugin.crypt.resolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuojl.mybatis.plugin.crypt.CryptUtil;
import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;
import com.zhuojl.mybatis.plugin.crypt.exception.MyRuntimeException;

/**
 * MethodCryptMetadata 的建造者
 *
 * @author junliang.zhuo
 * @date 2019-03-29 13:40
 */
public class MethodCryptMetadataBuilder {

    private static final MethodEncryptResolver EMPTY_ENCRYPT_RESOLVER = new EmptyMethodEncryptResolver();
    private static final MethodDecryptResolver EMPTY_DECRYPT_RESOLVER = new EmptyMethodDecryptResolver();

    private String statementId;
    private Boolean encryptWithOutAnnotation;
    private Boolean decryptWithOutAnnotation;

    public MethodCryptMetadataBuilder(String statementId, Boolean encryptWithOutAnnotation, Boolean decryptWithOutAnnotation) {
        this.statementId = statementId;
        this.encryptWithOutAnnotation = encryptWithOutAnnotation;
        this.decryptWithOutAnnotation = decryptWithOutAnnotation;
    }

    public MethodCryptMetadata build() {
        MethodCryptMetadata methodCryptMetadata = new MethodCryptMetadata();
        Method m = getMethod();
        methodCryptMetadata.methodEncryptResolver = buildEncryptResolver(m);
        methodCryptMetadata.methodDecryptResolver = buildDecryptResolver(m);
        return methodCryptMetadata;
    }

    private MethodEncryptResolver buildEncryptResolver(Method m) {
        if (m == null) {
            return EMPTY_ENCRYPT_RESOLVER;
        }
        Parameter[] parameters = m.getParameters();
        // 如果方法没有参数
        if (parameters == null || parameters.length == 0) {
            return EMPTY_ENCRYPT_RESOLVER;
        }
        // 方法有加密注解的参数
        List<MethodAnnotationEncryptParameter> methodEncryptParamList = getCryptParams(m);

        if (encryptWithOutAnnotation && methodEncryptParamList.size() == 0) {
            // 如果有多个参数
            if (parameters.length == 0) {
                return EMPTY_ENCRYPT_RESOLVER;
            }

            // 只有一个参数时间
            return new SimpleMethodEncryptResolver();
        }
        if (methodEncryptParamList.size() > 0) {
            return new AnnotationMethodEncryptResolver(methodEncryptParamList);
        }
        return EMPTY_ENCRYPT_RESOLVER;
    }

    private MethodDecryptResolver buildDecryptResolver(Method m) {
        if (m == null) {
            return EMPTY_DECRYPT_RESOLVER;
        }
        // 无返回
        if (m.getReturnType() == Void.class) {
            return EMPTY_DECRYPT_RESOLVER;
        }
        final CryptField cryptField = m.getAnnotation(CryptField.class);

        if (decryptWithOutAnnotation || cryptField != null) {
            return new SimpleMethodDecryptResolver(cryptField);
        }

        return EMPTY_DECRYPT_RESOLVER;
    }

    private List<MethodAnnotationEncryptParameter> getCryptParams(Method m) {
        Parameter[] parameters = m.getParameters();
        if (parameters == null || parameters.length == 0) {
            return new ArrayList<>();
        }
        List<MethodAnnotationEncryptParameter> paramList = new ArrayList<>();
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
                    paramList.add(new MethodAnnotationEncryptParameter(param.value(), crypt, parameters[i].getType()));
                    break;
                }
            }
            crypt = null;
            param = null;
        }

        // 如果没有注解加密，如果默认加密，且只有一个参数，且是list，array， collection，相当于加了注解
        if (paramList.size() == 0 && encryptWithOutAnnotation && parameters.length == 1) {
            String name;
            if (parameters[0].getType().isAssignableFrom(List.class)) {
                name = getParameterNameOrDefault(paramAnnotations[0], "list");
                paramList.add(new MethodAnnotationEncryptParameter(name, null, parameters[0].getType()));
            } else if (parameters[0].getType().isAssignableFrom(Collection.class)) {
                name = getParameterNameOrDefault(paramAnnotations[0], "collection");
                paramList.add(new MethodAnnotationEncryptParameter(name, null, parameters[0].getType()));
            } else if (parameters[0].getType().isArray()) {
                name = getParameterNameOrDefault(paramAnnotations[0], "array");
                paramList.add(new MethodAnnotationEncryptParameter(name, null, parameters[0].getType()));
            }
        }
        return paramList;
    }

    private String getParameterNameOrDefault(Annotation[] annotations, String defaultName) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Param) {
                return ((Param)annotation).value();
            }
        }
        return defaultName;
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
