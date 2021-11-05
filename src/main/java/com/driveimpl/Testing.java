package com.driveimpl;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;
import com.utils.StorageInfo;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Testing {

    public static void main(String[] args) throws IOException {
        /**
         * NALAZENJE FAJLA
        **/
//        FileList list = com.driveimpl.GoogleDrive.service.files().list().setQ("name='MyStorage'").setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents)").execute();
//        for (File file: list.getFiles()) {
//            String query = "parents=" + "'" + file.getId() + "'";
//            FileList list1 = com.driveimpl.GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents)").execute();
//
//            for (File file1: list1.getFiles()) {
//                System.out.println(file1.getName());
//            }
//
//            break;
//        }


        /**
         * KREIRANJE FOLDERA
         **/
//        String folderName = "nesto";
//        String folderIdParent = null;
//        File fileMetadata = new File();
//
//        fileMetadata.setName(folderName);
//        //fileMetadata.setMimeType("application/vnd.google-apps.folder");
//
//        if (folderIdParent != null) {
//            List<String> parents = Arrays.asList(folderIdParent);
//
//            fileMetadata.setParents(parents);
//        }
//        Drive driveService = com.driveimpl.GoogleDrive.service;
//        driveService.files().create(fileMetadata).setFields("id, name").execute();


        /**
         * UPLOAD FAJLA
         **/
//        java.io.File uploadFile = new java.io.File("/Users/lazardejanovic/Downloads/proba.txt");
//        AbstractInputStreamContent uploadStreamContent = new FileContent(null, uploadFile);
//        File parent = GoogleDrive.getFile("TEST");
//
//        File fileMetadata = new File();
//        fileMetadata.setName("proba.txt");
//        fileMetadata.setParents(Arrays.asList(parent.getId()));
//
//        GoogleDrive.service.files().create(fileMetadata, uploadStreamContent).setFields("id, webContentLink, webViewLink, parents").execute();


        /**
         * DOWNLOAD FAJLA
         **/
        FileList list = com.driveimpl.GoogleDrive.service.files().list().setQ("name='empty.txt'")
                .setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents)").execute();
        String fileID = null;
        for (File file: list.getFiles()) {
            fileID = file.getId();
            break;
            //System.out.println(file.getName() + " " + file.getSize() + " " + file.getCreatedTime() + " " + file.getModifiedTime());
        }
//
        String fileName = "empty.txt";
        java.io.File config = new java.io.File(fileName);  //TODO - za users i config
        OutputStream outputstream = new FileOutputStream(config);
        com.driveimpl.GoogleDrive.service.files().get(fileID).executeMediaAndDownloadTo(outputstream);
        outputstream.flush();
        outputstream.close();


        /**
         * BRISANJE FAJLA
         **/
//        FileList list = GoogleDrive.service.files().list().setQ("name='FolderZaBrisanje'").setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents)").execute();
//        String fileID = null;
//        for (File file: list.getFiles()) {
//            fileID = file.getId();
//            System.out.println(file.getName() + " " + file.getMimeType());
//        }
//
//        GoogleDrive.service.files().delete(fileID).execute();




//        Map<String, Object> configMap = new HashMap<>();
//        configMap.put("path", "Skladiste");
//        configMap.put("admin", "admin");
//        configMap.put("maxSize", 20);
//        configMap.put("maxNumOfFiles", 20);
//        configMap.put("unsupportedFiles", null);
//
//        java.io.File config = new java.io.File("config.json");
//        try {
//            Writer writer = new FileWriter(config);
//            new Gson().toJson(configMap, writer);
//            writer.close();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        config.delete();
    }
}
