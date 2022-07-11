package io.aiccj.mq;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liangwang
 * @date 2022/7/6
 * @description RockeMQ属性配置文件，提供访问RocketMQ服务器的访问Key、安全Key、服务器地址
 */
@Component
@Scope("singleton")
@Data
@Slf4j
public class RocketMQConsumerConfig {

	@Value("${spring.application.name}")
	private String applicationName;
	private final RocketMQProperties rocketMQProperties;
	private Integer maxReconsumeTimes;
	private Map<String, DefaultMQPushConsumer> consumers;

	public RocketMQConsumerConfig(RocketMQProperties rocketMQProperties) {
		consumers = new ConcurrentHashMap<>();
		maxReconsumeTimes = rocketMQProperties.getMaxReconsumeTimes();
		this.rocketMQProperties = rocketMQProperties;
	}


	/**
	 * 初始化mq消费者
	 * https://blog.csdn.net/weixin_34452850/article/details/82696084
	 *
	 * rocket默认消费次数
	 * 第几次重试 上次重试的间隔时间 第几次重试 上次重试的间隔时间
	 *  1 			10秒 			9 			7分钟
	 *  2 			30 秒 			10 			8 分钟
	 *  3 			1 分钟			11 			9 分钟
	 *  4 			2 分钟 			12 			10分钟
	 *  5 			3 分钟 			13 			20 分钟
	 *  6 			4 分钟 			14 			30 分钟
	 *  7 			5 分钟 			15 			1小时
	 *  8			6 分钟 			16 			2 小时
	 * @param topic
	 * @param tag
	 * @return
	 */
	public synchronized DefaultMQPushConsumer getConsumer(String topic, String tag) {
		String key = String.format("%s_%s_%s", applicationName, topic, tag);
		key = key.replaceAll("-", "_");
		log.info("初始化mq消费组: {}", key);
		DefaultMQPushConsumer consumer = consumers.get(key);
		if (consumer != null)
			return consumer;
		consumer=new DefaultMQPushConsumer(key);
		consumer.setNamesrvAddr(rocketMQProperties.getNameSrvAddress());

		/**
		 * CONSUME_FROM_LAST_OFFSET 默认策略，从该队列最尾开始消费，跳过历史消息
		 * CONSUME_FROM_FIRST_OFFSET 从队列最开始开始消费，即历史消息（还储存在broker的）全部消费一遍
		 */
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		consumer.setConsumerGroup(key);
		//默认20
		consumer.setConsumeThreadMin(20);
		consumer.setConsumeThreadMax(64);


		consumers.put(key, consumer);

		return consumer;
	}

	@PreDestroy
	public void destroy() {
		for (DefaultMQPushConsumer consumer : consumers.values()) {
			consumer.shutdown();
		}
	}
}
