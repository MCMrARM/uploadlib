package io.mrarm.uploadlib.example.provider;


import io.mrarm.uploadlib.FileUploadProviderManager;

public class MyProviderRegisterHelper {

    private static boolean registered = false;

    public static void registerMyProviders() {
        if (registered)
            return;
        registered = true;

        FileUploadProviderManager.add(new TestFileUploadProvider());
    }

}
