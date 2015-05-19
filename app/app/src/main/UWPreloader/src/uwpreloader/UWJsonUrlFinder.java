/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uwpreloader;

import org.json.*;
import java.util.ArrayList;

/**
 *
 * @author Fechner
 */
public class UWJsonUrlFinder {
    
    public static ArrayList<String> getAllUrlsForJson(String json){
        
        ArrayList<String> urls = new ArrayList<String>();
        try{
            
            JSONObject baseObject = new JSONObject(json);
            JSONArray projects = baseObject.getJSONArray("cat");
        
            for(int i = 0; i < projects.length(); i++){
            
                JSONObject project = projects.getJSONObject(i);
                urls.addAll(getAllUrlsForProject(project));
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        
        return urls;
    }
    
    private static ArrayList<String> getAllUrlsForProject(JSONObject project){
        
        ArrayList<String> urls = new ArrayList<String>();
        JSONArray models = project.getJSONArray("langs");
        for(int i = 0; i < models.length(); i++){
            
            JSONObject model = models.getJSONObject(i);
            urls.addAll(getAllUrlsForLanguage(model));
        }
        return urls;
    }
    
    private static ArrayList<String> getAllUrlsForLanguage(JSONObject language){
        
        ArrayList<String> urls = new ArrayList<String>();
        JSONArray models = language.getJSONArray("vers");
        for(int i = 0; i < models.length(); i++){
            
            JSONObject model = models.getJSONObject(i);
            urls.addAll(getAllUrlsForVersion(model));
        }
        return urls;
    }
    
    private static ArrayList<String> getAllUrlsForVersion(JSONObject version){
        
        ArrayList<String> urls = new ArrayList<String>();
        JSONArray models = version.getJSONArray("toc");
        for(int i = 0; i < models.length(); i++){
            
            JSONObject model = models.getJSONObject(i);
            urls.addAll(getAllUrlsForBook(model));
        }
        return urls;
    }
    
    private static ArrayList<String> getAllUrlsForBook(JSONObject book){
        
        ArrayList<String> urls = new ArrayList<String>();
        
        urls.add(book.getString("src"));
        urls.add(book.getString("src_sig"));
        
        return urls;
    }
    
    
}
