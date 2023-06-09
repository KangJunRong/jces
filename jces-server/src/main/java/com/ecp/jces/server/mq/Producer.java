package com.ecp.jces.server.mq;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.mq.MqMsgInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void syncSend(MqMsgInfo info) {
        // 同步发送消息
        rabbitTemplate.convertAndSend(ConstantCode.EXCHANGE, null, JSONUtils.toJSONString(info));
    }

}
