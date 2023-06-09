package com.ecp.jces.exception;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseExceptionConstants {

	/**
	 * 成功
	 */
	public static final String SUCCESS = "000000";
	/**
	 * 服务正忙
	 */
	public static final String BASE_ERROR = "-999999";
	/**
	 * 未定义异常
	 */
	public static final String UNDEFINED_ERROR = "-999998";
	/**
	 * 请求错误
	 */
	public static final String REQUEST_ERROR = "-999997";
	/**
	 * 请求失败
	 */
	public static final String REQUEST_FAILED = "-999996";
	/**
	 * JSON异常
	 */
	public static final String JSON_PROCESSING_EXCEPTION = "-999995";
	/**
	 * 重复插入违反约束
	 */
	public static final String DUPLICATE_KEY_ERROR = "-999994";
	/**
	 * 数据库异常
	 */
	public static final String DATABASE_EXCEPTION = "-999993";
	/**
	 * 解析异常
	 */
	public static final String PARSE_EXCEPTION = "-999992";
	/**
	 * 非法异常
	 */
	public static final String ILLEGAL_EXCEPTION = "-999991";
	/**
	 * 类型转换异常
	 */
	public static final String NUMBER_FORMAT_EXCEPTION = "-999990";
	/**
	 * lock超时
	 */
	public static final String LOCK_TIMEOUT = "-999989";
	/**
	 * 参数为空
	 */
	public static final String PARAM_EMPTY = "-999988";
	/**
	 * 不是十进制
	 */
	public static final String NOT_DECIMAL = "-999987";
	/**
	 * jedis异常
	 */
	public static final String JEDIS_EXCEPTION = "-999986";
	/**
	 * 页面不存在
	 */
	public static final String PAGE_NO_FOUND = "-999985";
	/**
	 * 参数过长
	 */
	public static final String PARAM_TOO_MAX_LENGTH = "-999984";
	/**
	 * 参数过大
	 */
	public static final String PARAM_TOO_MAX = "-999983";
	/**
	 * 参数过小
	 */
	public static final String PARAM_TOO_MIN = "-999982";
	/**
	 * 参数格式不正确
	 */
	public static final String PARAM_FORMAT_ERROR = "-999981";
	/**
	 * 上传文件格式不正确
	 */
	public static final String UPFILE_FORMAT_ERROR = "-999980";
	/**
	 * 上传文件过大
	 */
	public static final String UPFILE_TOO_BIG = "-999979";

	// /**
	// * 身份验证错误
	// */
	// public static final String AUTHENTICATION_ERROR = "-900004";

	private final static Map<String, String> BASE_MAP = new HashMap<String, String>() {
		private static final long serialVersionUID = 3514837515809187420L;
		{
			put(SUCCESS, "成功");
			put(BASE_ERROR, "服务正忙");
			put(UNDEFINED_ERROR, "未定义异常");
			put(REQUEST_ERROR, "请求错误");
			put(REQUEST_FAILED, "请求失败");
			put(JSON_PROCESSING_EXCEPTION, "JSON结构异常");
			put(DUPLICATE_KEY_ERROR, "重复插入违反约束");
			put(DATABASE_EXCEPTION, "数据库异常");
			put(PARSE_EXCEPTION, "解析异常");
			put(ILLEGAL_EXCEPTION, "非法异常");
			put(NUMBER_FORMAT_EXCEPTION, "参数{0}类型转换异常");
			put(JSON_PROCESSING_EXCEPTION, "JSON异常");
			put(LOCK_TIMEOUT, "lock超时");
			put(PARAM_EMPTY, "参数{0}为空");
			put(NOT_DECIMAL, "不是十进制");
			put(JEDIS_EXCEPTION, "jedis异常");
			put(PARAM_TOO_MAX_LENGTH, "参数过长");
			put(PARAM_TOO_MAX, "参数过大");
			put(PARAM_TOO_MIN, "参数过小");
			put(PARAM_FORMAT_ERROR, "参数格式不正确");
			// put(AUTHENTICATION_ERROR, "身份验证错误");
			put(UPFILE_FORMAT_ERROR, "上传文件格式不正确");
			put(UPFILE_TOO_BIG, "上传文件过大");
		}
	};

	protected final static Map<String, String> getBaseMap() {
		return Collections.unmodifiableMap(BASE_MAP);
	}

	private final static Map<String, String> MSG_MAP = new HashMap<String, String>(BASE_MAP);

	public static String getMessage(String code, String... params) {
		String str = MSG_MAP.get(code);
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				StringBuffer sb = new StringBuffer("{");
				sb.append(i).append("}");
				str = StringUtils.replace(str, sb.toString(), params[i]);
			}
		}
		return str;
	}
}
