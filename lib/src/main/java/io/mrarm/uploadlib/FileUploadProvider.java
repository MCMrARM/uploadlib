package io.mrarm.uploadlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

/**
 * The main interface representing a file upload provider.
 * All FileUploadProvider-s should be later added to the FileUploadProviderManager.
 */
public interface FileUploadProvider {

    /**
     * Returns this provider's unique identifier. No two providers with the same UUID can be added.
     * This should be a static value, shared between app's sessions.
     * @return the uuid
     */
    @NonNull UUID getUUID();

    /**
     * Uploads a file.
     * @param ctx Android context used for any additional UIs, optional
     * @param userContext the user to upload the file as, null for anonymous uploads
     * @param filename file name hint
     * @param mimeType file mime type hint
     * @param stream file data
     * @return the upload request that will be completed asynchronously
     */
    FileUploadRequest upload(@Nullable Context ctx, @Nullable FileUploadUserContext userContext,
                             @Nullable String filename, @Nullable String mimeType,
                             @NonNull InputStream stream);

    /**
     * Checks if this file upload provider supports log in (accounts).
     * @return does the file provider support logging in?
     */
    boolean canLogIn();

    /**
     * Checks if this file upload provider requires an account to upload files or the files can be
     * uploaded anonymously.
     * @return does the file provider require log in?
     */
    boolean isLogInRequired();

    /**
     * Gets the list of authenticated users.
     * @return the list of authenticated users
     */
    Collection<FileUploadUserContext> getLoggedInUsers();

    /**
     * Starts the log in flow and opens any necessary UIs.
     * @param ctx Android context used for the UIs
     */
    void startLogInFlow(Context ctx);

}
