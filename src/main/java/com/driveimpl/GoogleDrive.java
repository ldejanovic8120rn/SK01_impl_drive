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

    private static final String APPLICATION_NAME = "SK01GD-API";  //TODO - primeniti ime projekta
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

    public static File getFile(String name) {
        String query = "name=" + "'" + name + "'";
        FileList list = null;
        try {
            list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents)").execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (File file: list.getFiles()) {
            return file;
        }

        return null;
    }

    public static FileList getAllFiles() {
        FileList list = null;
        try {
            list = GoogleDrive.service.files().list().setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents)").execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static File getFileByParent(String parentName, String name) {
        File parent = getFile(parentName);
        String query = "name=" + "'" + name + "'";
        FileList list = null;
        try {
            list = GoogleDrive.service.files().list().setQ(query).setFields("nextPageToken, files(id, name, size, createdTime, mimeType, modifiedTime, parents)").execute();
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

}
