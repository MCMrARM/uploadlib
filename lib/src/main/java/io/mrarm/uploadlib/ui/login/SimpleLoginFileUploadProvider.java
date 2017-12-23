package io.mrarm.uploadlib.ui.login;

import android.content.Context;
import android.content.Intent;

import io.mrarm.uploadlib.FileUploadProvider;
import io.mrarm.uploadlib.ui.web.WebActivityController;

public abstract class SimpleLoginFileUploadProvider implements FileUploadProvider {

    private WebActivityController mLoginWebController;

    @Override
    public void startLogInFlow(Context ctx) {
        Intent intent = new Intent(ctx, SimpleLoginActivity.class);
        intent.putExtra(SimpleLoginActivity.ARG_PROVIDER_UUID, getUUID().toString());
        ctx.startActivity(intent);
    }

    public abstract void handleLogInFlow(WebActivityController controller)
            throws InterruptedException;

    synchronized void onLogInActivityStarted(SimpleLoginActivity activity) {
        if (mLoginWebController == null) {
            mLoginWebController = new WebActivityController(activity);

            WebActivityController controller = mLoginWebController;
            Thread thread = new Thread(() -> {
                try {
                    handleLogInFlow(controller);
                } catch (InterruptedException ignored) {
                }
                controller.setFinished();
                synchronized (this) {
                    if (mLoginWebController == controller)
                        mLoginWebController = null;
                }
            });
            thread.setName("Log In Flow Handler");
            thread.start();
        } else {
            mLoginWebController.setActivity(activity);
        }
    }

}
