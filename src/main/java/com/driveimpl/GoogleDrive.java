package com.driveimpl;

import com.StorageManager;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class GoogleDrive {

    private static final String APPLICATION_NAME = "SK01GD-API";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT;
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);
    protected static Drive service;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = getDriveService();
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }

        StorageManager.registerStorage(new DriveStorage(), new DriveCreate(), new DriveDelete(), new DriveOperations());
    }

    private GoogleDrive() {}

    public static Credential authorize() throws IOException {
        InputStream in = GoogleDrive.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setAccessType("offline").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

    public static File getRootFile(String name) {
        String query = "name=" + "'" + name + "'";
        FileList list = null;
        try {
            list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (File file: list.getFiles()) {
            return file;
        }

        return null;
    }

    public static File getFileByParent(String parentName, String name) {
        File parent = getRootFile(parentName);
        String query = "name=" + "'" + name + "'";
        FileList list = null;
        try {
            list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        for (File file: list.getFiles()) {
            if (file.getParents().get(0).equals(parent.getId())) {
                return file;
            }
        }

        return null;
    }

    /**
     * By - Doktor Prof. Lazar Dejanovic
     */
    public static File getFile(String path) {  //npr path: MyStorage/Folder1/Folder2/Folder3 - dolazimo do Foldera3 od korena
        String[] files = path.split("/");
        File root = GoogleDrive.getRootFile(files[0]);  //MyStorage

        for (int i = 1; i < files.length; i++) {  //prolazimo prvo za Folder1, pa Folder2...
            String query = "name='" + files[i] + "'";
            FileList list = null;
            try {
                list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents, fileExtension)").execute();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if (list == null) {
                return null;
            }

            File child = null;
            for (File file: list.getFiles()) {  //nalazimo dete npr. Folder1, tako sto poredimo id parent-a
                if (file.getParents().get(0).equals(root.getId())) {
                    child = file;
                    break;
                }
            }

            if (child == null) {
                return null;
            }

            root = child;
        }

        return root;
    }

}
