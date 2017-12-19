package io.mrarm.uploadlib.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import io.mrarm.uploadlib.FileUploadProviderManager;
import io.mrarm.uploadlib.example.provider.MyProviderRegisterHelper;
import io.mrarm.uploadlib.example.provider.TestFileUploadProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyProviderRegisterHelper.registerMyProviders();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test_login).setOnClickListener((View v) -> {
            FileUploadProviderManager.findProviderWithUUID(TestFileUploadProvider.MY_UUID)
                    .startLogInFlow(this);
        });
    }
}
