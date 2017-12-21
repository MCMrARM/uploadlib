package io.mrarm.uploadlib.ui.web;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class WebActivity extends AppCompatActivity {

    private RelativeLayout layout;
    private ProgressBar progressBar;
    private WebView activeWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = new RelativeLayout(this);
        setContentView(layout);

        setViewLoading();
    }

    public void setViewLoading() {
        if (progressBar == null) {
            progressBar = new ProgressBar(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            progressBar.setLayoutParams(params);
            layout.addView(progressBar);
        }
        if (activeWebView != null) {
            layout.removeView(activeWebView);
            activeWebView = null;
        }
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setViewWeb(WebView webView) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        if (layout.indexOfChild(webView) == -1)
            throw new RuntimeException("WebView not attached");
        activeWebView = webView;
        webView.setVisibility(View.VISIBLE);
    }

    public void attachWebView(WebView webView) {
        if (layout.indexOfChild(webView) != -1)
            return;
        webView.setVisibility(View.INVISIBLE);
        layout.addView(webView);
    }

    public void detachWebView(WebView webView) {
        layout.removeView(webView);
    }

}
