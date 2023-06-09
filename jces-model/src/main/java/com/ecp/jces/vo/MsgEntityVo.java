package com.ecp.jces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

//表不存在
@Data
@EqualsAndHashCode(callSuper = false)
public class MsgEntityVo extends BaseVo {

	private String code;
	private String msg;
	private Object data;

	private String reqData;

}
