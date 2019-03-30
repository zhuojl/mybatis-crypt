/**
 * Copyright 2009-2018 the original author or authors.
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

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;

public interface Mapper {

    void insert(User user);

    void batchInsert(List<User> user);

    void batchInsertArray(@Param("arrayName") User[] user);

    List<User> listUserWithMultipleParam(@Param("user1") User user1, @Param("user2") User user2,
        @Param("userList") @CryptField List<User> userList);



    @CryptField
    User getWithAnnotation(Integer id);

    User get(Integer id);

    @CryptField
    User[] listAllUserWithAnnotation();

    List<User> listAll();

    void deleteAll();
}
