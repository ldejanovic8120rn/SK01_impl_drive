package com.driveimpl;

import com.exception.PathException;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.storage.Delete;
import com.utils.StorageInfo;

public class DriveDelete extends Delete {

    @Override
    public void deleteDirectory(String directoryName) throws Exception {
        directoryName = StorageInfo.getStorageInfo().getConfig().getPath() + directoryName;

        if (!DriveFileChecker.getDFC().ckeckStoragePath(directoryName)) {
            throw new PathException("Directory doesn't exist!");
        }

        File child = GoogleDrive.getFile(directoryName);
        GoogleDrive.service.files().delete(child.getId()).execute();
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        fileName = StorageInfo.getStorageInfo().getConfig().getPath() + fileName;

        if (!DriveFileChecker.getDFC().ckeckStoragePath(fileName)) {
            throw new PathException("File doesn't exist!");
        }

        File child = GoogleDrive.getFile(fileName);
        GoogleDrive.service.files().delete(child.getId()).execute();
    }

    /**
     * By - Doktor Prof. Vuk Vukovic
     * */
    @Override
    public void deleteAll(String rootPath) throws Exception {
        File root = GoogleDrive.getRootFile(rootPath);
        String query = "parents=" + "'" + root.getId() + "'";
        FileList list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();

        for (File file: list.getFiles()) {
            if (!file.getName().equals("config.json") && !file.getName().equals("users.json")) {
                GoogleDrive.service.files().delete(file.getId()).execute();
            }
        }
    }
}
