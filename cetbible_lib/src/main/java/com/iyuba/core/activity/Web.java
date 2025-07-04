package com.iyuba.core.activity;

/**
 * 网页显示
 *
 * @author chentong
 * @version 1.0
 * @para 传入"url" 网址；"title"标题显示
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.base.SwipeBackHelper;
import com.iyuba.base.util.SimpleNightMode;
import com.iyuba.base.util.Util;
import com.iyuba.biblelib.R;

import java.util.ArrayList;
import java.util.List;

public class Web extends AppCompatActivity {
    private View backButton;
    private WebView web;
    private TextView textView;
    private String titleStr;
    private SwipeBackHelper swipeBackHelper;
    private SimpleNightMode simpleNightMode;
    private List<String> urlList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        swipeBackHelper = new SwipeBackHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        simpleNightMode = new SimpleNightMode(this);
        simpleNightMode.onResume();

        setProgressBarVisibility(true);
        //        CrashApplication.getInstance().addActivity(this);
        backButton = findViewById(R.id.button_back);
        textView = findViewById(R.id.title);
        web = findViewById(R.id.webView);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });

        titleStr = this.getIntent().getStringExtra("title");

        web.loadUrl(this.getIntent().getStringExtra("url"));

        if (!TextUtils.isEmpty(titleStr)) {
            textView.setText(titleStr);
        }
        WebSettings websettings = web.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setSupportZoom(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setUseWideViewPort(true);
        websettings.setDomStorageEnabled(true);
        websettings.setDatabaseEnabled(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("market")){
                    gotoMarket();
                }else if(url.startsWith("mqqwpa")){
                    goToQQ(url);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            // Set progress bar during loading
            @Override
            public void onProgressChanged(WebView view, int progress) {
                Web.this.setProgress(progress * 100);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(titleStr)) {
                    textView.setText(title);
                }
            }
        });
        web.setDownloadListener(new DownloadListener() {

            @Override

            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
    public static void start(Context context , String url , String title ){
        Intent intent  = new Intent(context , Web.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        simpleNightMode.close();
        web.destroy();
        super.onDestroy();
    }


    public  void gotoMarket(){
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToQQ(String url) {
        Util.startQQ(this, url.substring(url.indexOf("uin=")+4,url.indexOf("uin=")+14));
    }
}
