package io.mrarm.uploadlib.ui.login;

import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

import io.mrarm.uploadlib.FileUploadProvider;
import io.mrarm.uploadlib.FileUploadProviderManager;
import io.mrarm.uploadlib.ui.web.WebActivity;

public class SimpleLoginActivity extends WebActivity {

    private static final String TAG = "SimpleLoginActivity";

    public static final String ARG_PROVIDER_UUID = "provider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sProviderUUID = getIntent().getStringExtra(ARG_PROVIDER_UUID);
        if (sProviderUUID == null) {
            Log.e(TAG, "No provider UUID in intent");
            finish();
            return;
        }
        UUID providerUUID = UUID.fromString(sProviderUUID);
        FileUploadProvider provider = FileUploadProviderManager.findProviderWithUUID(providerUUID);
        if (provider == null || !(provider instanceof SimpleLoginFileUploadProvider)) {
            Log.e(TAG, "Invalid provider");
            finish();
            return;
        }
        ((SimpleLoginFileUploadProvider) provider).onLogInActivityStarted(this);
    }

}
