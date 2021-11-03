package com.driveimpl;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;

public class Testing {

    // /
    public static void main(String[] args) throws IOException {
        /**
         * NALAZENJE FAJLA
        **/
//        FileList list = com.driveimpl.GoogleDrive.service.files().list().setQ("name='test.json'").setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents)").execute();
//        for (File file: list.getFiles()) {
//            System.out.println(file.getName() + " " + file.getMimeType());
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
//        java.io.File uploadFile = new java.io.File("/Users/lazardejanovic/Downloads/test.json");
//        AbstractInputStreamContent uploadStreamContent = new FileContent(null, uploadFile);
//
//        File fileMetadata = new File();
//        fileMetadata.setName("test.json");
//        fileMetadata.setParents(null);
//
//        Drive driveService = com.driveimpl.GoogleDrive.service;
//        driveService.files().create(fileMetadata, uploadStreamContent).setFields("id, webContentLink, webViewLink, parents").execute();


        /**
         * DOWNLOAD FAJLA
         **/
//        FileList list = com.driveimpl.GoogleDrive.service.files().list().setQ("name='TEST'").setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents)").execute();
//        String fileID = null;
//        for (File file: list.getFiles()) {
//            fileID = file.getId();
//            System.out.println(file.getName() + " " + file.getMimeType());
//        }
//
//        String fileName = "TEST";
//        java.io.File config = new java.io.File("");  //TODO - za users i config
//        OutputStream outputstream = new FileOutputStream(fileName);
//        com.driveimpl.GoogleDrive.service.files().get(fileID).executeMediaAndDownloadTo(outputstream);
//        outputstream.flush();
//        outputstream.close();

        FileList list = GoogleDrive.service.files().list().setQ("name='komponente.txt'").setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents)").execute();
        String fileID = null;
        for (File file: list.getFiles()) {

            fileID = file.getId();
            System.out.println(file.getName() + " " + file.getMimeType());
        }

        GoogleDrive.service.files().delete(fileID).execute();
    }
}
