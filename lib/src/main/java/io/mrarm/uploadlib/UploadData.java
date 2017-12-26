package io.mrarm.uploadlib;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface representing a file that will be uploaded.
 */
public interface UploadData {

    /**
     * Returns this file name if it has one or null.
     * @return the filename
     */
    @Nullable String getName();

    /**
     * Returns this file's mimetype if available.
     * @return the file mimetype hint
     */
    @Nullable String getMimeType();

    /**
     * Opens this file for reading. This may be called more than once by the uploader.
     * @return the input stream for this file
     */
    @NonNull InputStream open() throws IOException;

    /**
     * Returns this file's size. This must return
     * @return the size of this file
     */
    long size();

}
