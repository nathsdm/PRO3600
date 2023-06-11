package com.example.testtest;



import android.content.Context;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleAuth {

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_APPDATA);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, Context context) throws IOException {
        // Load client secrets.
        InputStream in = context.getAssets().open(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(context.getFilesDir(), TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }


    public static Drive service_Setup(Context context) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, context))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return (service);
    }

    public static FileList get_Appdata_files (Drive service, String outputPath) throws IOException, GeneralSecurityException {
        try {
            FileList files = service.files().list()
                    .setQ("mimeType='text/txt'")
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            for (File file : files.getFiles()) {
                System.out.printf("Found file: %s (%s)\n",
                        file.getName(), file.getId());
                String outputPath_complete = outputPath + file.getName();
                System.out.println(outputPath_complete);

                java.io.File outputFile = new java.io.File(outputPath_complete);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                OutputStream outputStream = new FileOutputStream(outputFile);

                // Download the file content and write it to the output stream
                service.files().get(file.getId()).executeMediaAndDownloadTo(outputStream);
            }
            return files;
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to list files: " + e.getDetails());
            throw e;
        }
    }


    public static FileList list_Appdata_files (Drive service) throws IOException, GeneralSecurityException{
        try {
            FileList files = service.files().list()
                    .setQ("mimeType='text/txt'")
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            return files;
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to list files: " + e.getDetails());
            throw e;
        }
    }

    public static void send_Appdata_files (Drive service, String filename, String filepath_origin) throws IOException, GeneralSecurityException{
        try {
            // File's metadata.
            File fileMetadata = new File();
            fileMetadata.setName(filename);
            fileMetadata.setParents(Collections.singletonList("appDataFolder"));
            java.io.File filePath = new java.io.File(filepath_origin);
            FileContent mediaContent = new FileContent("text/txt", filePath);
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File ID: " + file.getId());
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to create file: " + e.getDetails());
            throw e;
        }
    }

    public static void delete_Appdata_files (Drive service) throws IOException, GeneralSecurityException{
        try {
            FileList files = service.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            for (File file : files.getFiles()){
                service.files().delete(file.getId()).execute();
            }
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to list files: " + e.getDetails());
            throw e;
        }
    }


}