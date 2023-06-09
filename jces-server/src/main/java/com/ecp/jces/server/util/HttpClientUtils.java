package com.ecp.jces.server.util;

import com.alibaba.fastjson.JSON;

import com.ecp.jces.core.utils.JSONUtils;
import com.ecp.jces.vo.MsgEntityVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class HttpClientUtils extends HttpConnectionManager {

    private static final Logger LOGGER = LogManager.getLogger(HttpClientUtils.class);

    protected static final int TIMEOUT = 30000;

    public static MsgEntityVo post(HttpPost httppost, String params, int timeout) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = getHttpClient();

        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .build();// 设置请求和传输超时时间
        httppost.setConfig(requestConfig);

        // 设置header
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Version", "v1.0");
        CloseableHttpResponse response = null;
        try {
            StringEntity stringEntity = new StringEntity(params, "utf-8");
            httppost.setEntity(stringEntity);
            response = httpclient.execute(httppost);
            return responseResult(response);
        } catch (IOException e) {
            LOGGER.error("IO异常:" + e.getMessage());
            LOGGER.error(httppost.getURI() + " 请求异常,请求参数为:" + params);
            return null;
        } finally {
            // 关闭连接,释放资源
            responseClose(response);
        }
    }

    public static MsgEntityVo post(HttpPost httppost, Map<String, Object> params, int timeout) {
        return post(httppost, JSON.toJSONString(params), timeout);
    }

    public static String postSimple(HttpPost httppost, String params, int timeout) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = getHttpClient();

        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .build();// 设置请求和传输超时时间
        httppost.setConfig(requestConfig);

        // 设置header
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Version", "v1.0");
        CloseableHttpResponse response = null;
        try {
            StringEntity stringEntity = new StringEntity(params, "utf-8");
            httppost.setEntity(stringEntity);
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.error(JSON.toJSONString(params));
            return null;
        } finally {
            // 关闭连接,释放资源
            responseClose(response);
        }
    }


    private static MsgEntityVo responseResult(CloseableHttpResponse response) {
        MsgEntityVo iotMsgEntity = new MsgEntityVo();
        if (response != null && HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    String result = EntityUtils.toString(entity, "UTF-8");
                    if (StringUtils.isNotBlank(result)) {
                        iotMsgEntity = JSONUtils.parseObject(result, MsgEntityVo.class);
                    }
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return iotMsgEntity;
    }

    private static String getUrlParams(Map<String, String> params, String charset)
            throws IOException {
        if (params == null) {
            return "";
        }

        return "?" + EntityUtils.toString(getEntity(params, charset));
    }

    private static UrlEncodedFormEntity getEntity(Map<String, String> params, String charset)
            throws UnsupportedEncodingException {
        List<NameValuePair> formparams = new ArrayList<>();
        for (Map.Entry<String, String> p : params.entrySet()) {
            formparams.add(new BasicNameValuePair(p.getKey(), p.getValue()));
        }
        return new UrlEncodedFormEntity(formparams, charset);
    }

    // 多播请求 get
    public static String get(String url, Map<String, String> params, Header[] headers, int timeout) {

        // 创建
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(url + getUrlParams(params, "UTF-8"));
        } catch (ParseException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.error(JSON.toJSONString(params));
            return null;
        }
        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .build();// 设置请求和传输超时时间
        httpGet.setConfig(requestConfig);
        // 设置header
        if (headers != null) {
            for (Header header : headers) {
                httpGet.setHeader(header);
            }
        }

        httpGet.setHeader("accept", "application/json");
        httpGet.setHeader("Content-Type", "application/json");
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        try {
            // 创建默认的httpClient实例.
            httpclient = getHttpClient();
            response = httpclient.execute(httpGet);
            if (response != null && 200 == response.getStatusLine().getStatusCode()) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity, "UTF-8");
                    LOGGER.debug("Response content: " + result);
                    return result;
                }
                LOGGER.error("Response status: " + response.getStatusLine());
            }
            LOGGER.error(JSON.toJSONString(params));
            return null;
        } catch (IOException e) {
            LOGGER.error(url + " 请求异常,请求参数为:" + JSON.toJSONString(params));
            return null;
        } finally {
            // 关闭连接,释放资源
            responseClose(response);
        }
    }

    public static void responseClose(CloseableHttpResponse response){
        try {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
                response.close();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
