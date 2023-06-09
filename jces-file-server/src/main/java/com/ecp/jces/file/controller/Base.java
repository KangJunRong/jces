package com.ecp.jces.file.controller;
import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.core.utils.pagination.Pagination;

import java.util.HashMap;
import java.util.Map;

public class Base {

	public String respString(String code, String msg, Pagination<?> pagination) {
		Map<Object, Object> params = new HashMap<Object, Object>(3);
		params.put("code", String.valueOf(code));
		params.put("msg", msg);
		if (pagination != null) {
			params.put("data", pagination.getData());
			params.put("count", pagination.getTotalPageSize());
		} else {
			params.put("data", null);
		}
		return JSONUtils.toJSONString(params);
	}

	public String respString(String code, String msg, Object object) {
		Map<Object, Object> params = new HashMap<Object, Object>(3);
		params.put("code", String.valueOf(code));
		params.put("msg", msg);
		if (object != null) {
			params.put("data", object);
		} else {
			params.put("data", null);
		}
		return JSONUtils.toJSONString(params);
	}



}
