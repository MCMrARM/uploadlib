package io.mrarm.uploadlib.ui.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;

import io.mrarm.uploadlib.FileUploadProvider;
import io.mrarm.uploadlib.ui.web.WebActivityController;

public abstract class SimpleLoginFileUploadProvider implements FileUploadProvider {

    private static WebActivityController currentLoginWebController;
    private static Thread currentLoginWebControllerThread;
    private static final Object currentLoginWebControllerLock = new Object();

    private WebActivityController mLoginWebController;

    @Override
    public void startLogInFlow(Context ctx) {
        Intent intent = new Intent(ctx, SimpleLoginActivity.class);
        intent.putExtra(SimpleLoginActivity.ARG_PROVIDER_UUID, getUUID().toString());
        ctx.startActivity(intent);
    }

    public abstract void handleLogInFlow(WebActivityController controller)
            throws InterruptedException;

    void onLogInActivityStarted(SimpleLoginActivity activity) {
        synchronized (currentLoginWebControllerLock) {
            if (mLoginWebController == null) {
                while (currentLoginWebController != null) {
                    currentLoginWebControllerThread.interrupt();
                    try {
                        currentLoginWebControllerLock.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                mLoginWebController = new WebActivityController(activity);

                WebActivityController controller = mLoginWebController;
                Thread thread = new Thread(() -> {
                    try {
                        handleLogInFlow(controller);
                    } catch (InterruptedException ignored) {
                    }
                    controller.setFinished();
                    synchronized (currentLoginWebControllerLock) {
                        if (mLoginWebController == controller) {
                            mLoginWebController = null;
                            currentLoginWebController = null;
                            currentLoginWebControllerThread = null;
                            currentLoginWebControllerLock.notify();
                        }
                    }
                });
                thread.setName("Log In Flow Handler");
                currentLoginWebController = mLoginWebController;
                currentLoginWebControllerThread = thread;
                thread.start();
            } else {
                mLoginWebController.setActivity(activity);
            }
        }
    }

    @VisibleForTesting
    public void waitForLogInFlow() throws InterruptedException {
        synchronized (currentLoginWebControllerLock) {
            while (mLoginWebController != null)
                currentLoginWebControllerLock.wait();
        }
    }

}
