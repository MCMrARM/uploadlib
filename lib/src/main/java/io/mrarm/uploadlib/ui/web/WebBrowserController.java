package io.mrarm.uploadlib.ui.web;

import android.content.Context;
import android.webkit.WebView;

import java.util.LinkedList;
import java.util.Queue;

public class WebBrowserController {

    private WebActivityController activityController;
    private int activityAttachmentCount = 0;
    private final boolean async;
    private final Queue<Runnable> userThreadQueue;
    private WebView webView;
    private boolean isWebViewReleased = true;
    private ControllerWebViewClient webViewClient = new ControllerWebViewClient(this);
    private boolean isFinished;
    private final Object isFinishedLock;

    private String url;

    public WebBrowserController(WebActivityController controller, boolean async) {
        activityController = controller;
        this.async = async;
        if (!async) {
            userThreadQueue = new LinkedList<>();
            isFinishedLock = userThreadQueue;
        } else {
            userThreadQueue = null;
            isFinishedLock = new Object();
        }
    }

    public WebBrowserController(WebActivityController controller) {
        this(controller, false);
    }

    public synchronized WebView getWebView() {
        return webView;
    }

    public boolean isAsync() {
        return async;
    }

    synchronized WebView getWebView(Context ctx) {
        if (webView != null && webView.getContext() != ctx)
            throw new RuntimeException("The WebView is created for another Context");
        return webView;
    }

    synchronized WebView getOrCreateWebView(Context ctx) {
        if (webView != null && webView.getContext() != ctx) {
            if (!isWebViewReleased)
                throw new RuntimeException("A WebView was already created for another Context");
            webView = null;
        }
        if (webView == null) {
            webView = new WebView(ctx);
            isWebViewReleased = false;
            setupWebView(webView);
        }
        return webView;
    }

    synchronized void resetWebView() {
        isWebViewReleased = true;
    }

    private void setupWebView(WebView webView) {
        webView.setWebViewClient(webViewClient);
        if (url != null)
            webView.loadUrl(url);
    }

    synchronized void runUserThreadCallbacks() {
        if (userThreadQueue == null)
            return;
        synchronized (userThreadQueue) {
            while (!userThreadQueue.isEmpty())
                userThreadQueue.remove().run();
        }
    }

    void waitForCompletion() throws InterruptedException {
        synchronized (isFinishedLock) {
            while (!isFinished) {
                runUserThreadCallbacks();
                isFinishedLock.wait();
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
     * Sets the user listener for this controller.
     *
     * If the 'async' option was not set for this client in the constructor (it's not set by
     * default), this all methods from the client will be ran on the user thread.
     * @param listener the listener to set
     */
    public void setListener(WebBrowserListener listener) {
        webViewClient.setUserListener(listener, userThreadQueue);
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
    public void loadUrl(String url) throws InterruptedException {
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
                    if (this.webView != webView)
                        return;
                    lambda.run(webView);
                }
            });
        }
    }

    private interface WebViewRunnable {
        void run(WebView webView);
    }

}
