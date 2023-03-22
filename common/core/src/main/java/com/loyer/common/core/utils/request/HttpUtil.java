package com.loyer.common.core.utils.request;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.loyer.common.core.inherit.CommonInputStreamResource;
import com.loyer.common.core.inherit.TextMessageConverter;
import com.loyer.common.core.utils.document.FileUtil;
import com.loyer.common.core.utils.reflect.EntityUtil;
import lombok.SneakyThrows;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Http请求工具类
 *
 * @author kuangq
 * @date 2019-08-28 17:46
 */
@SuppressWarnings("unused")
public class HttpUtil {

    public static RestTemplate restTemplate = getRestTemplate();

    /**
     * 创建RestTemplate
     *
     * @author kuangq
     * @date 2019-12-10 11:13
     */
    private static RestTemplate getRestTemplate() {
        try {
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            //全部信任，不做身份鉴定
            sslContextBuilder.loadTrustMaterial(null, (X509Certificate[] x509Certificates, String str) -> true);
            //客户端支持SSLv2Hello/SSLv3/TLSv1/TLSv1.2
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
            //为自定义连接器注册http与https
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", new PlainConnectionSocketFactory()).register("https", socketFactory).build();
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
            //最大连接数配置
            connectionManager.setMaxTotal(1000);
            connectionManager.setDefaultMaxPerRoute(1000);
            CloseableHttpClient closeableHttpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).setConnectionManager(connectionManager).setConnectionManagerShared(true).build();
            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            //超时配置
            httpRequestFactory.setHttpClient(closeableHttpClient);
            httpRequestFactory.setReadTimeout(10000);
            httpRequestFactory.setConnectTimeout(5000);
            httpRequestFactory.setConnectionRequestTimeout(1000);
            //设置序列化
            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
            List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters().stream().filter(item -> !(item instanceof MappingJackson2HttpMessageConverter)).collect(Collectors.toList());
            messageConverters.add(new FastJsonHttpMessageConverter());
            messageConverters.add(new TextMessageConverter());
            restTemplate.setMessageConverters(messageConverters);
            return restTemplate;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 拼接url
     *
     * @author kuangq
     * @date 2021-09-15 23:16
     */
    private static String getUrl(String url, Map<String, Object> uriVariables) {
        if (uriVariables == null) {
            return url;
        }
        StringBuilder stringBuilder = new StringBuilder(url);
        stringBuilder.append("?");
        for (Map.Entry<String, Object> entry : uriVariables.entrySet()) {
            stringBuilder.append(String.format("%s=%s&", entry.getKey(), entry.getValue()));
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    /**
     * 封装请求头/请求实体/请求内容
     *
     * @author kuangq
     * @date 2022-03-08 16:15
     */
    private static <T> HttpEntity<?> getHttpEntity(MediaType mediaType, Map<String, String> headers, Object uriVariables) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (mediaType != null) {
            httpHeaders.setContentType(mediaType);
        }
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpHeaders.add(entry.getKey(), entry.getValue());
            }
        }
        if (uriVariables != null) {
            if (mediaType != null && mediaType == MediaType.APPLICATION_FORM_URLENCODED) {
                MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
                multiValueMap.setAll(EntityUtil.entityToMap(uriVariables));
                return new HttpEntity<>(multiValueMap, httpHeaders);
            }
            return new HttpEntity<>(uriVariables, httpHeaders);
        }
        return new HttpEntity<>(httpHeaders);
    }

