package io.aiccj.mq;

/**
 * @Author liangwang
 * @create 2022/7/7 21:30
 */
public interface AiccjEventSubscribeListener<E extends AiccjEvent> {

    void consume(E event);

    Class<E> getType();
}
