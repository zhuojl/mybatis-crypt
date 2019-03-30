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

import com.zhuojl.mybatis.plugin.crypt.resolver.MethodCryptMetadata;
import com.zhuojl.mybatis.plugin.crypt.resolver.MethodCryptMetadataBuilder;

/**
 * 加解密插件
 *
 * @author junliang.zhuo
 * @date 2019-03-28 12:49
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

    /**
     * XXX 设置为public 主要是因为 多个单元测试之间类静态变量共享，需要清缓存
     */
    public static final ConcurrentHashMap<String, MethodCryptMetadata> METHOD_ENCRYPT_MAP = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MethodCryptMetadata methodCryptMetadata = getCachedMethodCryptMetaData((MappedStatement) args[0]);
        // 加密
        args[1] = methodCryptMetadata.encrypt(args[1]);
        // 获得出参
        Object returnValue = invocation.proceed();
        // 解密
        return methodCryptMetadata.decrypt(returnValue);
    }

    private MethodCryptMetadata getCachedMethodCryptMetaData(MappedStatement mappedStatement) {
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

}
