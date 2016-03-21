package adapters.sharing;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.DaoDBHelper;
import model.daoModels.Language;
import model.daoModels.LanguageLocale;
import model.daoModels.Project;
import model.daoModels.Version;

/**
 * Created by Fechner on 12/11/15.
 */
public class SharingLanguageViewModel implements Comparable<SharingLanguageViewModel> {

    private String title;
    private List<Version> versions = new ArrayList<>();


    public SharingLanguageViewModel(String title, List<Version> versions) {
        this.versions = versions;
        this.title = title;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int compareTo(SharingLanguageViewModel another) {
        return this.title.compareTo(another.getTitle());
    }


    public static List<SharingLanguageViewModel> createViewModels(Context context, Project[] projects) {

        Map<String, List<Version>> versionMap = new HashMap<>();

        for (Project project : projects) {
            for(Language language : project.getLanguages()){

                if(!versionMap.containsKey(language.getLanguageAbbreviation())){
                    versionMap.put(language.getLanguageAbbreviation(), new ArrayList<Version>());
                }

                for(Version version : language.getVersions()){
                    if(version.isDownloaded()) {
                        versionMap.get(language.getLanguageAbbreviation()).add(version);
                    }
                }
            }
        }

        List<SharingLanguageViewModel> models = new ArrayList<>();

        for (Map.Entry<String, List<Version>> entry : versionMap.entrySet()) {
            if(entry.getValue().size() > 0) {
                models.add(new SharingLanguageViewModel(getTitle(context, entry.getKey()), entry.getValue()));
            }
        }
        Collections.sort(models);
        return models;
    }

    private static String getTitle(Context context, String abbreviation){
        return getLanguageName(context, abbreviation) + " (" + abbreviation + ")";
    }

    private static String getLanguageName(Context context, String abbreviation){
        LanguageLocale languageLocale = LanguageLocale.getLocalForKey(abbreviation, DaoDBHelper.getDaoSession(context));
        return  (languageLocale != null)? languageLocale.getLanguageName() : "";
    }
}
