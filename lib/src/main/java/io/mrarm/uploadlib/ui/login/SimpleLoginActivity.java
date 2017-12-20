package io.mrarm.uploadlib.ui.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.util.UUID;

import io.mrarm.uploadlib.FileUploadProvider;
import io.mrarm.uploadlib.FileUploadProviderManager;

public class SimpleLoginActivity extends AppCompatActivity {

    private static final String TAG = "SimpleLoginActivity";

    public static final String ARG_PROVIDER_UUID = "provider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewLoading();

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

    public void setViewLoading() {
        ProgressBar bar = new ProgressBar(this);
        setContentView(bar);
    }

    public void setViewWeb(WebView webView) {
        setContentView(webView);
    }

}
