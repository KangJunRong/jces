package com.ecp.jces.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BodyMap {

	public BodyMap() {
		super();
	}

	public BodyMap(String key, String val) {
		super();
		this.key = key;
		this.val = val;
	}

	private String key;

	private String val;

}
