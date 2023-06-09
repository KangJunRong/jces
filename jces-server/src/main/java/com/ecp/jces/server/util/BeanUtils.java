package com.ecp.jces.server.util;

import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.exception.BaseExceptionConstants;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.global.LogMsg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @date 2019年5月8日
 * @description 对象复制类
 */
public final class BeanUtils {

	private static final Logger LOGGER = LogManager.getLogger(BeanUtils.class);

	private BeanUtils() {

	}

	public static <S, T> T copy(S source, T to) throws FrameworkRuntimeException {
		org.springframework.beans.BeanUtils.copyProperties(source, to);
		return to;
	}

	public static <S, T> T copy(S source, Class<T> to) throws FrameworkRuntimeException {
		try {
			if (source == null) {
				return null;
			}
			// 暂时使用此复制方法进行对象的嵌套复制
			return JSONUtils.parseObject(JSONUtils.toJSONString(source), to);
		} catch (IllegalArgumentException | SecurityException e) {
			LOGGER.error(LogMsg.to("ex", e));
			throw new FrameworkRuntimeException(BaseExceptionConstants.ILLEGAL_EXCEPTION,
					BaseExceptionConstants.getMessage(BaseExceptionConstants.ILLEGAL_EXCEPTION), e);
		}
	}

	public static <S, T> T copyProperties(S source, Class<T> to) throws FrameworkRuntimeException {
		try {
			if (source == null) {
				return null;
			}
			T t = to.newInstance();
			org.springframework.beans.BeanUtils.copyProperties(source, t);
			return t;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
			LOGGER.error(LogMsg.to("ex", e));
			throw new FrameworkRuntimeException(BaseExceptionConstants.ILLEGAL_EXCEPTION,
					BaseExceptionConstants.getMessage(BaseExceptionConstants.ILLEGAL_EXCEPTION), e);
		}
	}

	public static <S, T> List<T> copy(List<S> sources, Class<T> to) throws FrameworkRuntimeException {
		try {
			if (sources == null) {
				return new ArrayList<>();
			}
			List<T> ts = new ArrayList<>(sources.size());
			for (S source : sources) {
				T t = BeanUtils.copy(source, to);
				ts.add(t);
			}
			return ts;
		} catch (IllegalArgumentException | SecurityException e) {
			LOGGER.error(LogMsg.to("ex", e));
			throw new FrameworkRuntimeException(BaseExceptionConstants.ILLEGAL_EXCEPTION,
					BaseExceptionConstants.getMessage(BaseExceptionConstants.ILLEGAL_EXCEPTION), e);
		}
	}

	public static <K, S, T> Map<K, T> copy(Map<K, S> maps, Class<T> to) throws FrameworkRuntimeException {
		try {
			if (maps == null) {
				return null;
			}
			Map<K, T> ms = new HashMap<>(maps.size());
			for (Entry<K, S> entry : maps.entrySet()) {
				K k = entry.getKey();
				S source = entry.getValue();
				T t = BeanUtils.copy(source, to);
				ms.put(k, t);
			}
			return ms;
		} catch (IllegalArgumentException | SecurityException e) {
			LOGGER.error(LogMsg.to("ex", e));
			throw new FrameworkRuntimeException(BaseExceptionConstants.ILLEGAL_EXCEPTION,
					BaseExceptionConstants.getMessage(BaseExceptionConstants.ILLEGAL_EXCEPTION), e);
		}
	}

	public static <K, S, T> Map<K, List<T>> copyMapList(Map<K, List<S>> maps, Class<T> to)
			throws FrameworkRuntimeException {
		try {
			if (maps == null) {
				return null;
			}
			Map<K, List<T>> ms = new HashMap<>(maps.size());
			for (Entry<K, List<S>> entry : maps.entrySet()) {
				K k = entry.getKey();
				List<S> sources = entry.getValue();
				List<T> ts = BeanUtils.copy(sources, to);
				ms.put(k, ts);
			}
			return ms;
		} catch (IllegalArgumentException | SecurityException e) {
			LOGGER.error(LogMsg.to("ex", e));
			throw new FrameworkRuntimeException(BaseExceptionConstants.ILLEGAL_EXCEPTION,
					BaseExceptionConstants.getMessage(BaseExceptionConstants.ILLEGAL_EXCEPTION), e);
		}
	}

	@SafeVarargs
	public static <T> List<T> list(T... ts) throws FrameworkRuntimeException {
		// 传递null,所以返回null
		if (ts == null || ts.length == 0) {
			return new ArrayList<>();
		}
		List<T> list = new ArrayList<>(ts.length);
		for (T t : ts) {
			list.add(t);
		}
		return list;
	}

}
