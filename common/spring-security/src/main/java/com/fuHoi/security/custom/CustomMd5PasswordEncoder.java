package com.fuHoi.security.custom;

import com.fuHoi.common.utils.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @title: CustomMd5PasswordEncoder
 * @Author Xie
 * @Date: 2023/6/8 19:51
 * @Version 1.0
 */

/**
 * <p>
 * 密码处理
 * <p/>
 *
 */
@Component
public class CustomMd5PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return MD5.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(rawPassword.toString()));
    }
}
