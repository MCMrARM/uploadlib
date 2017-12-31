package io.mrarm.uploadlib.storage;

import android.content.Context;

/**
 * A simple implementation of the StorageManger, that stores all the files in a directory in the
 * app's data directory.
 */
public class AndroidStorageManager extends DirectoryStorageManager {

    public static final String DEFAULT_DIR_NAME = "uploadlib";

    /**
     * Creates an AndroidStorageManager with the following context and a custom directory name for
     * file storage.
     * The path to the directory will be obtained by calling context.getDir, with mode MODE_PRIVATE.
     * @param context any Android context
     * @param directoryName the custom directory name
     */
    public AndroidStorageManager(Context context, String directoryName) {
        super(context.getDir(directoryName, Context.MODE_PRIVATE));
    }

    /**
     * Creates an AndroidStorageManager with the following context and the default directory name.
     * The default directory name is set with the DEFAULT_DIR_NAME field.
     * @param context any Android context
     */
    public AndroidStorageManager(Context context) {
        this(context, DEFAULT_DIR_NAME);
    }

}
