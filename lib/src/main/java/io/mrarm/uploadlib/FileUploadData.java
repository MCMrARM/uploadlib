package io.mrarm.uploadlib;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple filesystem-backed UploadData implementation.
 */
public class FileUploadData implements UploadData {

    private File file;
    private String mimetype;

    public FileUploadData(File file, String mimetype) {
        this.file = file;
        this.mimetype = mimetype;
    }

    @Nullable
    @Override
    public String getName() {
        return file.getName();
    }

    @Nullable
    @Override
    public String getMimeType() {
        return mimetype;
    }

    @NonNull
    @Override
    public InputStream open() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public long size() {
        return file.length();
    }

}
