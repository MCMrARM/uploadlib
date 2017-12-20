package io.mrarm.uploadlib.ui.login;

import android.content.Context;
import android.webkit.WebView;

import java.util.function.Consumer;

public class WebBrowserController {

    private WebView webView;
    private boolean isFinished;
    private final Object isFinishedLock = new Object();

    private String url;

    public WebBrowserController() {
    }

    public synchronized WebView getWebView() {
        return webView;
    }

    synchronized WebView getOrCreateWebView(Context ctx) {
        if (webView != null && webView.getContext() != ctx)
            webView = null;
        if (webView == null) {
            webView = new WebView(ctx);
            setupWebView(webView);
        }
        return webView;
    }

    private void setupWebView(WebView webView) {
        if (url != null)
            webView.loadUrl(url);
    }

    void waitForCompletion() {
        synchronized (isFinishedLock) {
            while (!isFinished) {
                try {
                    isFinishedLock.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    void start() {
        synchronized (isFinishedLock) {
            if (isFinished)
                throw new RuntimeException("This WebBrowserController has already been finished");
        }
    }

    public void finish() {
        synchronized (isFinishedLock) {
            isFinished = true;
            isFinishedLock.notifyAll();
        }
    }

    public synchronized void setUrl(String url) {
        this.url = url;
        runWithWebView((WebView webView) -> webView.loadUrl(url));
    }


    private synchronized void runWithWebView(WebViewRunnable lambda) {
        WebView webView = this.webView;
        if (webView != null) {
            webView.post(() -> lambda.run(webView));
        }
    }

    private interface WebViewRunnable {
        void run(WebView webView);
    }

}
