package io.aiccj.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.scheduling.annotation.Async;

/**
 * @Author liangwang
 * @create 2022/7/8 14:42
 * 这里的方法全部为异步处理，避免主流程阻塞
 */
@Slf4j
@Async(value="asyncServiceExecutor")
public class MqService {

    RocketMQSubscriber rocketMQSubscriber;

    RocketMQPublisher rocketMQPublisher;

    public <E extends AiccjEvent> void subscribe(String aTopic, String aTags,
                                                 AiccjEventSubscribeListener<E> aListener){
        rocketMQSubscriber.subscriber(aTopic, aTags, aListener);
    }

    public SendResult send(String topic, String tag, AiccjEvent event){
        return rocketMQPublisher.publish(topic, tag, event);
    }

    public void init(RocketMQSubscriber aRocketMQSubscriber, RocketMQPublisher aRocketMQPublisher) {
        this.rocketMQSubscriber = aRocketMQSubscriber;
        this.rocketMQPublisher = aRocketMQPublisher;
    }
}
