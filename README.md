# Mq-stater

## 快速搭建

#### 加入依赖

```xml
<mirror>
         <groupId>com.aiccj</groupId>
  		   <artifactId>mq-stater</artifactId>
         <version>0.0.1-SNAPSHOT</version>
</mirror>
```

1. 具备运行环境：JDK1.8、Maven3.0+。
2. 导入ide前，安装lombok插件



#### 加入configuration配置

```yaml
mq:
  server:
    address: mq.yourdomain.com:9876
  default:
    producer_group: default_producer_group
  worker:
    thread:
      core_pool_size: 10
      max_pool_size: 40
      queue_capacity: 99999
      name:
        prefix: mq-worker-
```

1. 除了`mq.server.address`以外，其他配置均可使用默认值



#### 开始使用

发布事件

```
@Data
class DemoEvent extends RoxeEvent {
    private String orderId;
    @Override
    public void validate() {}
}

@Autowired
private MqService mqService;

public void test() {
				DemoEvent demoEvent = new DemoEvent();
        demoEvent.setOrderId("000000101asdbfsdhfja");
        SendResult send = mqService.send("my_topic", "my_tag", demoEvent);
        system.out.println(send);
}
```



订阅事件

```java
@Configuration
public class Config {
    @Bean
    public Object tmpBean(MqService mqService) {
        MyConsumer myConsumer = new MyConsumer(mqService);
        myConsumer.init();
        return new Object();
    }
    
    class MyConsumer {

    @Autowired
    private MqService mqService;

    public MyConsumer(MqService mqService) {
        this.mqService = mqService;
    }

    public void init() {
        mqService.subscribe("my_topic", "my_tag", new AiccjEventSubscribeListener<DemoEvent>() {
            @Override
            public void consume(DemoEvent event) {
               //todo for business logic
            }

            @Override
            public Class<DemoEvent> getType() {
                return DemoEvent.class;
            }
        });
    }
}
```



说明：

1. MqService默认异步线程处理，如果相关逻辑有事物要求，请谨慎使用
