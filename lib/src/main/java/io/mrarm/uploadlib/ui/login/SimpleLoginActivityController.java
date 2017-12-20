package io.mrarm.uploadlib.ui.login;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class SimpleLoginActivityController {

    private static final String TAG = "SimpleLoginActivity";

    private static final int STATE_LOADING = 0;
    private static final int STATE_WEB_BROWSER = 1;

    private WeakReference<SimpleLoginActivity> activity;
    private int currentState = STATE_LOADING;
    private WebBrowserController currentWebBrowserController = null;
    private final Set<WebBrowserController> attachedWebBrowserControllers = new HashSet<>();

    public SimpleLoginActivityController(SimpleLoginActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public synchronized void setActivity(SimpleLoginActivity activity) {
        this.activity = new WeakReference<>(activity);
        setupActivity(activity);
    }

    private synchronized void setupActivity(SimpleLoginActivity activity) {
        for (WebBrowserController controller : attachedWebBrowserControllers)
            activity.attachWebView(controller.getOrCreateWebView(activity));
        setState(currentState);
    }

    private synchronized void setState(int state) {
        Log.d(TAG, "New state: " + state);
        currentState = state;
        runWithActivity((SimpleLoginActivity activity) -> {
            if (state == STATE_LOADING) {
                activity.setViewLoading();
            } else if (state == STATE_WEB_BROWSER) {
                activity.setViewWeb(currentWebBrowserController.getOrCreateWebView(activity));
            }
        });
    }

    public synchronized void setLoadingState() {
        setState(STATE_LOADING);
    }

    synchronized void attachWebController(WebBrowserController controller) {
        attachedWebBrowserControllers.add(controller);
        runWithActivity((SimpleLoginActivity activity) -> {
            activity.attachWebView(controller.getOrCreateWebView(activity));
        });
    }

    synchronized void detachWebController(WebBrowserController controller) {
        attachedWebBrowserControllers.remove(controller);
        runWithActivity((SimpleLoginActivity activity) -> {
            activity.detachWebView(controller.getOrCreateWebView(activity));
        });
    }

    public void setWebState(WebBrowserController controller, boolean async) {
        synchronized (this) {
            if (currentWebBrowserController != null &&
                    !attachedWebBrowserControllers.contains(currentWebBrowserController)) {
                WebBrowserController oldController = currentWebBrowserController;
                runWithActivity((SimpleLoginActivity activity) -> {
                    activity.detachWebView(oldController.getOrCreateWebView(activity));
                });
            }
            currentWebBrowserController = controller;
            if (!attachedWebBrowserControllers.contains(controller)) {
                runWithActivity((SimpleLoginActivity activity) -> {
                    activity.attachWebView(controller.getOrCreateWebView(activity));
                });
            }
            setState(STATE_WEB_BROWSER);
        }
        if (!async)
            controller.waitForCompletion();
    }

    public void setWebState(WebBrowserController controller) {
        setWebState(controller, false);
    }


    private synchronized void runWithActivity(ActivityRunnable lambda) {
        SimpleLoginActivity activity = this.activity != null ? this.activity.get() : null;
        if (activity != null) {
            activity.runOnUiThread(() -> {
                synchronized (this) {
                    lambda.run(activity);
                }
            });
        }
    }

    private interface ActivityRunnable {
        void run(SimpleLoginActivity activity);
    }

}
