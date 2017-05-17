package com.lewis.vinci;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.lewis.lib_vinci.coreframe.https.webview.LoadedListener;
import com.lewis.lib_vinci.coreframe.https.webview.SslPinningWebViewClient;

import java.io.IOException;

/**
 * 项目名称：vinci
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2017-05-17
 *
 * @version ${VSERSION}
 */


public class WebHttpsActivity extends AppCompatActivity {
    private WebView webView;
    public static Switch pinningSwitch;
    private Button btnA;
    private Button btnB;
    public static TextView textView;

    private String url1 = "your https url";
    private String url2 = "your https url";

    public WebHttpsActivity() {
    }

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_https);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        pinningSwitch = (Switch) findViewById(R.id.pinningSwitch);
        btnA = (Button) findViewById(R.id.btn1);
        btnB = (Button) findViewById(R.id.btn2);
        textView = (TextView) findViewById(R.id.tv);

        mContext = this;
        SslPinningWebViewClient webViewClient = null;
        try {
            webViewClient = new SslPinningWebViewClient(WebHttpsActivity.this, new LoadedListener() {

                @Override
                public void loaded(final String url) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("Loaded " + url);
                        }
                    });
                }

                @Override
                public void pinningPreventedLoading(final String host) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("SSL Pinning prevented loading from " + host);
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        webView.setWebViewClient(webViewClient);

        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearView();
                textView.setText("");
                webView.loadUrl(url1);
            }
        });

        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearView();
                textView.setText("");
                webView.loadUrl(url2);
            }
        });
    }
}
