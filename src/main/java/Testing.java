import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class Testing {

    // /
    public static void main(String[] args) throws IOException {
        /**
         * NALAZENJE FAJLA
        **/
//        FileList list = GoogleDrive.service.files().list().setQ("name='test.json'").setFields("nextPageToken, files(id, name, createdTime, mimeType, modifiedTime, parents)").execute();
//        for (File file: list.getFiles()) {
//            System.out.println(file.getName() + " " + file.getMimeType());
//        }

        /**
         * KREIRANJE FOLDERA
         **/
//        String folderName = "TEST";
//        String folderIdParent = null;
//        File fileMetadata = new File();
//
//        fileMetadata.setName(folderName);
//        fileMetadata.setMimeType("application/vnd.google-apps.folder");
//
//        if (folderIdParent != null) {
//            List<String> parents = Arrays.asList(folderIdParent);
//
//            fileMetadata.setParents(parents);
//        }
//        Drive driveService = GoogleDrive.service;
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
//        Drive driveService = GoogleDrive.service;
//        driveService.files().create(fileMetadata, uploadStreamContent).setFields("id, webContentLink, webViewLink, parents").execute();
    }
}
