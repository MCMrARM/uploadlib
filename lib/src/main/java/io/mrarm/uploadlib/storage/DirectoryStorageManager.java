package io.mrarm.uploadlib.storage;

import java.io.File;

import io.mrarm.uploadlib.FileUploadProvider;

/**
 * A simple implementation of the StorageManager that stores all the provider data in a single
 * directory.
 * For a quick start, you'll probably want to use AndroidStorageManager instead.
 */
public class DirectoryStorageManager implements StorageManager {

    private File directory;

    public DirectoryStorageManager(File directory) {
        this.directory = directory;
    }

    @Override
    public File getDataDir(FileUploadProvider provider) {
        return new File(directory, provider.getUUID().toString());
    }

    @Override
    public void deleteData(FileUploadProvider provider) {
        StorageHelper.deleteRecursive(getDataDir(provider));
    }

}
