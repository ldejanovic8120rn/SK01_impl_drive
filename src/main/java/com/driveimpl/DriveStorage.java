package com.driveimpl;

import com.exception.LogException;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.storage.Storage;
import com.utils.Privilege;
import com.utils.StorageInfo;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriveStorage extends Storage {

    @Override
    public java.io.File getConfig(String path) throws Exception {
        File file = GoogleDrive.getFile(path + "/config.json");
        String fileID = file.getId();

        java.io.File config = new java.io.File("config.json");
        OutputStream outputstream = new FileOutputStream(config);
        GoogleDrive.service.files().get(fileID).executeMediaAndDownloadTo(outputstream);
        outputstream.flush();
        outputstream.close();

        return config;
    }

    @Override
    public java.io.File getUsers(String path) throws Exception {
        File file = GoogleDrive.getFile(path + "/users.json");
        String fileID = file.getId();

        java.io.File users = new java.io.File("users.json");
        OutputStream outputstream = new FileOutputStream(users);
        GoogleDrive.service.files().get(fileID).executeMediaAndDownloadTo(outputstream);
        outputstream.flush();
        outputstream.close();

        return users;
    }

    @Override
    public void createStorage(String path, String storageName, String adminName, String adminPsw) throws Exception {
        if (StorageInfo.getStorageInfo().getUser().isLogged()) {
            throw new LogException("User must be logged, before creating storage");
        }

        File parent = GoogleDrive.getFile(path);  //uzimamo roditelja kod kog cemo da kreiramo skladiste
        File fileMetadata = new File();

        fileMetadata.setName(storageName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        if (parent == null) {  //My Drive je parent
            fileMetadata.setParents(null);
        }
        else {
            fileMetadata.setParents(Arrays.asList(parent.getId()));
        }

        GoogleDrive.service.files().create(fileMetadata).setFields("id, name").execute();

        java.io.File configFile = new java.io.File("config.json");
        java.io.File usersFile = new java.io.File("users.json");

        initConfig(configFile, storageName, adminName);  //napravimo config.json
        initUsers(usersFile, adminName, adminPsw);  //napravimo users.json

        createSettings(configFile, storageName);  //sacuvamo na drive-u config.json
        createSettings(usersFile, storageName);  //sacuvamo na drive-u config.json
    }

    @Override
    public void editConfig(String storageName, String maxSize, String maxNumOfFiles, List<String> unsupportedFiles) throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("path", storageName);
        configMap.put("admin", StorageInfo.getStorageInfo().getUser().getName());
        configMap.put("maxSize", (maxSize != null) ? maxSize : StorageInfo.getStorageInfo().getConfig().getMaxSize());
        configMap.put("maxNumOfFiles", (maxNumOfFiles != null) ? maxNumOfFiles : StorageInfo.getStorageInfo().getConfig().getMaxNumOfFiles());
        configMap.put("unsupportedFiles", (unsupportedFiles != null) ? unsupportedFiles : StorageInfo.getStorageInfo().getConfig().getUnsupportedFiles());

        java.io.File config = new java.io.File("config.json");
        try {
            Writer writer = new FileWriter(config);
            new Gson().toJson(configMap, writer);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        File configDrive = GoogleDrive.getFile(storageName + "/config.json");  //uzmemo file sa drive-a
        GoogleDrive.service.files().delete(configDrive.getId()).execute();  //obrisemo ga
        createSettings(config, storageName);  //dodamo izmenjeni
        config.delete();  //obrisemo ga lokalno
    }

    @Override
    public void editUsers(String storageName, String name, String password, Privilege privilege) throws Exception {
        JsonObject user = new JsonObject();
        user.addProperty("name", name);
        user.addProperty("password", password);
        user.addProperty("privilege", String.valueOf(privilege));

        java.io.File users = getUsers(storageName);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(users));
            String line = bufferedReader.readLine();
            line = line.replace("]", "");

            Writer writer = new FileWriter(users, false);
            writer.append(line);
            writer.append(",");

            new Gson().toJson(user, writer);
            writer.append("]");
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        File usersDrive = GoogleDrive.getFile(storageName + "/users.json");  //uzmemo file sa drive-a
        GoogleDrive.service.files().delete(usersDrive.getId()).execute();  //obrisemo ga
        createSettings(users, storageName);  //dodamo izmenjeni
        users.delete();  //obrisemo ga lokalno
    }

    private void initConfig(java.io.File configFile, String path, String adminName) {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("path", path);
        configMap.put("admin", adminName);
        configMap.put("maxSize", "UN");
        configMap.put("maxNumOfFiles", "UN");
        configMap.put("unsupportedFiles", null);

        try {
            Writer writer = new FileWriter(configFile);
            new Gson().toJson(configMap, writer);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUsers(java.io.File usersFile, String adminName, String adminPsw) {
        JsonObject user = new JsonObject();
        user.addProperty("name", adminName);
        user.addProperty("password", adminPsw);
        user.addProperty("privilege", String.valueOf(Privilege.ADMIN));

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(user);
        try {
            Writer writer = new FileWriter(usersFile);
            new Gson().toJson(jsonArray, writer);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSettings(java.io.File settings, String storageName) {
        AbstractInputStreamContent uploadStreamContent = new FileContent(null, settings);
        File parent = GoogleDrive.getRootFile(storageName);

        File fileMetadata = new File();
        fileMetadata.setName(settings.getName());
        fileMetadata.setParents(Arrays.asList(parent.getId()));

        try {
            GoogleDrive.service.files().create(fileMetadata, uploadStreamContent).setFields("id, webContentLink, webViewLink, parents").execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
