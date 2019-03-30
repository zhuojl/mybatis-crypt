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
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zhuojl.mybatis.MybatisTestUtil;

class DecryptTest {

    private SqlSessionFactory sqlSessionFactory;
    String nameParam = "name";
    String phoneParam = "phone";

    @BeforeEach
    void setUp() throws Exception {
        try (Reader reader = Resources.getResourceAsReader("com/zhuojl/mybatis/plugin/crypt/Config.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
        MybatisTestUtil.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
            "com/zhuojl/mybatis/plugin/crypt/createDb.sql");

        CryptTestUtil.cleanCache();
    }

    @Test
    public void testDecryptWithOutAnnotation() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            CryptTestUtil.adjustProperties(sqlSession.getConfiguration(), Boolean.FALSE, Boolean.TRUE);
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User user = new User(1, nameParam, phoneParam);
            mapper.insert(user);

            List<User> userList = mapper.listAll();
            Assert.assertTrue(userList.stream().allMatch(item ->
                item.isDecrypted(false, nameParam, true, phoneParam))
            );

            mapper.deleteAll();
            mapper.insert(user);
            User[] users = mapper.listAllUserWithAnnotation();
            Assert.assertTrue(Arrays.stream(users).allMatch(item ->
                item.isDecrypted(false, nameParam, true, phoneParam))
            );

            mapper.deleteAll();
            mapper.insert(user);
            User temp = mapper.get(user.getId());
            Assert.assertTrue(temp.isDecrypted(false, nameParam, true, phoneParam));

            mapper.deleteAll();
            mapper.insert(user);
            temp = mapper.getWithAnnotation(user.getId());
            Assert.assertTrue(temp.isDecrypted(false, nameParam, true, phoneParam));

            sqlSession.commit();
        }
    }


    @Test
    public void testDecryptWithAnnotation() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            CryptTestUtil.adjustProperties(sqlSession.getConfiguration(), Boolean.FALSE, Boolean.FALSE);

            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User user = new User(1, nameParam, phoneParam);
            mapper.insert(user);

            List<User> userList = mapper.listAll();
            Assert.assertTrue(userList.stream().allMatch(item ->
                item.isDecrypted(false, nameParam, false, phoneParam))
            );

            mapper.deleteAll();
            mapper.insert(user);
            User[] users = mapper.listAllUserWithAnnotation();
            Assert.assertTrue(Arrays.stream(users).allMatch(item ->
                item.isDecrypted(false, nameParam, true, phoneParam))
            );

            mapper.deleteAll();
            mapper.insert(user);
            User temp = mapper.get(user.getId());
            Assert.assertTrue(temp.isDecrypted(false, nameParam, false, phoneParam));

            mapper.deleteAll();
            mapper.insert(user);
            temp = mapper.getWithAnnotation(user.getId());
            Assert.assertTrue(temp.isDecrypted(false, nameParam, true, phoneParam));

            sqlSession.commit();
        }
    }

}
