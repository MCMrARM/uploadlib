package io.mrarm.uploadlib.ui.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.UUID;

import io.mrarm.uploadlib.FileUploadProvider;
import io.mrarm.uploadlib.FileUploadProviderManager;

public class SimpleLoginActivity extends AppCompatActivity {

    private static final String TAG = "SimpleLoginActivity";

    public static final String ARG_PROVIDER_UUID = "provider";

    private RelativeLayout layout;
    private ProgressBar progressBar;
    private WebView activeWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = new RelativeLayout(this);
        setContentView(layout);

        setViewLoading();

        String sProviderUUID = getIntent().getStringExtra(ARG_PROVIDER_UUID);
        if (sProviderUUID == null) {
            Log.e(TAG, "No provider UUID in intent");
            finish();
            return;
        }
        UUID providerUUID = UUID.fromString(sProviderUUID);
        FileUploadProvider provider = FileUploadProviderManager.findProviderWithUUID(providerUUID);
        if (provider == null || !(provider instanceof SimpleLoginFileUploadProvider)) {
            Log.e(TAG, "Invalid provider");
            finish();
            return;
        }
        ((SimpleLoginFileUploadProvider) provider).onLogInActivityStarted(this);
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
