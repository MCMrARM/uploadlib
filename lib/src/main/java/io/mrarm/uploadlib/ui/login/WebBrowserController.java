package io.mrarm.uploadlib.ui.login;

import android.content.Context;
import android.webkit.WebView;

public class WebBrowserController {

    private SimpleLoginActivityController activityController;
    private int activityAttachmentCount = 0;
    private WebView webView;
    private ControllerWebViewClient webViewClient = new ControllerWebViewClient();
    private boolean isFinished;
    private final Object isFinishedLock = new Object();

    private String url;

    public WebBrowserController(SimpleLoginActivityController controller) {
        activityController = controller;
    }

    public synchronized WebView getWebView() {
        return webView;
    }

    synchronized WebView getOrCreateWebView(Context ctx) {
        if (webView != null && webView.getContext() != ctx)
            throw new RuntimeException("A WebView was already created for another Context");
        if (webView == null) {
            webView = new WebView(ctx);
            setupWebView(webView);
        }
        return webView;
    }

    private void setupWebView(WebView webView) {
        webView.setWebViewClient(webViewClient);
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

    private synchronized void addAttachment() {
        activityAttachmentCount++;
        if (activityAttachmentCount == 1)
            activityController.attachWebController(this);
    }

    private synchronized void removeAttachment() {
        activityAttachmentCount--;
        if (activityAttachmentCount == 0)
            activityController.detachWebController(this);
    }

    /**
     * Sets the specified URL without waiting for it to load.
     * @param url the url to set
     */
    public synchronized void setUrl(String url) {
        this.url = url;
        runWithWebView((WebView webView) -> webView.loadUrl(url));
    }

    /**
     * Sets the specified url and waits for it to load.
     * @param url the url to load
     */
    public void loadUrl(String url) {
        addAttachment();
        setUrl(url);
        webViewClient.waitForUrl(url);
        removeAttachment();
    }


    private synchronized void runWithWebView(WebViewRunnable lambda) {
        WebView webView = this.webView;
        if (webView != null) {
            webView.post(() -> {
                synchronized (this) {
                    lambda.run(webView);
                }
            });
        }
    }

    private interface WebViewRunnable {
        void run(WebView webView);
    }

}
