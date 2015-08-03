package uwpreloader;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * Created by Fechner on 11/28/14.
 */
public class Downloader {

    static final String kCatalogUrl = "https://api.unfoldingword.org/uw/txt/2/catalog.json";
    static final String kLocalesUrl = "http://td.unfoldingword.org/exports/langnames.json";
    
    static final String kDirName = "assets/preloaded_content";
    
    static final String kFileName = "preloaded_data.json";
    static final String kLocalesFileName = "locales_data.json";
    
    public static void updateData(){
        
        purgeDirectory(kDirName);
        
        String json = downloadString(kCatalogUrl);
        saveFile(json, kFileName);
        
        String localesJson = downloadString(kLocalesUrl);
        saveFile(localesJson, kLocalesFileName);
        saveUrlsForJson(json);
    }
    
    private static void purgeDirectory(String directory){
        
        System.out.println("Will try to purge dir: " + directory);
        File dir = new File(directory);
        if(!dir.exists()){
            return;
        }
        
         for (File file: dir.listFiles()){ 
             System.out.println("Name: " + file.getName());
             if (!file.isDirectory() && (file.getName().equals(kFileName) 
                     || file.getName().equals(kLocalesFileName) 
                     || file.getName().contains("json")
                     || file.getName().contains("usfm")
                     || file.getName().contains("sig"))){
                 file.delete();
             }
         }
    }
   
    private static void saveUrlsForJson(String json){
        
        ArrayList<String> allUrls = UWJsonUrlFinder.getAllUrlsForJson(json);
        
        for(String url : allUrls){
            byte[] text = downloadBytes(url);
            if(text != null){
                saveFile(text, prepareUrlForSaving(url));
            }
        }
    }

    private static void saveFile(String fileString, String fileName) {

        fileName = kDirName + File.separator + fileName;
        System.out.println("Saving: fileName: " + fileName);
        try {
            File file = new File(fileName);

            // if file doesnt exists, then create it 
            
            if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
            }
            
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileString);
            bw.close();
            System.out.println("Done writing to " + fileName); //For testing 
            
        } catch (IOException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
    }
    
    private static void saveFile(byte[] fileBytes, String fileName) {

        fileName = kDirName + File.separator + fileName;
        System.out.println("Saving: fileName: " + fileName);
        try {
            File file = new File(fileName);

            // if file doesnt exists, then create it 
            
            if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
            }
            
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
            fos.write(fileBytes);
            System.out.println("Done writing to " + fileName); //For testing 
            
        } catch (IOException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
    }
    
    private static String prepareUrlForSaving(String url){
        
        return url.replace(":", "#").replace("/", "*");
    }
    
    static public HttpResponse downloadUrl(String url) throws IOException {

        HttpParams httpParameters = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParameters,
                4000);
        HttpConnectionParams.setSoTimeout(httpParameters, 4000);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpGet get = new HttpGet(url);
        HttpResponse response = httpClient.execute(get);
        return response;
    }
    /**
     * Download JSON data from URL
     *
     * @param url
     * @return
     */
    public static String downloadString(String url){

        try{
            HttpResponse response =  downloadUrl(url);

            return EntityUtils.toString(response.getEntity());
        }
        catch(IOException e){
            return null;
        }
    }

    public static byte[] downloadBytes(String url) {

        try{
            HttpResponse response =  downloadUrl(url);

            return EntityUtils.toByteArray(response.getEntity());
        }
        catch(IOException e){
            return null;
        }
            
    }
}
