/**
 * Copyright 2009-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.zhuojl.mybatis.plugin.crypt;

import java.io.Reader;
import java.util.Arrays;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zhuojl.mybatis.BaseTest;

class EncryptTest {

    private SqlSessionFactory sqlSessionFactory;

    @BeforeEach
    void setUp() throws Exception {
        try (Reader reader = Resources.getResourceAsReader("com/zhuojl/mybatis/plugin/crypt/Config.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
        BaseTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "com/zhuojl/mybatis/plugin/crypt/createDb.sql");
    }

    @Test
    public void testUserParamIndex() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            System.out.println(mapper.testUserParamIndex(1, "zhuojl"));
            sqlSession.commit();
        }
    }

    @Test
    public void testSingleParam() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            adjustProperties(sqlSession.getConfiguration(), Boolean.TRUE, Boolean.FALSE);
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            mapper.insert(new User(1, "name"));
            mapper.insert(new User(1, "name"));
            System.out.println(mapper.testSingleParam(1));
            sqlSession.commit();
        }
    }


    @Test
    public void testMultiParamNoAnnotation() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            System.out.println(mapper.testMultiParamNoAnnotation(1, "zhuojl"));
            sqlSession.commit();
        }
    }


    @Test
    public void testList() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            System.out.println(mapper.testUseListMethod(Arrays.asList(1)));
            sqlSession.commit();
        }
    }

    @Test
    public void testForeach() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            System.out.println(mapper.testForeach(Arrays.asList(1, 2)));
            sqlSession.commit();
        }
    }

    public void adjustProperties(Configuration configuration, Boolean encryptWithOutAnnotation, Boolean decryptWithOutAnnotation) {
        if (configuration.getInterceptors().get(0) instanceof CryptInterceptor) {
            CryptInterceptor cryptInterceptor = (CryptInterceptor) configuration.getInterceptors().get(0);
            cryptInterceptor.setEncryptWithOutAnnotation(encryptWithOutAnnotation);
            cryptInterceptor.setDecryptWithOutAnnotation(decryptWithOutAnnotation);
        }
    }

}
