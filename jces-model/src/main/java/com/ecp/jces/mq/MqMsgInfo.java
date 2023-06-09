package com.ecp.jces.mq;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @program: jces-engine
 * @description: 队列信息
 * @author: KJR
 * @create: 2023-03-01 17:55
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class MqMsgInfo implements Serializable {
    private String engineId;
    private String msgType;
    private Object data;
}
