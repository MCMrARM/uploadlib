package io.mrarm.uploadlib;

import java.io.InputStream;

public interface FileUploadProvider {

    /**
     * Uploads a file.
     * @param filename file name hint
     * @param mimeType file mime type hint
     * @param stream file data
     * @return the upload request that will be completed asynchronously
     */
    FileUploadRequest upload(String filename, String mimeType, InputStream stream);

}
