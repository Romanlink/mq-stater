package io.aiccj.mq;

import org.apache.rocketmq.client.producer.SendResult;

/**
 * @Author liangwang
 * @create 2022/5/7 4:13 下午
 */
public interface AiccjEventPublisher {

    /**
     * 普通同步消息发送，等待服务器返回确认消息，
     * 若不考虑本地数据库事物，比较可靠，可以用该方法发送。
     */
    SendResult publish(String aTopic, String tag, AiccjEvent aRoxeEvent);
    
}
