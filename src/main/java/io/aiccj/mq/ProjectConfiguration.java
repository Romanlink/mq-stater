package io.aiccj.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author liangwang
 * @create 2022/7/11 20:02
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(RocketMQProperties.class)
public class ProjectConfiguration {

    @Autowired
    RocketMQProperties rocketMQProperties;

}
