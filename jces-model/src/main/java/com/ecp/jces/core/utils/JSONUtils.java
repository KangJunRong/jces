package com.ecp.jces.core.utils;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.exception.FrameworkRuntimeException;
import org.apache.commons.io.Charsets;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * json 封装类
 *
 */
public class JSONUtils {

	// private static final Logger logger =
	// LoggerFactory.getLogger(JSONUtils.class);

	public final static String toJSONString() {
		return null;
	}

	public final static String toJSONString(Object object) {
		if (object == null) {
			return null;
		}
		return com.alibaba.fastjson.JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
	}

	public final static <T> T parseObject(String text, Class<T> clazz) {
		return com.alibaba.fastjson.JSON.parseObject(text, clazz);
	}

	public final static <T> T parseObject(String text, TypeReference<T> type) {
		return com.alibaba.fastjson.JSON.parseObject(text, type);
	}

	public final static <K, V> Map<K, V> parseObject(String text, Class<K> clazzK, Class<V> clazzV) {
		return com.alibaba.fastjson.JSON.parseObject(text, new TypeReference<Map<K, V>>((Type) clazzK, (Type) clazzV) {
		});
	}

	public final static <T> List<T> parseArray(String text, Class<T> clazz) {
		return com.alibaba.fastjson.JSON.parseArray(text, clazz);
	}

	public final static JSONObjects parseObject(String text) {
		JSONObjects j = com.alibaba.fastjson.JSON.parseObject(text, JSONObjects.class);
		return (JSONObjects) j;
	}

	public final static byte[] toJSONBytes(Object object) throws FrameworkRuntimeException {
		String jsonString = toJSONString(object);
		byte[] bytes = jsonString.getBytes(Charsets.toCharset(StandardCharsets.UTF_8));
		return bytes;
		// byte[] bytes = null;
		// try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
		// ObjectOutputStream oo = new ObjectOutputStream(bo);) {
		// oo.writeObject(object);
		// bytes = bo.toByteArray();
		// bo.close();
		// oo.close();
		// } catch (Exception e) {
		// logger.error(LogMsg.to("ex", e));
		// throw new FrameworkRuntimeException(BaseExceptionConstants.ILLEGAL_EXCEPTION,
		// BaseExceptionConstants.getMessage(BaseExceptionConstants.ILLEGAL_EXCEPTION));
		// }
		// return bytes;
	}
}
