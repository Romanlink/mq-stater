package io.aiccj.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

/**
 * @Author liangwang
 * @create 2022/7/7 21:24
 * 默认单例
 */
@Slf4j
@Component
public class RocketMQPublisher implements AiccjEventPublisher {

	private RocketMQProducerConfig rocketMQProducerConfig;

	public RocketMQPublisher(RocketMQProducerConfig aRocketMQProducerConfig) {
		this.rocketMQProducerConfig = aRocketMQProducerConfig;
	}

	@Override
	public SendResult publish(String aTopic, String tag, AiccjEvent aRoxeEvent) {
		Message message = new Message(aTopic,tag, JSONObject.toJSONString(aRoxeEvent).getBytes());
		String _reason = null;SendResult _sendResult = null;
		try {
			_sendResult = rocketMQProducerConfig.getProducer().send(message);
		} catch (Exception e) {
			_reason = getReason(e);
			e.printStackTrace();
		} finally {
			log.info("[RocketMQPublisher] [publish] result : {}", _reason == null ? JSONObject.toJSONString(_sendResult) : _reason);
		}
		return _sendResult;
	}

	private String getReason(Exception e) {
		String s = JSONObject.toJSONString(e);
		return s.substring(0, s.length() > 1024 ? 1024 : s.length());
	}
	

}
