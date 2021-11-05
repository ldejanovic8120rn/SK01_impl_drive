package com.driveimpl;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.storage.FileChecker;
import com.utils.StorageInfo;

import java.io.IOException;

public class DriveFileChecker extends FileChecker {

    @Override
    public boolean ckeckPath(String path) {
        java.io.File file = new java.io.File(path);
        return file.exists();
    }

    // Storage/A/B/C/fajl.txt
    @Override
    public boolean ckeckStoragePath(String path) {
        String[] files = path.split("/");
        File root = GoogleDrive.getFile(files[0]);

        for (int i = 1; i < files.length; i++) {
            String query = "name='" + files[i] + "'";
            FileList list = null;
            try {
                list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents)").execute();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if (list == null) {
                return false;
            }

            File child = null;
            for (File file: list.getFiles()) {
                if (file.getParents().get(0).equals(root.getId())) {
                    child = file;
                    break;
                }
            }

            if (child == null) {
                return false;
            }

            root = child;
        }

        return true;
    }

    @Override
    public boolean checkNumOfFiles() {
        return false;
    }

    @Override
    public boolean checkMaxSize(long size) {
        return false;
    }

    @Override
    public boolean ckeckExtention(String extension) {
        return false;
    }
}
