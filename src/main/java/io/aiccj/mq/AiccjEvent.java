package io.aiccj.mq;

import java.io.Serializable;

/**
 * @Author liangwang
 * @create 2022/5/7 4:14 下午
 */
public abstract class AiccjEvent implements Serializable {

    abstract public void validate();

}
