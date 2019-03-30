package com.zhuojl.mybatis.plugin.crypt;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.zhuojl.mybatis.plugin.crypt.resolver.MethodCryptMetadataBuilder;

/**
 * 项目：mybatis-crypt 包名：org.apache.ibatis.plugin 功能：数据库数据脱敏 加解密算法推荐：aes192 + base64 时间：2017-11-22 作者：miaoxw
 */
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class CryptInterceptor implements Interceptor {

    /**
     * true表示默认加密出参，false表示只认注解
     */
    private Boolean encryptWithOutAnnotation;
    /**
     * true表示默认解密出参，false表示只认注解
     */
    private Boolean decryptWithOutAnnotation;

    private static final ConcurrentHashMap<String, MethodCryptMetaData> METHOD_ENCRYPT_MAP = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MethodCryptMetaData methodCryptMetaData = getCachedMethodCryptMetaData((MappedStatement) args[0]);
        // 加密
        args[1] = methodCryptMetaData.encrypt(args[1]);
        // 获得出参
        Object returnValue = invocation.proceed();
        // 解密
        return methodCryptMetaData.decrypt(returnValue);
    }

    private MethodCryptMetaData getCachedMethodCryptMetaData(MappedStatement mappedStatement) {
        return METHOD_ENCRYPT_MAP.computeIfAbsent(mappedStatement.getId(), id ->
            new MethodCryptMetadataBuilder(id, encryptWithOutAnnotation, decryptWithOutAnnotation).build()
        );
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        encryptWithOutAnnotation = Boolean.valueOf(properties.getProperty("encryptWithOutAnnotation", "false"));
        decryptWithOutAnnotation = Boolean.valueOf(properties.getProperty("decryptWithOutAnnotation", "false"));
    }

    public void setEncryptWithOutAnnotation(boolean encryptWithOutAnnotation) {
        this.encryptWithOutAnnotation = encryptWithOutAnnotation;
    }

    public void setDecryptWithOutAnnotation(boolean decryptWithOutAnnotation) {
        this.decryptWithOutAnnotation = decryptWithOutAnnotation;
    }
}
