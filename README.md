# 写在前面

最近国家强抓用户隐私，因此很多公司开始做数据加减密改造，那由于mybatis提供也提供了插件这个扩展，
很多的思路就是在插件上做文章，在github上也的确有这样的仓库,
- [基于Executor](https://github.com/miaoxinwei/mybatis-crypt)
- [基于StatementHandler](https://github.com/ikchan/mybatis-plugin-cryptogram)

# 正文
这两个分别是基于Executor和StatementHandler做的插件，这里不介绍怎么实现一个mybatis插件，有兴趣的可以看下官网，[mybatis插件demo](http://www.mybatis.org/mybatis-3/zh/configuration.html#plugins)
分别可以对以下4种情况就行扩展：
1. ParameterHandler (getParameterObject, setParameters)，aop setParameters是不行的，因为这时已经完成了sql解析，某些取值已经不会再通过原始的parameter取值，
比如：如果时foreach标签，在此时会通过boundSql.getAdditionalParameter获取
2. StatementHandler (prepare, parameterize, batch, update, query)，这里也是做不了的，因为已经失去了属性注解相关的信息
3. ResultSetHandler (handleResultSets, handleOutputParameters) 这个可以做结果集解密。
4. Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed) 这个当然是可以的，他就是查询的出入口

所以在对数据做加减密的插件时，选择对Executor做。不过在工作中，针对大部分的数据库操作，其实都是单表操作（入库全是单表操作），
那么这里大部分场景是不需要interceptor的，这个时候用[typeHandler](http://www.mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)就可以了，
更轻量，不过由于typeHandler无法捕获注解，只有增加typeHandler来支撑多种加解密处理的情况，对于联合查询，可以使用aop实现。

其实interceptor就是aop了，那么这里就实现一个基于Executor的Interceptor。在看到上面[基于Executor]实现的时候，总感觉有些别扭，
一是没有将方法元数据与加解密分开，二是太多if else判断在做加解密流程中。

新的实现将intercept方法分为以下4步
```java
public Object intercept(Invocation invocation) throws Throwable {
    Object[] args = invocation.getArgs();
    // 1.获取方法加解密 元数据
    MethodCryptMetadata methodCryptMetadata = getCachedMethodCryptMetaData((MappedStatement) args[0]);
    // 2.加密
    args[1] = methodCryptMetadata.encrypt(args[1]);
    // 3.执行sql
    Object returnValue = invocation.proceed();
    // 4.解密
    return methodCryptMetadata.decrypt(returnValue);
}
```
主要的逻辑在获取元数据上，也就是第一步，因为项目实际的原因，和一些mybatis部分参数（list,collection,array）特殊处理的原因，这一步做了一些特殊处理，
在继续之前，我们再梳理以下我们的需求，什么情况下需要对参数加密：
1. insert(POJO)或者insert(List<POJO>)这些方法的参数不加注解是否需要加密
2. 加了加密注解的参数需要加密

所以，如果项目中需求是没有加密注解就不处理，那么这里就比较简单。相反，就需要对insert(POJO)，insert(List<POJO>)和加了注解的情况做区分处理，insert(POJO) 待加密的值就是入参本身，有加密注解的待加密的值是从map中获取；而insert(List<POJO>)这种形式，待加密的值也是中map中获取，可以变相理解为加了一个为null的注解，把它划分到加了注解的一类中，所以这里把加密就分为了两种类型来处理。

对于解密，比加密逻辑简单，不需要像加密一样从map中获取，待解密的值就是返回结果。

整体逻辑可以通过这两张图有个大致了解：[类图](https://img-blog.csdnimg.cn/20190419204726748.jpg),[序列图](https://img-blog.csdnimg.cn/2019041920474410.jpg)

# 特殊说明
1. encryptWithOutAnnotation,decryptWithOutAnnotation可以通过插件properties修改
2. 代码中XXX标记的是特殊说明
3. 关于bean的加密，会涉及到原对象的修改，代码中通过clone避免
4. 为避免多处对null值就行判断，handler和executor等都加了Null的实现
5. MethodMetadata中的两个Resolver可能有一定的过度设计的嫌疑，主要是为了减少if else判断

# 写在最后
这个内容花了将近3天才写好，比想象中难，也是第一次写了传github，请多指教

