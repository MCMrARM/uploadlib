package io.mrarm.uploadlib.storage;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class StorageHelper {

    /**
     * Closes the specified stream silently.
     * @param stream the stream to close
     */
    public static void closeSilently(@Nullable Closeable stream) {
        if (stream == null)
            return;
        try {
            stream.close();
        } catch (IOException e) {
            Log.w("StorageHelper", "closeSilently: IOException");
            e.printStackTrace();
        }
    }

    /**
     * Deletes recursively a directory.
     * @param file the directory to delete recursively
     */
    public static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null)
                return;
            for (File ch : files)
                deleteRecursive(ch);
        } else {
            file.delete();
        }
    }

    /**
     * This is the function that you should call when starting the application/class before reading
     * the file. This function allows proper rollback of a file operation.
     */
    public static void checkFileState(File path) {
        File rollback = getRollbackFilePath(path);
        if (rollback.exists()) {
            path.delete();
            rollback.renameTo(path);
        }
    }

    /**
     * Starts a file write operation. The current file will be deleted (moved as the rollback file)
     * and you will be able to safely write data to it. After you are done, you should call
     * finishFileOperation. No two concurrent file operations are allowed.
     * For the rollback feature to work you must call .checkFileState before reading the file on
     * startup.
     * @param path the file path
     */
    public static void startFileOperation(File path) {
        File rollback = getRollbackFilePath(path);
        if (rollback.exists())
            Log.e("StorageHelper", "The rollback file already exists for: " + path.getAbsolutePath());
        path.renameTo(rollback);
    }

    /**
     * Finishes a file write operation: deletes the rollback file.
     * @param path the file path
     */
    public static void finishFileOperation(File path) {
        File rollback = getRollbackFilePath(path);
        rollback.delete();
    }

    /**
     * Reverts the file to the original state.
     * @param path the file path
     */
    public static void abortFileOperation(File path) {
        checkFileState(path);
    }


    private static File getRollbackFilePath(File file) {
        return new File(file.getParent(), file.getName() + ".rollback");
    }

}
