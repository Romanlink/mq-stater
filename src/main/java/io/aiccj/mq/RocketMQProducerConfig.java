package io.aiccj.mq;

import lombok.Data;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @Author liangwang
 * @create 2022/7/7 21:24
 * 默认单例
 */
@Component
@Data
public class RocketMQProducerConfig {

	private static final String DEFAULT_GROUP_SUFFIX = "_default_group";
	private static final String DEFAULT_GROUP_PREFIX = "default_application_";

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private RocketMQProperties rocketMQProperties;

	private DefaultMQProducer producer;

	public DefaultMQProducer getProducer() {
		if (producer == null) {
			init();
		}
		return producer;
	}

	public RocketMQProducerConfig(RocketMQProperties rocketMQProperties) {
		this.rocketMQProperties = rocketMQProperties;
	}

	public void init() {
		producer=new DefaultMQProducer(getDefaultGroup());
		producer.setNamesrvAddr(rocketMQProperties.getNameSrvAddress());
		producer.setVipChannelEnabled(false);
		try {
			producer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}

	private String getDefaultGroup() {

		String appName = applicationName == null ? DEFAULT_GROUP_PREFIX : applicationName.replaceAll("-", "_")+"_";

		String defaultProducerGroup = rocketMQProperties.getDefaultProducerGroup();
		String suffix = defaultProducerGroup == null ?
				DEFAULT_GROUP_SUFFIX : defaultProducerGroup.replaceAll("-","_");

		return appName +suffix;
	}

	@PreDestroy
	public void destroy() {
		if (producer != null)
			producer.shutdown();
	}
}
