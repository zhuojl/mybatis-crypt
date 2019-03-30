/**
 * Copyright 2009-2015 the original author or authors.
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

import java.io.Serializable;
import java.util.Objects;

import com.zhuojl.mybatis.plugin.crypt.annotation.CryptField;
import com.zhuojl.mybatis.plugin.crypt.executor.NameCryptExecutor;
import com.zhuojl.mybatis.plugin.crypt.executor.PhoneCryptExecutor;
import com.zhuojl.mybatis.plugin.crypt.executor.CryptType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private Integer id;

    @CryptField(value = CryptType.NAME, decrypt = false)
    private String name;

    @CryptField(value = CryptType.PHONE, encrypt = false)
    private String phone;


    public boolean isEncrypted(Boolean encryptName, String targetName, Boolean encryptPhone, String targetPhone) {
        return isNameEncrypted(encryptName, targetName) && isPhoneEncrypted(encryptPhone, targetPhone);
    }

    public boolean isDecrypted(Boolean encryptName, String targetName, Boolean encryptPhone, String targetPhone) {
        return isNameDecrypted(encryptName, targetName) && isPhoneDecrypted(encryptPhone, targetPhone);
    }

    private boolean isNameEncrypted(Boolean isEncrypt, String targetName) {
        if (isEncrypt) {
            return Objects.equals(this.name, NameCryptExecutor.ENCRYPT_STRING + targetName);
        }
        return Objects.equals(this.name, targetName);
    }

    private boolean isNameDecrypted(Boolean isDecrypt, String targetName) {
        if (isDecrypt) {
            return Objects.equals(this.name, NameCryptExecutor.DECRYPT_STRING + targetName);
        }
        return Objects.equals(this.name, targetName);
    }

    private boolean isPhoneEncrypted(Boolean isEncrypt, String targetPhone) {
        if (isEncrypt) {
            return Objects.equals(this.phone, PhoneCryptExecutor.ENCRYPT_STRING + targetPhone);
        }
        return Objects.equals(this.phone, targetPhone);
    }

    private boolean isPhoneDecrypted(Boolean isDecrypt, String targetPhone) {
        if (isDecrypt) {
            return Objects.equals(this.phone, PhoneCryptExecutor.DECRYPT_STRING + targetPhone);
        }
        return Objects.equals(this.phone, targetPhone);
    }


}
