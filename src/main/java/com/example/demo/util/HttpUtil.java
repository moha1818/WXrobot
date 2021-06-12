package com.example.demo.util;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.pentaho.di.core.SQLStatement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * HTTP工具类
 * 发送http/https协议get/post请求，发送map，json，xml，txt数据
 * @author happyqing
 * @since 2017-04-08
 */
public final class HttpUtil {
    /**
     * 执行一个http/https get请求，返回请求响应的文本数据
     *
     * @param url		请求的URL地址，可以带参数?param1=a&parma2=b
     * @param headerMap	请求头参数map，可以为null
     * @param paramMap	请求参数map，可以为null
     * @param charset	字符集
     * @param pretty	是否美化
     * @return 			返回请求响应的文本数据
     */
    public static String doGet(String url, Map<String, String> headerMap, Map<String, String> paramMap, String charset, boolean pretty) {
        //StringBuffer contentSb = new StringBuffer();
        String responseContent = "";
        // http客户端
        CloseableHttpClient httpclient = null;
        // Get请求
        HttpGet httpGet = null;
        // http响应
        CloseableHttpResponse response = null;
        try {

//            if(url.startsWith("https")){
//                httpclient = HttpsSSLClient.createSSLInsecureClient();
//            } else {
//
//            }
            httpclient = HttpClients.createDefault();

            // 设置参数
            if (paramMap != null) {
                List <NameValuePair> nvps = new ArrayList <NameValuePair>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                url = url + "?" + URLEncodedUtils.format(nvps, charset);
            }

            // Get请求
            httpGet = new HttpGet(url); // HttpUriRequest httpGet

            // 设置header
            if (headerMap != null) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }

            // 发送请求，返回响应
            response = httpclient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, charset);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } catch (ParseException e) {
        } finally {
            try {
                if(response!=null){
                    response.close();
                }
                if(httpGet!=null){
                    httpGet.releaseConnection();
                }
                if(httpclient!=null){
                    httpclient.close();
                }
            } catch (IOException e) {
            }
        }
        return responseContent;
    }


    public static String doPost(String url, String jsonObject) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        String response = null;

        try {
            StringEntity s = new StringEntity(jsonObject,"UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                response = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public static void main(String[] args) {

    }

}