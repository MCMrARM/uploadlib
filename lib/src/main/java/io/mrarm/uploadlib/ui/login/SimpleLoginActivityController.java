package io.mrarm.uploadlib.ui.login;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class SimpleLoginActivityController {

    private static final String TAG = "SimpleLoginActivity";

    private static final int STATE_LOADING = 0;
    private static final int STATE_WEB_BROWSER = 1;
    private static final int STATE_FINISHED = -1;

    private WeakReference<SimpleLoginActivity> activity;
    private int currentState = STATE_LOADING;
    private WebBrowserController currentWebBrowserController = null;
    private final Set<WebBrowserController> attachedWebBrowserControllers = new HashSet<>();

    public SimpleLoginActivityController(SimpleLoginActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public synchronized void setActivity(SimpleLoginActivity activity) {
        SimpleLoginActivity oldActivity = this.activity != null ? this.activity.get() : null;
        this.activity = new WeakReference<>(activity);
        if (oldActivity != null) {
            for (WebBrowserController controller : attachedWebBrowserControllers) {
                oldActivity.detachWebView(controller.getWebView(oldActivity));
                controller.resetWebView();
            }
            if (currentWebBrowserController != null) {
                oldActivity.detachWebView(currentWebBrowserController.getWebView(oldActivity));
                currentWebBrowserController.resetWebView();
            }
        }
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
                if (!attachedWebBrowserControllers.contains(currentWebBrowserController))
                    activity.attachWebView(currentWebBrowserController
                            .getOrCreateWebView(activity));
                activity.setViewWeb(currentWebBrowserController.getOrCreateWebView(activity));
            } else if (state == STATE_FINISHED) {
                activity.finish();
                this.activity = null;
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
            activity.detachWebView(controller.getWebView(activity));
            controller.resetWebView();
        });
    }

    public void setWebState(WebBrowserController controller) {
        synchronized (this) {
            if (currentWebBrowserController != null &&
                    !attachedWebBrowserControllers.contains(currentWebBrowserController)) {
                WebBrowserController oldController = currentWebBrowserController;
                runWithActivity((SimpleLoginActivity activity) -> {
                    activity.detachWebView(oldController.getWebView(activity));
                    controller.resetWebView();
                });
            }
            currentWebBrowserController = controller;
            setState(STATE_WEB_BROWSER);
        }
        if (!controller.isAsync())
            controller.waitForCompletion();
    }

    public void setFinished() {
        setState(STATE_FINISHED);
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
