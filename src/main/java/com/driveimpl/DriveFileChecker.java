package com.driveimpl;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.storage.FileChecker;
import com.utils.StorageInfo;

import java.io.IOException;

public class DriveFileChecker extends FileChecker {

    private DriveFileChecker() {}

    private static final class DriveFileCheckerHolder {
        static final DriveFileChecker driveFileChecker = new DriveFileChecker();
    }

    public static DriveFileChecker getDFC() {
        return DriveFileCheckerHolder.driveFileChecker;
    }

    @Override
    public boolean ckeckPath(String path) {
        java.io.File file = new java.io.File(path);
        return file.exists();
    }

    @Override
    public boolean ckeckStoragePath(String path) {
        String[] files = path.split("/");
        File root = GoogleDrive.getRootFile(files[0]);

        for (int i = 1; i < files.length; i++) {
            String query = "name='" + files[i] + "'";
            FileList list = null;
            try {
                list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();
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
        if (StorageInfo.getStorageInfo().getConfig().getMaxNumOfFiles().equals("UN")) {
            return true;
        }

        int counter = 0;
        try {
            counter = countFiles(StorageInfo.getStorageInfo().getConfig().getPath(), counter);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return counter + 1 <= Integer.parseInt(StorageInfo.getStorageInfo().getConfig().getMaxNumOfFiles());
    }

    @Override
    public boolean checkMaxSize(long size) {
        if (StorageInfo.getStorageInfo().getConfig().getMaxSize().equals("UN")) {
            return true;
        }

        File root = GoogleDrive.getRootFile(StorageInfo.getStorageInfo().getConfig().getPath());
        return root.getSize() + size <= Long.parseLong(StorageInfo.getStorageInfo().getConfig().getMaxSize());
    }

    @Override
    public boolean ckeckExtention(String extension) {
        return StorageInfo.getStorageInfo().getConfig().getUnsupportedFiles().contains(extension);
    }

    private int countFiles(String name, int counter) throws Exception {
        File root = GoogleDrive.getRootFile(name);
        String query = "parents='" + root.getId() + "'";
        FileList list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();

        for (File file: list.getFiles()) {
            if (!file.getMimeType().equals("application/vnd.google-apps.folder")) {
                counter = countFiles(file.getName(), counter);
            }
            else {
                counter++;
            }
        }

        return counter;
    }
}
