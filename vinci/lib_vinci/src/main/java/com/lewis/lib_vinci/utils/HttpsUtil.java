package com.lewis.lib_vinci.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * https协议---处理证书的工具类，
 * <p>
 * 1、当使用原生的HttpUrlConnection或者HttpClient处理网络请求时，需要手动调用这些方法来处理证书
 * <p>
 * 2、目前使用的大多请求框架一般都会做过类似的处理（其采用的是信任所有）
 */
public class HttpsUtil {
    private final static String TLS = "TLS";
    private final static String CERTIFY_TYPE_X509 = "X.509";
    private final static String CERTIFY_KEY = "ca";
    private static X509Certificate specifyCa;
    //主机名
    public final static String DEFAULT_HOST_NAME = "*.haomaiche.com";


    /**
     * HttpUrlConnection 方式一，支持所有证书验证,实际就是不校验，直接通过
     * <p>
     * 某个具体的URL HttpsURLConnection
     */
    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new TrustManagerAll();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance(TLS);
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        if (hvAll != null) {
            HttpsURLConnection.setDefaultHostnameVerifier(hvAll);
        }
    }

    /**
     * HttpUrlConnection 方式二，支持所有证书验证,实际就是不校验，直接通过
     * <p>
     * 某个具体的URL HttpsURLConnection
     */
    private static void trustAllHttpsByUrl(String url) throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new TrustManagerAll();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance(TLS);
        sc.init(null, trustAllCerts, null);
        URL mUrl = new URL(url);
        HttpsURLConnection urlConnection = (HttpsURLConnection) mUrl.openConnection();
        urlConnection.setSSLSocketFactory(sc.getSocketFactory());
        //方式一,已经不支持
        //urlConnection.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        //方式二
        urlConnection.setHostnameVerifier(hvAll);

        //获取返回数据
        InputStream inputStream = urlConnection.getInputStream();

    }

    /**
     * 主机名检验---未真正校验服务器证书域名是否相符，即信任所有域名
     */
    private static HostnameVerifier hvAll = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            Log.w("Warning: URL Host: ", urlHostName + " vs. "
                    + session.getPeerHost());
            return true;
        }
    };

    /**
     * 主机名检验---信任指定的域名
     */
    private static HostnameVerifier hvSpecify = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
            if (defaultHostnameVerifier != null) {
                boolean verify = defaultHostnameVerifier.verify(DEFAULT_HOST_NAME, session);
                Log.w("Warning: URL Host: ", urlHostName + " vs. "
                        + session.getPeerHost());
                return verify;
            } else {
                return false;
            }
        }
    };

    /**
     * 认证链---未校验服务器证书
     */
    static class TrustManagerAll implements TrustManager, X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 认证链---未校验服务器证书
     */
    static class TrustManagerSpecify implements TrustManager, X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (chain == null) {
                throw new IllegalArgumentException("check Server chain is null");
            }

            if (chain.length <= 0) {
                throw new IllegalArgumentException("check Server chain is empty");
            }

            for (X509Certificate x509Certificate : chain) {
                //校验服务器证书是否有效
                x509Certificate.checkValidity();
                if (specifyCa != null) {
                    try {
                        x509Certificate.verify(specifyCa.getPublicKey());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    } catch (SignatureException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 方式一
     * <p>
     * HttpUrlConnection 方式，支持指定load-der.crt证书验证，此种方式Android官方建议
     */
    public static SSLSocketFactory trustSpecifyHttpsCertificates(Context mContext,
                                                                 String certificateName, String url)
            throws CertificateException, IOException, KeyStoreException,
            NoSuchAlgorithmException, KeyManagementException {
        //获取预埋在APP中的证书
        specifyCa = getCertificate(mContext, certificateName);
        TrustManager[] trustSpecifyCerts = new TrustManager[1];
        TrustManagerSpecify tm = new TrustManagerSpecify();
        trustSpecifyCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance(TLS);
        sc.init(null, trustSpecifyCerts, null);

        URL mUrl = new URL(url);
        HttpsURLConnection urlConnection = (HttpsURLConnection) mUrl.openConnection();
        urlConnection.setSSLSocketFactory(sc.getSocketFactory());
        //方式一,已经不支持
        //urlConnection.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
        //方式二
        urlConnection.setHostnameVerifier(hvSpecify);
        return sc.getSocketFactory();
    }

    /**
     * 方式二
     * <p>
     * HttpUrlConnection 方式，支持指定load-der.crt证书验证，此种方式Android官方建议
     */
    public static SSLSocketFactory trustAppointHttpsCertificates(Context mContext, String certificateName)
            throws CertificateException, IOException, KeyStoreException,
            NoSuchAlgorithmException, KeyManagementException {
        //获取预埋在APP中的证书
        Certificate ca = getCertificate(mContext, certificateName);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null, null);
        keystore.setCertificateEntry(CERTIFY_KEY, ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keystore);
        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance(TLS);
        context.init(null, tmf.getTrustManagers(), null);

        //返回值实际上是给urlConnection.setSSLSocketFactory(sc.getSocketFactory())调用
        return context.getSocketFactory();
    }

    /**
     * 获取放置于assets 文件夹中的证书的Certificate对象
     *
     * @param mContext
     * @param certificateName
     * @return
     * @throws CertificateException
     */
    private static X509Certificate getCertificate(Context mContext, String certificateName) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance(CERTIFY_TYPE_X509);
        // 导入指定证书
        return (X509Certificate) cf.generateCertificate(getCertificatesInputStream(mContext, certificateName));
    }

    /**
     * 获取放在assets 文件夹中的证书
     *
     * @param context
     * @param certificateName 证书地址
     * @return 证书的文件刘
     */
    private static InputStream getCertificatesInputStream(Context context, String certificateName) {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(context.getAssets().open(certificateName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
