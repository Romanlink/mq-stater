package io.aiccj.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @Author liangwang
 * @create 2022/7/7 21:24
 * 默认单例
 */
@Slf4j
@Component
public class RocketMQSubscriber implements AiccjEventSubscriber {

	private RocketMQConsumerConfig rocketMQConsumerConfig;


	public RocketMQSubscriber(RocketMQConsumerConfig rocketMQConsumerConfig) {
		this.rocketMQConsumerConfig = rocketMQConsumerConfig;
	}

	@Override
	public <E extends AiccjEvent> void subscriber(String aTopic, String aTags,
												  AiccjEventSubscribeListener<E> aListener) {
		DefaultMQPushConsumer consumer = rocketMQConsumerConfig.getConsumer(aTopic, aTags);
		try {
			consumer.subscribe(aTopic, aTags);
			consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
				long start = System.currentTimeMillis();
				MessageExt messageExt = list.get(0);
				boolean _s = false;String reason = null;
				try {
					String s = new String(messageExt.getBody(), StandardCharsets.UTF_8);
					log.debug("收到mq消息, topic:{}, tag:{}, body:{}", aTopic, aTags, s);
					E e = JSONObject.parseObject(s, aListener.getType());
					e.validate();
					aListener.consume(e);
					_s = true;
				} catch (Exception e) {
					reason = getReason(e);
					log.warn("消费未成功 :{}", reason);
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;//ACK机制，消费失败，触发RocketMQ 重发消息
				}
				finally {
					log.info("topic:{}, tag:{} 消费成功-耗时 {} ms", aTopic, aTags,  System.currentTimeMillis() - start);
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; //ACK机制，消费成功
			});
			consumer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}

	private String getReason(Exception e) {
		String s = JSONObject.toJSONString(e);
		return s.substring(0, s.length() > 1024 ? 1024 : s.length());
	}
}
