package io.mrarm.uploadlib.ui.login;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerWebViewClient extends WebViewClient {

    private static final String TAG = "ControllerWebViewClient";

    private final Queue<Runnable> urlWaitList = new LinkedList<>();

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "onPageFinished: url = " + url);
        synchronized (this) {
            if (!urlWaitList.isEmpty())
                urlWaitList.remove().run();
        }
    }

    public synchronized void waitForUrl(String url, Runnable runnable) {
        // TODO: Actually wait for this URL, not any URL
        urlWaitList.add(runnable);
    }

    public void waitForUrl(String url) {
        final AtomicBoolean done = new AtomicBoolean(false);
        waitForUrl(url, () -> {
            synchronized (done) {
                done.set(true);
                done.notify();
            }
        });
        synchronized (done) {
            while (!done.get()) {
                try {
                    done.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

}
