package io.mrarm.uploadlib;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This interface represents a file upload request, returned by FileUploadProvider.upload.
 */
public interface FileUploadRequest {

    /**
     * Checks if the request has been finished or cancelled.
     * @return if the request has been finished or cancelled
     */
    boolean isDone();

    /**
     * Checks if the request has been cancelled
     * @return if the request has been cancelled
     */
    boolean isCancelled();

    /**
     * Gets the currently uploaded amount of data. Must be reported in bytes.
     * @return the amount of currently uploaded bytes
     */
    long getUploadProgress();

    /**
     * Gets the total amount of data to upload. Must be reported in bytes.
     * @return the total amount of data to upload
     */
    long getUploadProgressMax();

    /**
     * Gets the uploaded file. This will block the thread until either the timeout has passed, the
     * program is interrupted or the request is finished.
     * @param timeout the max time to wait
     * @param unit the unit in which 'timeout' is specified
     * @return the uploaded file info
     * @throws FileUploadException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    @NonNull UploadedFile get(long timeout, @NonNull TimeUnit unit) throws FileUploadException,
            InterruptedException, TimeoutException;

    /**
     * Gets the uploaded file.
     * @return the uploaded file info
     * @throws FileUploadException
     * @throws InterruptedException
     */
    @NonNull UploadedFile get() throws FileUploadException, InterruptedException;

    /**
     * Cancels the request.
     */
    void cancel();

}
