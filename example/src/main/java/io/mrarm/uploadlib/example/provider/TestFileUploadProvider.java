package io.mrarm.uploadlib.example.provider;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

import io.mrarm.uploadlib.FileUploadRequest;
import io.mrarm.uploadlib.FileUploadUserContext;
import io.mrarm.uploadlib.ui.login.SimpleLoginActivityController;
import io.mrarm.uploadlib.ui.login.SimpleLoginFileUploadProvider;
import io.mrarm.uploadlib.ui.login.WebBrowserController;

public class TestFileUploadProvider extends SimpleLoginFileUploadProvider {

    public static final UUID MY_UUID = UUID.fromString("dd49e39d-0bbc-44e1-93d3-f00be52104a0");

    @NonNull
    @Override
    public UUID getUUID() {
        return MY_UUID;
    }

    @Override
    public FileUploadRequest upload(@Nullable Context ctx, @Nullable FileUploadUserContext userContext, @Nullable String filename, @Nullable String mimeType, @NonNull InputStream stream) {
        return null;
    }

    @Override
    public boolean canLogIn() {
        return true;
    }

    @Override
    public boolean isLogInRequired() {
        return true;
    }

    @Override
    public Collection<FileUploadUserContext> getLoggedInUsers() {
        return null;
    }

    @Override
    public void handleLogInFlow(SimpleLoginActivityController controller) {
        controller.setLoadingState();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        WebBrowserController webController = new WebBrowserController(controller);
        webController.loadUrl("http://example.com/");
        controller.setWebState(webController);
    }

}
