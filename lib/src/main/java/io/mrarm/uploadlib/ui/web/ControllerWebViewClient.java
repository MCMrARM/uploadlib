package io.mrarm.uploadlib.ui.web;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerWebViewClient extends WebViewClient {

    private static final String TAG = "ControllerWebViewClient";

    private final WebBrowserController browser;
    private final Queue<Runnable> urlWaitList = new LinkedList<>();
    private WebBrowserListener userListener;
    private Queue<Runnable> userListenerQueue;

    public ControllerWebViewClient(WebBrowserController browser) {
        this.browser = browser;
    }

    public void setUserListener(WebBrowserListener client, Queue<Runnable> queue) {
        userListener = client;
        userListenerQueue = queue;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        runUserCallback((WebBrowserListener client) -> client.onPageStarted(browser, url, favicon));
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "onPageFinished: url = " + url);
        synchronized (this) {
            if (!urlWaitList.isEmpty())
                urlWaitList.remove().run();
        }
        runUserCallback((WebBrowserListener client) -> client.onPageFinished(browser, url));
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        runUserCallback((WebBrowserListener client) -> client.onLoadResource(browser, url));
    }

    public synchronized void waitForUrl(String url, Runnable runnable) {
        // TODO: Actually wait for this URL, not any URL
        urlWaitList.add(runnable);
    }

    public void waitForUrl(String url) {
        final AtomicBoolean done = new AtomicBoolean(false);
        Object lk;
        synchronized (this) {
            if (userListenerQueue != null)
                lk = userListenerQueue;
            else
                lk = done;
        }
        waitForUrl(url, () -> {
            synchronized (lk) {
                done.set(true);
                lk.notify();
            }
        });
        synchronized (lk) {
            while (!done.get()) {
                if (lk != done)
                    browser.runUserThreadCallbacks();
                try {
                    lk.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private void runUserCallback(UserCallbackRunnable runnable) {
        Queue<Runnable> queue;
        synchronized(this) {
            if (userListener == null)
                return;
            queue = this.userListenerQueue;
        }

        if (queue != null) {
            synchronized (queue) {
                queue.add(() -> {
                    WebBrowserListener listener;
                    synchronized (this) {
                        if (userListener == null)
                            return;
                        listener = userListener;
                    }
                    runnable.run(listener);
                });
                queue.notify();
            }
        } else {
            runnable.run(userListener);
        }
    }

    private interface UserCallbackRunnable {
        void run(WebBrowserListener runnable);
    }

}
