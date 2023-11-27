package com.fuHoi.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @title: WeChatAccountConfig
 * @Author Xie
 * @Date: 2023/7/3 20:14
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatAccountConfig {
    private String mpAppId;

    private String mpAppSecret;
}
