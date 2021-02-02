package com.teamnova.dateset.addedfunc.find_restaurant;

import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dateset.R;

public class RestaurantInfoWebview extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_restaurant_info);

        String link = getIntent().getStringExtra("LINK");

        mWebView = findViewById(R.id.mWebView);

        // 웹뷰로 클릭 했을 때 하얀화면만 나오는 경우가 있다.
        // 이것은 ssl인증으로 인한 문제이며 이를 해결하기 위해 해당 에러가 발생해도
        // 웹페이지가 출력될 수 있도록 아래와 같은 코드를 추가해줘야 한다.
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl(link);
    }
}