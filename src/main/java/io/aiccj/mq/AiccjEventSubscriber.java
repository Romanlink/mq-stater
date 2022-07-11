package io.aiccj.mq;

/**
 * @Author liangwang
 * @create 2022/7/7 21:29
 */
public interface AiccjEventSubscriber {
    /**
     * 订阅普通消息
     */
    <E extends AiccjEvent> void subscriber(String aTopic, String aTags, AiccjEventSubscribeListener<E> aListener);
}
