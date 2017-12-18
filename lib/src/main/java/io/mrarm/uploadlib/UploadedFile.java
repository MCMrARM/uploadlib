package io.mrarm.uploadlib;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Represents an uploaded file.
 */
public interface UploadedFile {

    /**
     * Get the associated provider for this file.
     * @return the provider
     */
    FileUploadProvider getProvider();

    /**
     * Get the URL with the specified key. This is provider-specific.
     * @param key the provider-specific key
     * @return the url for the key or null if it doesn't exist
     */
    @Nullable String getUrl(String key);

    /**
     * Get all the URLs associated with the specific file. This will include any additional URLs
     * that are enabled by this provider.
     * @return the url list
     */
    @NonNull Map<String, String> getAllUrls();


    /**
     * Get the primary URL.
     * @return the primary URL
     */
    @NonNull String getPrimaryUrl();

    /**
     * Get the thumbnail URL.
     * @return the thumbnail URL
     */
    @Nullable String getThumbnailUrl();


    /**
     * Deletes this file. Can throw a NotSupportedException.
     */
    void delete();

}
