package com.driveimpl;

import com.google.api.services.drive.model.File;

import java.io.*;

public class Testing {

    public static void main(String[] args) throws IOException {
        /**
         * NALAZENJE FAJLA
        **/
//        FileList list = com.driveimpl.GoogleDrive.service.files().list().setQ("name='novi.txt'").setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();
//        for (File file: list.getFiles()) {
//            System.out.println(file.getFileExtension());
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
//        java.io.File uploadFile = new java.io.File("/Users/lazardejanovic/Downloads/novi.txt");
//        AbstractInputStreamContent uploadStreamContent = new FileContent(null, uploadFile);
//        File parent = GoogleDrive.getFile("TEST");
//
//        File fileMetadata = new File();
//        fileMetadata.setName("novi.txt");
//        fileMetadata.setParents(Arrays.asList(parent.getId()));
//
//        GoogleDrive.service.files().create(fileMetadata, uploadStreamContent).setFields("id, webContentLink, webViewLink, parents").execute();

//        File tmp = GoogleDrive.getFile("test.json");
//        File parent1 = GoogleDrive.getFile("TEST1");
//
//        StringBuilder previousParents = new StringBuilder();
//        for (String parent : tmp.getParents()) {
//            previousParents.append(parent);
//            previousParents.append(',');
//        }
//// Move the file to the new folder
//        GoogleDrive.service.files().update(tmp.getId(), null)
//                .setAddParents(parent1.getId())
//                .setRemoveParents(previousParents.toString())
//                .setFields("id, parents")
//                .execute();



        /**
         * DOWNLOAD FAJLA
         **/
//        FileList list = com.driveimpl.GoogleDrive.service.files().list().setQ("name='empty.txt'")
//                .setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents)").execute();
//        String fileID = null;
//        for (File file: list.getFiles()) {
//            fileID = file.getId();
//            break;
//            //System.out.println(file.getName() + " " + file.getSize() + " " + file.getCreatedTime() + " " + file.getModifiedTime());
//        }
////
//        String fileName = "empty.txt";
//        java.io.File config = new java.io.File(fileName);  //TODO - za users i config
//        OutputStream outputstream = new FileOutputStream(config);
//        com.driveimpl.GoogleDrive.service.files().get(fileID).executeMediaAndDownloadTo(outputstream);
//        outputstream.flush();
//        outputstream.close();


        /**
         * BRISANJE FAJLA
         **/
//        FileList list = GoogleDrive.service.files().list().setQ("name='ovajsigurnonepostoji'").setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents)").execute();
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


        File file = GoogleDrive.getFile("MyStorage/dir2/config.json");
        System.out.println("PARENT - " + file.getParents());
    }
}
