package io.mrarm.uploadlib.ui.login;

import android.os.Looper;

import java.lang.ref.WeakReference;

public class SimpleLoginActivityController {

    private static final int STATE_LOADING = 0;
    private static final int STATE_WEB_LOGIN = 1;

    private WeakReference<SimpleLoginActivity> activity;
    private boolean applyActivityStateQueued = false;
    private int currentState = STATE_LOADING;

    public SimpleLoginActivityController(SimpleLoginActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public synchronized void setActivity(SimpleLoginActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    private synchronized void applyActivityState() {
        if (applyActivityStateQueued)
            return;
        SimpleLoginActivity activity = this.activity.get();
        if (activity == null)
            return;
        if (Looper.myLooper() != Looper.getMainLooper()) {
            applyActivityStateQueued = true;
            activity.runOnUiThread(this::applyActivityState);
            return;
        }
        applyActivityStateQueued = false;

        if (currentState == STATE_LOADING) {
            activity.setViewLoading();
        }
    }


    public synchronized void setLoadingState() {
        currentState = STATE_LOADING;
        applyActivityState();
    }

    public synchronized void setWebState() {
        currentState = STATE_WEB_LOGIN;
        applyActivityState();
    }

}
