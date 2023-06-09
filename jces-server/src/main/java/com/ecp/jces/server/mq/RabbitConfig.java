package com.ecp.jces.server.mq;
import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.mq.MqMsgInfo;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // 创建 Fanout Exchange
    @Bean
    public FanoutExchange Exchange() {
        return new FanoutExchange(ConstantCode.EXCHANGE,
                true,  // durable: 是否持久化
                false);  // exclusive: 是否排它
    }

}
