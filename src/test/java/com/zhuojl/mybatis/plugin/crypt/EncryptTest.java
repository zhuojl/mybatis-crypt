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

class EncryptTest {

    String nameParam = "name";
    String phoneParam = "phone";
    private SqlSessionFactory sqlSessionFactory;

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
    public void testEncryptWithOutAnnotation() {
        Boolean encryptName = true;
        Boolean encryptPhone = false;
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            CryptTestUtil.adjustProperties(sqlSession.getConfiguration(), Boolean.TRUE, Boolean.FALSE);
            Mapper mapper = sqlSession.getMapper(Mapper.class);

            // 测试单参数
            mapper.insert(new User(1, nameParam, phoneParam));
            List<User> userList = mapper.listAll();
            Assert.assertNotNull(userList);
            Assert.assertTrue(userList.stream().allMatch(user -> user.isEncrypted(encryptName, nameParam, encryptPhone, phoneParam)));

            // 测试插入List
            mapper.batchInsert(Arrays.asList(new User(1, nameParam, phoneParam), new User(2, nameParam, phoneParam)));
            userList = mapper.listAll();
            Assert.assertNotNull(userList);
            Assert.assertTrue(userList.stream().allMatch(user -> user.isEncrypted(encryptName, nameParam, encryptPhone, phoneParam)));

            // 测试插入Array
            mapper.batchInsertArray(new User[]{new User(1, nameParam, phoneParam), new User(2, nameParam, phoneParam)});
            userList = mapper.listAll();
            Assert.assertNotNull(userList);
            Assert.assertTrue(userList.stream().allMatch(user -> user.isEncrypted(encryptName, nameParam, encryptPhone, phoneParam)));

            // 测试复合参数
            int index = 0;
            User user1 = new User(++index, nameParam + index, phoneParam + index);
            User user2 = new User(++index, nameParam + index, phoneParam + index);
            User user3 = new User(++index, nameParam + index, phoneParam + index);
            mapper.batchInsert(Arrays.asList(user1, user2, user3));

            // 由于插入都加密，所以在查询时，由于查询条件只有user3加密，所以只返回user3
            List<User> result = mapper.listUserWithMultipleParam(user1, user2, Arrays.asList(user3));
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(result.stream().allMatch(user -> user.isEncrypted(encryptName, user3.getName(), encryptPhone, user3.getPhone())));

            sqlSession.commit();
        }
    }


    @Test
    public void testEncryptWithAnnotation() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            CryptTestUtil.adjustProperties(sqlSession.getConfiguration(), Boolean.FALSE, Boolean.FALSE);
            Mapper mapper = sqlSession.getMapper(Mapper.class);

            // 测试单参数
            mapper.insert(new User(1, nameParam, phoneParam));
            List<User> userList = mapper.listAll();
            Assert.assertNotNull(userList);
            Assert.assertTrue(userList.stream().allMatch(user ->
                user.isEncrypted(Boolean.FALSE, nameParam, Boolean.FALSE, phoneParam))
            );

            // 测试插入List
            mapper.batchInsert(Arrays.asList(new User(1, nameParam, phoneParam), new User(2, nameParam, phoneParam)));
            userList = mapper.listAll();
            Assert.assertNotNull(userList);
            Assert.assertTrue(userList.stream().allMatch(user ->
                user.isEncrypted(Boolean.FALSE, nameParam, Boolean.FALSE, phoneParam))
            );

            // 测试插入Array
            mapper.batchInsertArray(new User[]{new User(1, nameParam, phoneParam), new User(2, nameParam, phoneParam)});
            userList = mapper.listAll();
            Assert.assertNotNull(userList);
            Assert.assertTrue(userList.stream().allMatch(user ->
                user.isEncrypted(Boolean.FALSE, nameParam, Boolean.FALSE, phoneParam))
            );

            // 测试复合参数
            int index = 0;
            User user1 = new User(++index, nameParam + index, phoneParam + index);
            User user2 = new User(++index, nameParam + index, phoneParam + index);
            User user3 = new User(++index, nameParam + index, phoneParam + index);
            mapper.batchInsert(Arrays.asList(user1, user2, user3));

            // 由于数据入库都没有加密，而查询的时候对第三个参数加密，所以查询返回user1 和 user2
            List<User> result = mapper.listUserWithMultipleParam(user1, user2, Arrays.asList(user3));
            Assert.assertNotNull(result);
            Assert.assertEquals(2, result.size());

            sqlSession.commit();
        }
    }

}
