package uwpreloader;

import java.io.*;
import java.net.*;

/**
 * Created by Fechner on 11/28/14.
 */
public class Downloader {

    static final String kCatalogUrl = "https://api.unfoldingword.org/uw/txt/2/catalog.json";
    static final String kLocalesUrl = "http://td.unfoldingword.org/exports/langnames.json";
    
    static final String kDirName = "assets";
    
    static final String kFileName = "preloaded_data.json";
    static final String kLocalesFileName = "locales_data.json";
    
    public static void updateData(){
        
        purgeDirectory(kDirName);
        
        String json = getStringFromUrl(kCatalogUrl);
        saveFile(json, kFileName);
        
        String localesJson = getStringFromUrl(kLocalesUrl);
        saveFile(localesJson, kLocalesFileName);
    }
    
    private static void purgeDirectory(String directory){
        
        System.out.println("Will try to purge dir: " + directory);
        File dir = new File(directory);
        
         for (File file: dir.listFiles()){ 
             System.out.println("Name: " + file.getName());
             if (!file.isDirectory() && (file.getName().equals(kFileName) || file.getName().equals(kLocalesFileName))){
                 file.delete();
             }
         }
    }
   
    
    private static String getStringFromUrl(String url){
    URL u;
      InputStream is = null;
      DataInputStream dis;
      String s;
      
      String finalString = "";
 
      try {

         u = new URL(url);

         is = u.openStream();        
 
         dis = new DataInputStream(new BufferedInputStream(is));

         while ((s = dis.readLine()) != null) {
            System.out.println(s);
            finalString += s;
         }
 
      } catch (MalformedURLException mue) {
 
         System.out.println("Ouch - a MalformedURLException happened.");
         mue.printStackTrace();
         System.exit(1);
         return null;
 
      } catch (IOException ioe) {
 
         System.out.println("Oops- an IOException happened.");
         ioe.printStackTrace();
         System.exit(1);
         return null;
 
      } finally {

         try {
            is.close();
         } catch (IOException ioe) {
            return null;
         }
 
      }
      
      return finalString;
    }

    private static void saveFile(String fileString, String fileName) {

        fileName = kDirName + File.separator + fileName;
        System.out.println("fileName: " + fileName);
        try {
            File file = new File(fileName);

            // if file doesnt exists, then create it 
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
}