    /**
     * 无参get请求
     *
     * @author kuangq
     * @date 2022-03-08 16:35
     */
    public static <T> T doGet(String url, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(null, null, null), responseType).getBody();
    }

    /**
     * 无参get请求
     *
     * @author kuangq
     * @date 2022-03-08 16:36
     */
    public static <T> T doGet(String url, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(null, null, null), typeReference).getBody();
    }

    /**
     * 有header的get请求
     *
     * @author kuangq
     * @date 2022-03-08 16:37
     */
    public static <T> T doGet(String url, Map<String, String> headers, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(null, headers, null), responseType).getBody();
    }

    /**
     * 有header的get请求
     *
     * @author kuangq
     * @date 2022-03-08 16:38
     */
    public static <T> T doGet(String url, Map<String, String> headers, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(null, headers, null), typeReference).getBody();
    }

    /**
     * 有参的get请求
     *
     * @author kuangq
     * @date 2022-03-08 16:38
     */
    public static <T> T doGet(String url, Object uriVariables, Class<T> responseType) {
        return restTemplate.exchange(getUrl(url, EntityUtil.entityToMap(uriVariables)), HttpMethod.GET, getHttpEntity(null, null, null), responseType).getBody();
    }

    /**
     * 有参的get请求
     *
     * @author kuangq
     * @date 2022-03-08 16:38
     */
    public static <T> T doGet(String url, Object uriVariables, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(getUrl(url, EntityUtil.entityToMap(uriVariables)), HttpMethod.GET, getHttpEntity(null, null, null), typeReference).getBody();
    }

    /**
     * 有header，有参的get请求
     *
     * @author kuangq
     * @date 2022-03-08 16:39
     */
    public static <T> T doGet(String url, Map<String, String> headers, Object uriVariables, Class<T> responseType) {
        return restTemplate.exchange(getUrl(url, EntityUtil.entityToMap(uriVariables)), HttpMethod.GET, getHttpEntity(null, headers, null), responseType).getBody();
    }

    /**
     * 有header，有参的get请求
     *
     * @author kuangq
     * @date 2022-03-08 16:39
     */
    public static <T> T doGet(String url, Map<String, String> headers, Object uriVariables, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(getUrl(url, EntityUtil.entityToMap(uriVariables)), HttpMethod.GET, getHttpEntity(null, headers, null), typeReference).getBody();
    }

    /**
     * 无参post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:39
     */
    public static <T> T doPost(String url, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(null, null, null), responseType).getBody();
    }

    /**
     * 无参post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:40
     */
    public static <T> T doPost(String url, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(null, null, null), typeReference).getBody();
    }

    /**
     * 有header的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:40
     */
    public static <T> T doPost(String url, Map<String, String> headers, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(null, headers, null), responseType).getBody();
    }

    /**
     * 有header的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:40
     */
    public static <T> T doPost(String url, Map<String, String> headers, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(null, headers, null), typeReference).getBody();
    }

    /**
     * 有参，form提交的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:40
     */
    public static <T> T doPostForm(String url, Object uriVariables, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(MediaType.APPLICATION_FORM_URLENCODED, null, uriVariables), responseType).getBody();
    }

    /**
     * 有参，form提交的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:41
     */
    public static <T> T doPostForm(String url, Object uriVariables, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(MediaType.APPLICATION_FORM_URLENCODED, null, uriVariables), typeReference).getBody();
    }

    /**
     * 有header，有参，form提交的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:41
     */
    public static <T> T doPostForm(String url, Map<String, String> headers, Object uriVariables, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(MediaType.APPLICATION_FORM_URLENCODED, headers, uriVariables), responseType).getBody();
    }

    /**
     * 有header，有参，form提交的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:41
     */
    public static <T> T doPostForm(String url, Map<String, String> headers, Object uriVariables, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(MediaType.APPLICATION_FORM_URLENCODED, headers, uriVariables), typeReference).getBody();
    }

    /**
     * 有参，json提交的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:41
     */
    public static <T> T doPostJson(String url, Object uriVariables, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(MediaType.APPLICATION_JSON, null, uriVariables), responseType).getBody();
    }

    /**
     * 有参，json提交的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:41
     */
    public static <T> T doPostJson(String url, Object uriVariables, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(MediaType.APPLICATION_JSON, null, uriVariables), typeReference).getBody();
    }

    /**
     * 有header，有参，json提交的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:42
     */
    public static <T> T doPostJson(String url, Map<String, String> headers, Object uriVariables, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(MediaType.APPLICATION_JSON, headers, uriVariables), responseType).getBody();
    }

    /**
     * 有header，有参，json提交的post请求
     *
     * @author kuangq
     * @date 2022-03-08 16:42
     */
    public static <T> T doPostJson(String url, Map<String, String> headers, Object uriVariables, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(url, HttpMethod.POST, getHttpEntity(MediaType.APPLICATION_JSON, headers, uriVariables), typeReference).getBody();
    }

    /**
     * 自定义method的请求，如put/delete
     *
     * @author kuangq
     * @date 2022-03-08 16:43
     */
    public static <T> T send(String url, HttpMethod httpMethod, MediaType mediaType, Map<String, String> headers, Object uriVariables, Class<T> responseType) {
        return restTemplate.exchange(url, httpMethod, getHttpEntity(mediaType, headers, uriVariables), responseType).getBody();
    }

    /**
     * 自定义method的请求，如put/delete
     *
     * @author kuangq
     * @date 2022-03-08 16:43
     */
    public static <T> T send(String url, HttpMethod httpMethod, MediaType mediaType, Map<String, String> headers, Object uriVariables, ParameterizedTypeReference<T> typeReference) {
        return restTemplate.exchange(url, httpMethod, getHttpEntity(mediaType, headers, uriVariables), typeReference).getBody();
    }

    /**
     * 上传文件
     *
     * @author kuangq
     * @date 2023-03-22 14:47
     */
    @SneakyThrows
    public static <T> T upload(String url, String fileName, String base64, Class<T> responseType) {
        InputStream inputStream = FileUtil.toInputStream(base64);
        CommonInputStreamResource commonInputStreamResource = new CommonInputStreamResource(inputStream, fileName);
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("media", commonInputStreamResource);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multiValueMap, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType).getBody();
    }
}