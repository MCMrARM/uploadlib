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

import io.mrarm.uploadlib.FileUploadUserContext;

/**
 * A simple class that can be used to store users. After each add or remove operation, the table
 * will be immediately persisted on the disk (not asynchronously).
 * @param <T>
 */
public class UserStorageManager<T extends FileUploadUserContext & Serializable> {

    private Set<T> users = new HashSet<>();

    private File mFilePath;

    /**
     * Creates the manager with the specified file as the storage.
     * @param dbPath the path to the file in which the user data will be saved
     */
    UserStorageManager(File dbPath) {
        mFilePath = dbPath;
        StorageHelper.checkFileState(dbPath);
        load();
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
        try {
            FileInputStream stream = new FileInputStream(mFilePath);
            BufferedInputStream bufferedStream = new BufferedInputStream(stream);
            ObjectInputStream objectStream = new ObjectInputStream(bufferedStream);
            users = (Set<T>) objectStream.readObject();
            objectStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private synchronized void store() {
        StorageHelper.startFileOperation(mFilePath);
        boolean success = false;
        try {
            FileOutputStream stream = new FileOutputStream(mFilePath);
            BufferedOutputStream bufferedStream = new BufferedOutputStream(stream);
            ObjectOutputStream objectStream = new ObjectOutputStream(bufferedStream);
            objectStream.writeObject(users);
            objectStream.close();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (success)
                StorageHelper.finishFileOperation(mFilePath);
            else
                StorageHelper.abortFileOperation(mFilePath);
        }
    }

}
