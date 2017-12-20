package io.mrarm.uploadlib.ui.login;

import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;

public class SimpleLoginActivityController {

    private static final String TAG = "SimpleLoginActivity";

    private static final int STATE_LOADING = 0;
    private static final int STATE_WEB_BROWSER = 1;

    private WeakReference<SimpleLoginActivity> activity;
    private boolean applyActivityStateQueued = false;
    private int currentState = STATE_LOADING;
    private WebBrowserController currentWebBrowserController = null;

    public SimpleLoginActivityController(SimpleLoginActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public synchronized void setActivity(SimpleLoginActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    private synchronized void applyActivityState() {
        SimpleLoginActivity activity = this.activity.get();
        if (activity == null) {
            Log.d(TAG, "Apply state: activity is null");
            applyActivityStateQueued = false;
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            if (applyActivityStateQueued) {
                return;
            }
            applyActivityStateQueued = true;
            activity.runOnUiThread(this::applyActivityState);
            return;
        }
        applyActivityStateQueued = false;

        Log.d(TAG, "Apply state: " + currentState);
        if (currentState == STATE_LOADING) {
            activity.setViewLoading();
        } else if (currentState == STATE_WEB_BROWSER) {
            activity.setViewWeb(currentWebBrowserController.getOrCreateWebView(activity));
        }
    }


    public synchronized void setLoadingState() {
        currentState = STATE_LOADING;
        applyActivityState();
    }

    public void setWebState(WebBrowserController controller, boolean async) {
        synchronized (this) {
            currentState = STATE_WEB_BROWSER;
            currentWebBrowserController = controller;
            applyActivityState();
        }
        if (!async)
            controller.waitForCompletion();
    }

    public void setWebState(WebBrowserController controller) {
        setWebState(controller, false);
    }

}
