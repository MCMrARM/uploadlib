package io.mrarm.uploadlib.ui.login;

import android.content.Context;
import android.content.Intent;

import io.mrarm.uploadlib.FileUploadProvider;

public abstract class SimpleLoginFileUploadProvider implements FileUploadProvider {

    private SimpleLoginActivityController mLoginController;

    @Override
    public void startLogInFlow(Context ctx) {
        Intent intent = new Intent(ctx, SimpleLoginActivity.class);
        intent.putExtra(SimpleLoginActivity.ARG_PROVIDER_UUID, getUUID().toString());
        ctx.startActivity(intent);
    }

    public abstract void handleLogInFlow(SimpleLoginActivityController controller);

    void onLogInActivityStarted(SimpleLoginActivity activity) {
        if (mLoginController == null) {
            mLoginController = new SimpleLoginActivityController(activity);

            SimpleLoginActivityController controller = mLoginController;
            Thread thread = new Thread(() -> {
                handleLogInFlow(controller);
                controller.setFinished();
            });
            thread.setName("Log In Flow Handler");
            thread.start();
        } else {
            mLoginController.setActivity(activity);
        }
    }

}
