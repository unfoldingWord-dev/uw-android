package model.database;

import android.content.Context;

import java.util.ArrayList;

import model.datasource.LanguageDataSource;
import model.modelClasses.mainData.ProjectModel;

/**
 * Created by Fechner on 3/12/15.
 */
public class ModelCaching {

    static ArrayList<String> availableLanguages = null;
    static public ArrayList<String> getAvailableLanguages(Context context){
        if(availableLanguages == null || availableLanguages.size() < 1){
            availableLanguages = new LanguageDataSource(context).getAvailableLanguages();
        }
        return availableLanguages;
    }

    static public ArrayList<String> getAvailableLanguages(Context context, ArrayList<ProjectModel> projects) {

        ArrayList<String> availLanguages = ModelCaching.getAvailableLanguages(context);
        ArrayList<String> languages = new ArrayList<String>();

        for(String language : availLanguages){

            for(ProjectModel project : projects){
                if(project.containsLanguage(language, context) && !languages.contains(language)){
                    languages.add(language.toLowerCase());
                    break;
                }
            }
        }
        return languages;
    }
}
