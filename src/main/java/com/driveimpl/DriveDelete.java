package com.driveimpl;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.storage.Delete;
import com.utils.StorageInfo;

public class DriveDelete extends Delete {

    @Override
    public void deleteDirectory(String directoryName) throws Exception {
        directoryName = StorageInfo.getStorageInfo().getConfig().getPath() + directoryName;

        String name = directoryName.split("/")[directoryName.split("/").length - 1];
        String parentName = directoryName.split("/")[directoryName.split("/").length - 2];
        File child = GoogleDrive.getFileByParent(parentName, name);

        GoogleDrive.service.files().delete(child.getId()).execute();
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        fileName = StorageInfo.getStorageInfo().getConfig().getPath() + fileName;

        String name = fileName.split("/")[fileName.split("/").length - 1];
        String parentName = fileName.split("/")[fileName.split("/").length - 2];
        File child = GoogleDrive.getFileByParent(parentName, name);

        GoogleDrive.service.files().delete(child.getId()).execute();
    }

    @Override
    public void deleteAll(String rootPath) throws Exception {
        File root = GoogleDrive.getFile(rootPath);
        String query = "parents=" + "'" + root.getId() + "'";
        FileList list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents)").execute();

        for (File file: list.getFiles()) {
            if (!file.getName().equals("config.json") && !file.getName().equals("users.json")) {
                GoogleDrive.service.files().delete(file.getId()).execute();
            }
        }
    }
}
