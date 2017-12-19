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
        if (mLoginController == null)
            mLoginController = new SimpleLoginActivityController(activity);
        else
            mLoginController.setActivity(activity);

        handleLogInFlow(mLoginController);
    }

}
