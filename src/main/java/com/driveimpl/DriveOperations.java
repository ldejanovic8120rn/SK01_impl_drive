package com.driveimpl;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.storage.Operations;
import com.utils.FileMetadata;
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
        String name = path.split("/")[path.split("/").length - 1];

        File dir = GoogleDrive.getFile(name);
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
        String name = path.split("/")[path.split("/").length - 1];

        File dir = GoogleDrive.getFile(name);
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
        path = StorageInfo.getStorageInfo().getConfig().getPath() + path;
        String name = path.split("/")[path.split("/").length - 1];

        File dir = GoogleDrive.getFile(name);
        String query = "parents=" + "'" + dir.getId() + "'";
        FileList list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();

        List<File> files = new ArrayList<>();
        List<FileMetadata> metadataList = new ArrayList<>();
        for (File file: list.getFiles()) {
            if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                metadataList.addAll(getAllFiles(file.getName()));
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
        String name = path.split("/")[path.split("/").length - 1];
        File driveFile = GoogleDrive.getFile(name);

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
        String parentName = toPath.split("/")[fromPath.split("/").length - 1];
        File parent = GoogleDrive.getFile(parentName);

        java.io.File uploadFile = new java.io.File(fromPath);
        AbstractInputStreamContent uploadStreamContent = new FileContent(null, uploadFile);

        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setParents(Arrays.asList(parent.getId()));

        GoogleDrive.service.files().create(fileMetadata, uploadStreamContent).setFields("id, webContentLink, webViewLink, parents").execute();
    }

    @Override
    public void moveFile(String fromPath, String toPath) throws Exception {
        String fileName = fromPath.split("/")[fromPath.split("/").length - 1];

        toPath = StorageInfo.getStorageInfo().getConfig().getPath() + toPath;
        String dirName = toPath.split("/")[fromPath.split("/").length - 1];

        File file = GoogleDrive.getFile(fileName);
        File dir = GoogleDrive.getFile(dirName);

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
