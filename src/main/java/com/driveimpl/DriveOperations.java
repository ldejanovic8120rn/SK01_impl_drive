package com.driveimpl;

import com.exception.ConfigException;
import com.exception.LogException;
import com.exception.PathException;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.storage.Operations;
import com.utils.FileMetadata;
import com.utils.Privilege;
import com.utils.StorageInfo;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DriveOperations extends Operations {

    @Override
    public List<FileMetadata> getAllFiles(String path) throws Exception {
        path = StorageInfo.getStorageInfo().getConfig().getPath() + path;

        if (!StorageInfo.getStorageInfo().checkUser(Privilege.ADMIN, Privilege.RDCD, Privilege.RD, Privilege.RO)) {
            throw new LogException("User isn't logged or doesn't have permission");
        }

        if (!DriveFileChecker.getDFC().ckeckStoragePath(path)) {
            throw new PathException("Path doesn't exist in storage");
        }

        File dir = GoogleDrive.getFile(path);
        String query = "parents=" + "'" + dir.getId() + "'";
        FileList list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();

        List<File> files = new ArrayList<>();
        for (File file: list.getFiles()) {
            if (!file.getMimeType().equals("application/vnd.google-apps.folder")) {
                files.add(file);
            }
        }

        return getFileMetadata(files);
    }

    @Override
    public List<FileMetadata> getAllDirectories(String path) throws Exception {
        path = StorageInfo.getStorageInfo().getConfig().getPath() + path;

        if (!StorageInfo.getStorageInfo().checkUser(Privilege.ADMIN, Privilege.RDCD, Privilege.RD, Privilege.RO)) {
            throw new LogException("User isn't logged or doesn't have permission");
        }

        if (!DriveFileChecker.getDFC().ckeckStoragePath(path)) {
            throw new PathException("Path doesn't exist in storage");
        }

        File dir = GoogleDrive.getFile(path);
        String query = "parents=" + "'" + dir.getId() + "'";
        FileList list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();

        List<File> files = new ArrayList<>();
        for (File file: list.getFiles()) {
            if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                files.add(file);
            }
        }

        return getFileMetadata(files);
    }

    @Override
    public List<FileMetadata> getAllFilesRecursive(String path) throws Exception {
        String dirPath = path;
        path = StorageInfo.getStorageInfo().getConfig().getPath() + path;

        if (!StorageInfo.getStorageInfo().checkUser(Privilege.ADMIN, Privilege.RDCD, Privilege.RD, Privilege.RO)) {
            throw new LogException("User isn't logged or doesn't have permission");
        }

        if (!DriveFileChecker.getDFC().ckeckStoragePath(path)) {
            throw new PathException("Path doesn't exist in storage");
        }

        File dir = GoogleDrive.getFile(path);
        String query = "parents=" + "'" + dir.getId() + "'";
        FileList list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();

        List<File> files = new ArrayList<>();
        List<FileMetadata> metadataList = new ArrayList<>();
        for (File file: list.getFiles()) {
            if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                metadataList.addAll(getAllFilesRecursive(dirPath + "/" + file.getName()));
            }
            else {
                files.add(file);
            }
        }

        metadataList.addAll(getFileMetadata(files));
        return metadataList;
    }

    @Override
    public void download(String path) throws Exception {
        path = StorageInfo.getStorageInfo().getConfig().getPath() + path;

        if (!StorageInfo.getStorageInfo().checkUser(Privilege.ADMIN, Privilege.RDCD, Privilege.RD)) {
            throw new LogException("User isn't logged or doesn't have permission");
        }

        if (!DriveFileChecker.getDFC().ckeckStoragePath(path)) {
            throw new PathException("Path doesn't exist in storage");
        }

        File driveFile = GoogleDrive.getFile(path);
        java.io.File file = new java.io.File(driveFile.getName());
        OutputStream outputstream = new FileOutputStream(file);
        com.driveimpl.GoogleDrive.service.files().get(driveFile.getId()).executeMediaAndDownloadTo(outputstream);
        outputstream.flush();
        outputstream.close();
    }

    @Override
    public void uploadFile(String fromPath, String toPath) throws Exception {
        String name = fromPath.split("/")[fromPath.split("/").length - 1];
        toPath = StorageInfo.getStorageInfo().getConfig().getPath() + toPath;

        if (!StorageInfo.getStorageInfo().checkUser(Privilege.ADMIN, Privilege.RDCD)) {
            throw new LogException("User isn't logged or doesn't have permission");
        }

        if (!DriveFileChecker.getDFC().ckeckPath(fromPath)) {
            throw new PathException("File doesn't exist!");
        }

        if (!DriveFileChecker.getDFC().ckeckStoragePath(toPath)) {
            throw new PathException("Path doesn't exist in storage");
        }

        String extension = name.split("\\.")[1];
        if (!DriveFileChecker.getDFC().ckeckExtention(extension)) {
            throw new ConfigException("Unsupported extension");
        }

        if (!DriveFileChecker.getDFC().checkNumOfFiles()) {
            throw new ConfigException("Exceeded number of files");
        }

        File parent = GoogleDrive.getFile(toPath);
        java.io.File uploadFile = new java.io.File(fromPath);
        if (!DriveFileChecker.getDFC().checkMaxSize(uploadFile.length())) {
            throw new ConfigException("Exceeded storage size");
        }

        AbstractInputStreamContent uploadStreamContent = new FileContent(null, uploadFile);

        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setParents(Arrays.asList(parent.getId()));

        GoogleDrive.service.files().create(fileMetadata, uploadStreamContent).setFields("id, webContentLink, webViewLink, parents").execute();
    }

    @Override
    public void moveFile(String fromPath, String toPath) throws Exception {
        fromPath = StorageInfo.getStorageInfo().getConfig().getPath() + fromPath;
        toPath = StorageInfo.getStorageInfo().getConfig().getPath() + toPath;

        if (!StorageInfo.getStorageInfo().checkUser(Privilege.ADMIN, Privilege.RDCD)) {
            throw new LogException("User isn't logged or doesn't have permission");
        }

        if (!DriveFileChecker.getDFC().ckeckStoragePath(fromPath)) {
            throw new PathException("Path doesn't exist in storage - fromPath");
        }

        if (!DriveFileChecker.getDFC().ckeckStoragePath(toPath)) {
            throw new PathException("Path doesn't exist in storage - toPath");
        }

        File file = GoogleDrive.getFile(fromPath);
        File dir = GoogleDrive.getFile(toPath);

        StringBuilder previousParents = new StringBuilder();
        for (String parent : file.getParents()) {
            previousParents.append(parent);
            previousParents.append(',');
        }

        GoogleDrive.service.files().update(file.getId(), null).setAddParents(dir.getId()).setRemoveParents(previousParents.toString()).setFields("id, parents").execute();
    }

    private List<FileMetadata> getFileMetadata(List<File> files) {
        List<FileMetadata> filesMD = new ArrayList<>();

        for (File file: files) {
            String name = file.getName();
            Date cd = new Date(file.getCreatedTime().getValue());
            Date lmd = new Date(file.getModifiedTime().getValue());

            FileMetadata fmd = new FileMetadata(name, cd, lmd);
            filesMD.add(fmd);
        }

        return filesMD;
    }
}
