package com.driveimpl;

import com.google.api.services.drive.model.File;
import com.storage.Storage;
import com.utils.Privilege;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class DriveStorage extends Storage {

    @Override
    public java.io.File getConfig(String path) throws Exception {
        File file = GoogleDrive.getFile(path);
        String fileID = file.getId();
        String fileName = "TEST";
        java.io.File config = new java.io.File("");  //TODO - za users i config
        OutputStream outputstream = new FileOutputStream(fileName);
        GoogleDrive.service.files().get(fileID).executeMediaAndDownloadTo(outputstream);
        outputstream.flush();
        outputstream.close();

        return null;
    }

    @Override
    public java.io.File getUsers(String path) throws Exception {
        return null;
    }

    @Override
    public void createStorage(String path, String storageName, String adminName, String adminPsw) throws Exception {

    }

    @Override
    public void editConfig(String path, String maxSize, String maxNumOfFiles, List<String> unsupportedFiles) throws Exception {

    }

    @Override
    public void editUsers(String path, String name, String password, Privilege privilege) throws Exception {

    }
}
