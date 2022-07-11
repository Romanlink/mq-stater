package io.aiccj.mq;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author liangwang
 * @create 2022/7/7 21:24
 * 默认单例
 */
@Setter
@Getter
@Configuration
@ConditionalOnProperty(prefix = "mq", name = "enable", matchIfMissing = true)
@EnableAsync
@Slf4j
public class RocketMQProperties {

    @Value("${mq.server.address}")
    private String nameSrvAddress;

    @Value("${mq.reconsume.times:10}")
    private Integer maxReconsumeTimes;

    @Value("${mq.default.producer_group:null}")
    private String defaultProducerGroup;


    @Value("${mq.worker.thread.core_pool_size:10}")
    private int corePoolSize;
    @Value("${mq.worker.thread.max_pool_size:100}")
    private int maxPoolSize;
    @Value("${mq.worker.thread.queue_capacity:1000}")
    private int queueCapacity;
    @Value("${mq.worker.thread.name.prefix:name_pre}")
    private String namePrefix;

    @Bean(name = "asyncServiceExecutor")
    public Executor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(namePrefix);

        executor.setThreadGroup(new ThreadGroup("roman-async-thread-"));
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }

    @Bean
    public MqService mqService(RocketMQProperties rocketMQProperties){
        RocketMQConsumerConfig rocketMQConsumerConfig =
                new RocketMQConsumerConfig(rocketMQProperties);
        RocketMQSubscriber rocketMQSubscriber = new RocketMQSubscriber(rocketMQConsumerConfig);


        RocketMQProducerConfig rocketMQProducerConfig = new RocketMQProducerConfig(rocketMQProperties);
        RocketMQPublisher rocketMQPublisher = new RocketMQPublisher(rocketMQProducerConfig);

        MqService mqService = new MqService();
        mqService.init(rocketMQSubscriber, rocketMQPublisher);

        return mqService;
    }
}