package com.lewis.lib_vinci.coreframe.https.webview;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 项目名称：vinci
 * 类描述：Webview双向证书绑定
 * step1、生成两个证书
 * server.cer  服务器端证书
 * client.p12   客户端证书 （需要记住密码加入之后的Java文件中）
 * 将这两个证书放在 Android assets 文件下。
 * <p>
 * step2、重写WebViewClient引入两个证书
 * <p>
 * <p>
 * step3、
 * 创建人：Administrator
 * 创建时间：2017-05-17
 *
 * @version ${VSERSION}
 */

public class SslPinningWebViewClient extends WebViewClient {

    private Context mContext;
    private LoadedListener listener;
    private SSLContext sslContext;

    public SslPinningWebViewClient(Context context, LoadedListener listener) throws IOException {
        this.mContext = context;
        this.listener = listener;
        //导入服务端证书，证书名：server.cer
        prepareSslPinning(mContext.getResources().getAssets().open("server.cer"));
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
        //MainActivity.pinningSwitch.isChecked(),开关
        if (true) {
            return processRequest(Uri.parse(url));
        } else {
            return null;
        }
    }

    @Override
    @TargetApi(21)
    public WebResourceResponse shouldInterceptRequest(final WebView view, WebResourceRequest interceptedRequest) {
        //MainActivity.pinningSwitch.isChecked()
        if (true) {
            return processRequest(interceptedRequest.getUrl());
        } else {
            return null;
        }
    }

    private WebResourceResponse processRequest(Uri uri) {
        Log.d("SSL_PINNING_WEBVIEWS", "GET: " + uri.toString());

        try {
            // Setup connection
            URL url = new URL(uri.toString());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            // Set SSL Socket Factory for this request
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Get content, contentType and encoding
            InputStream is = urlConnection.getInputStream();
            String contentType = urlConnection.getContentType();
            String encoding = urlConnection.getContentEncoding();

            // If got a contentType header
            if (contentType != null) {

                String mimeType = contentType;

                // Parse mime type from contenttype string
                if (contentType.contains(";")) {
                    mimeType = contentType.split(";")[0].trim();
                }

                Log.d("SSL_PINNING_WEBVIEWS", "Mime: " + mimeType);

                listener.loaded(uri.toString());

                // Return the response
                return new WebResourceResponse(mimeType, encoding, is);
            }

        } catch (SSLHandshakeException e) {
            if (isCause(CertPathValidatorException.class, e)) {
                listener.pinningPreventedLoading(uri.getHost());
            }
            Log.d("SSL_PINNING_WEBVIEWS", e.getLocalizedMessage());
        } catch (Exception e) {
            Log.d("SSL_PINNING_WEBVIEWS", e.getLocalizedMessage());
        }

        // Return empty response for this request
        return new WebResourceResponse(null, null, null);
    }

    /**
     * 双向证书认证
     *
     * client.p12客服端证书
     *
     * @param certificates  服务器端证书的流文件
     * @throws IOException
     */
    private void prepareSslPinning(InputStream... certificates) throws IOException {
        try {
            InputStream inputStream = mContext.getResources().getAssets().open("client.p12");

            TrustManager[] trustManagers = prepareTrustManager(certificates);
            KeyManager[] keyManagers = prepareKeyManager(inputStream, "your client.p12 password");

            sslContext = SSLContext.getInstance("TLS");

            sslContext.init(keyManagers, new TrustManager[]{new MyTrustManager(chooseTrustManager(trustManagers))}, new SecureRandom());


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理服务器端证书
     *
     * @param certificates
     * @return
     */
    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return null;
        try {

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }
            TrustManagerFactory trustManagerFactory = null;

            trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            return trustManagers;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 处理客户端证书
     *
     * @param bksFile
     * @param password
     * @return
     */
    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;

            KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static boolean isCause(Class<? extends Throwable> expected, Throwable exc) {
        return expected.isInstance(exc) || (exc != null && isCause(expected, exc.getCause()));
    }
}
