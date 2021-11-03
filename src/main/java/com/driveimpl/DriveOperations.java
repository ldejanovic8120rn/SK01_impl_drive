package com.driveimpl;

import com.storage.Operations;
import com.utils.FileMetadata;

import java.util.List;

public class DriveOperations extends Operations {

    @Override
    public List<FileMetadata> getAllFiles(String path) throws Exception {
        return null;
    }

    @Override
    public List<FileMetadata> getAllDirectories(String path) throws Exception {
        return null;
    }

    @Override
    public List<FileMetadata> getAllFilesRecursive(String path) throws Exception {
        return null;
    }

    @Override
    public void download(String path) throws Exception {

    }

    @Override
    public void uploadFile(String fromPath, String toPath) throws Exception {

    }

    @Override
    public void moveFile(String fromPath, String toPath) throws Exception {
        // TODO - brisanje trenutnog roditelja i dodavanje novog roditelja
    }
}
