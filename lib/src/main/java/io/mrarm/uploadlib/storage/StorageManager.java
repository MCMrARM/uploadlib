package io.mrarm.uploadlib.storage;

import java.io.File;

import io.mrarm.uploadlib.FileUploadProvider;

/**
 * An interface for managing provider-specific data. If your provider needs to store any data, you
 * should ask for a instance of this class in the constructor and use .getDataDir to get the data
 * directory for your provider.
 */
public interface StorageManager {

    /**
     * Gets the data directory exclusive for the specified provider. This directory doesn't have
     * to exist and it's up to the provider to create it if needed. However, the parent directory
     * should be created by the StorageManager.
     * Providers should not store any persistent data outside of that directory.
     * @param provider the provider
     * @return the data directory for the provider
     */
    File getDataDir(FileUploadProvider provider);

    /**
     * Deletes the data directory for the specified provider.
     * @param provider the provider
     */
    void deleteData(FileUploadProvider provider);

}
