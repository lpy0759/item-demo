package com.lpy.config;

import org.springframework.context.annotation.Configuration;

/**
 * Seata配置类
 * 在Spring Boot 2.7.18中，大部分配置都可以通过application.yml完成
 * 这个类主要用于一些自定义配置
 */
@Configuration
public class SeataConfig {

    // 如果需要自定义数据源代理，可以在这里配置
    // 但通常使用自动配置即可

}
