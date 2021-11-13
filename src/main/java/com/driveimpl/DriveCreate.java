package com.driveimpl;

import com.exception.ConfigException;
import com.google.api.services.drive.model.File;
import com.storage.Create;
import com.utils.StorageInfo;

import java.util.Arrays;

public class DriveCreate extends Create {

    @Override
    public void saveDirectory(String directoryName) throws Exception {
        directoryName = StorageInfo.getStorageInfo().getConfig().getPath() + directoryName;
        String name = directoryName.split("/")[directoryName.split("/").length - 1];

        File parent = GoogleDrive.getFile(getPath(directoryName));
        File fileMetadata = new File();

        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Arrays.asList(parent.getId()));

        GoogleDrive.service.files().create(fileMetadata).setFields("id, name").execute();
    }

    @Override
    public void saveFile(String fileName) throws Exception {
        fileName = StorageInfo.getStorageInfo().getConfig().getPath() + fileName;
        String name = fileName.split("/")[fileName.split("/").length - 1];

        File parent = GoogleDrive.getFile(getPath(fileName));
        File fileMetadata = new File();

        fileMetadata.setName(name);
        fileMetadata.setParents(Arrays.asList(parent.getId()));

        if (!DriveFileChecker.getDFC().ckeckExtention(fileMetadata.getFileExtension())) {
            throw new ConfigException("Unsupported extension");
        }

        if (!DriveFileChecker.getDFC().checkNumOfFiles()) {
            throw new ConfigException("Exceeded number of files");
        }

        if (!DriveFileChecker.getDFC().checkMaxSize(fileMetadata.getSize())) {
            throw new ConfigException("Exceeded storage size");
        }

        GoogleDrive.service.files().create(fileMetadata).setFields("id, name").execute();
    }

    private String getPath(String path) {
        String parts[] = path.split("/");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length - 1; i++) {
            sb.append(parts[i]);
            if (i != parts.length - 2) {
                sb.append("/");
            }
        }

        return sb.toString();
    }
}
