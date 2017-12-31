package io.mrarm.uploadlib.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.mrarm.uploadlib.FileUploadProvider;
import io.mrarm.uploadlib.FileUploadUserContext;

/**
 * A simple class that can be used to store users. After each add or remove operation, the table
 * will be immediately persisted on the disk (not asynchronously).
 * @param <T>
 */
public class UserStorageManager<T extends FileUploadUserContext & Serializable> {

    private static final String DEFAULT_FILENAME = "users.ser";

    private Set<T> users = new HashSet<>();

    private File filePath;
    private boolean canCreateDir = false;

    /**
     * Creates the manager with the specified file as the storage.
     * @param dbPath the path to the file in which the user data will be saved
     */
    public UserStorageManager(File dbPath) {
        filePath = dbPath;
        StorageHelper.checkFileState(dbPath);
        load();
    }

    public UserStorageManager(StorageManager storageManager, FileUploadProvider provider) {
        this(new File(storageManager.getDataDir(provider), DEFAULT_FILENAME));
        canCreateDir = true;
    }

    /**
     * Gets the user list.
     * @return a copy of the user list
     */
    public synchronized List<T> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Adds an user to the list and saves the list.
     * @param user the user to add
     */
    public synchronized void addUser(T user) {
        users.add(user);
        store();
    }

    /**
     * Removes an user from the list and saves the list.
     * @param user the user to remove
     */
    public synchronized void removeUser(T user) {
        users.remove(user);
        store();
    }

    @SuppressWarnings("unchecked")
    private synchronized void load() {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(filePath);
            BufferedInputStream bufferedStream = new BufferedInputStream(stream);
            ObjectInputStream objectStream = new ObjectInputStream(bufferedStream);
            users = (Set<T>) objectStream.readObject();
            objectStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            StorageHelper.closeSilently(stream);
        }
    }

    private synchronized void store() {
        if (canCreateDir)
            filePath.getParentFile().mkdir();
        StorageHelper.startFileOperation(filePath);
        boolean success = false;
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(filePath);
            BufferedOutputStream bufferedStream = new BufferedOutputStream(stream);
            ObjectOutputStream objectStream = new ObjectOutputStream(bufferedStream);
            objectStream.writeObject(users);
            objectStream.close();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StorageHelper.closeSilently(stream);
            if (success)
                StorageHelper.finishFileOperation(filePath);
            else
                StorageHelper.abortFileOperation(filePath);
        }
    }

}
