package com.loyer.common.core.utils.request;

import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.utils.StringUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Https请求工具类（基于HttpsURLConnection实现）
 *
 * @author kuangq
 * @date 2021-02-23 15:18
 */
public class HttpsUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpsUtil.class);

    /**
     * 请求
     *
     * @author kuangq
     * @date 2021-02-23 16:29
     */
    @SneakyThrows
    public static String request(String url, String method, String params, Map<String, String> headers) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            //创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] trustManagers = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            //从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            //创建Https请求连接
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(url).openConnection();
            //设置请求header
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(httpsURLConnection::setRequestProperty);
            }
            //设置SSL
            httpsURLConnection.setSSLSocketFactory(ssf);
            //允许输入
            httpsURLConnection.setDoInput(true);
            //允许输出
            httpsURLConnection.setDoOutput(true);
            //不允许缓存
            httpsURLConnection.setUseCaches(false);
            //设置请求方式（GET/POST）
            httpsURLConnection.setRequestMethod(method.toUpperCase());
            //如果是GET请求
            if (HttpMethod.GET.toString().equalsIgnoreCase(method)) {
                httpsURLConnection.connect();
            }
            //当有数据需要提交时
            if (StringUtils.isNotBlank(params)) {
                outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(StringUtil.decode(params));
                outputStream.close();
            }
            //将请求返回的输入流转换成字符串
            inputStream = httpsURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            String str;
            StringBuilder stringBuilder = new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            //断开连接
            httpsURLConnection.disconnect();
            //反序列化处理转义符，便于输出
            String response = StringEscapeUtils.unescapeJava(stringBuilder.toString());
            logger.info("【https请求出参】{}：{}", httpsURLConnection.getResponseCode(), response);
            return stringBuilder.toString();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                logger.error("【{}】{}", HintEnum.HINT_1011.getMsg(), e.getMessage());
            }
        }
    }

    public static class MyX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}