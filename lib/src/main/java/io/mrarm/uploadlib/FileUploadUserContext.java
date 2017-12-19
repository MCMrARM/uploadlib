package io.mrarm.uploadlib;

/**
 * This interface is supposed to be used as an user identifier for file uploads.
 */
public interface FileUploadUserContext {

    /**
     * Get the display name for this user for display in UIs.
     * @return the name to display
     */
    String getDisplayName();

}
