package saoif;
/**
 *
 * @author Windows
 */
import java.io.*;
import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class FileDownloader {
    private int start;
    private int end;
    private String type;
    
    
    public FileDownloader(){
        System.out.println("Please input the type of data you want to download(pic / web):");
        Scanner s = new Scanner(System.in);
        type = s.nextLine();
        System.out.println("Please input the card numbers you want to download (from start to end)");
        System.out.printf("start = ");
        start = s.nextInt();
        System.out.printf("end = ");
        end = s.nextInt();
    }
    
    public FileDownloader(int s, int e, String t){
        start = s;
        end = e;
        type = t;
    }
    
    
    public static void downloadFile(String fileURL, String savePath,int i) throws Exception {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
        httpConn.setRequestMethod("GET");
        
        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            try (BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
                    FileOutputStream out = new FileOutputStream(savePath)){
                byte[] buffer = new byte[1024];
                int bytesRead;
                
                while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1){
                    out.write(buffer, 0, bytesRead);
                }
                
                System.out.println("File "+i+" downloaded successfully!");
            }
        }else{
            System.out.println("No file to download. Server replied HTTP code: "+responseCode);
        }
        httpConn.disconnect();
    }
    
    
    
    public void download(){
        if (!(type.equals("pic")) && !(type.equals("web"))){
            System.err.println("Wrong input");
            System.exit(0);
        }
        
        if (type.equals("pic")){
        
            for (int i = start; i<=end; i++){
                String fileURL = "https://saoif.fanadata.com/assets/characters/artwork/character_".concat(String.valueOf(i)).concat(".jpg");
                String  savePath = "pic".concat(File.separator).concat(String.valueOf(i)).concat(".jpg");
            
                try{
                    copyFile("no_data.jpg", savePath);
                    downloadFile(fileURL, savePath, i);
                }catch(Exception e){
                    System.err.println(e);
                }
                System.gc();
            }
        }
        if (type.equals("web")){
            for (int i = start; i<=end; i++){
                String fileURL = "https://saoif.fanadata.com/characters/".concat(String.valueOf(i));
                String  savePath = "web".concat(File.separator).concat(String.valueOf(i)).concat(".html");
            
                try{
                    downloadFile(fileURL, savePath, i);
                }catch(Exception e){
                    System.err.println(e);
                }
                System.gc();
            }
        }
    }
    
    public static void copyFile(String sourcePath, String destinationPath) {
        Path source = Path.of(sourcePath);
        Path destination = Path.of(destinationPath);
        
        try {
            // Copy the file from source to destination
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            //System.out.println("File copied successfully from " + sourcePath + " to " + destinationPath);
        } catch (IOException e) {
            System.err.println("Error occurred while copying the file: " + e.getMessage());
        }
    }
    
    public static void delete(String folderPath) {
        Path folder = Paths.get(folderPath);
        
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
            for (Path filePath : directoryStream) {
                // Check if it is a file (not a directory) before deleting
                if (Files.isRegularFile(filePath)) {
                    Files.delete(filePath);
                    //System.out.println("Deleted file: " + filePath);
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred while deleting files: " + e.getMessage());
        }
    }
}
