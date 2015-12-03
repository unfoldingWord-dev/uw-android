package adapters.versions;

import android.content.Context;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import model.DaoDBHelper;
import model.DataFileManager;
import model.DownloadState;
import model.daoModels.Language;
import model.daoModels.LanguageLocale;
import model.daoModels.Project;
import model.daoModels.Version;
import model.parsers.MediaType;
import view.ViewContentHelper;

/**
 * Created by Fechner on 12/1/15.
 */
public class VersionViewModel {

    private Version version;
    private List<ResourceViewModel> resources = new ArrayList<>();

    public static List<VersionViewModel> createModels(Project project){

        List<VersionViewModel> models = new ArrayList<>();
        for(Language language : project.getLanguages()){
            for(Version version : language.getVersions()){
                models.add(new VersionViewModel(version));
            }
        }
        return models;
    }
    public VersionViewModel(Version version) {
        this.version = version;
        setupResources();
    }

    private void setupResources(){
        int numAdded = 0;
        resources.add(numAdded, new ResourceViewModel(MediaType.MEDIA_TYPE_TEXT));
        numAdded++;
        if(version.hasAudio()){
            resources.add(numAdded, new ResourceViewModel(MediaType.MEDIA_TYPE_AUDIO));
            numAdded++;
        }
        if(version.hasVideo()){
            resources.add(numAdded, new ResourceViewModel(MediaType.MEDIA_TYPE_VIDEO));
        }
    }

    public Version getVersion() {
        return version;
    }

    public List<ResourceViewModel> getResources() {
        return resources;
    }

    public String getTitle(Context context){
        return version.getName() + " " + getLanguageName(context) + " - " + version.getLanguage().getLanguageAbbreviation();
    }

    private String getLanguageName(Context context){
        LanguageLocale languageLocale = LanguageLocale.getLocalForKey(version.getLanguage().getLanguageAbbreviation(), DaoDBHelper.getDaoSession(context));
        return (languageLocale != null)? languageLocale.getLanguageName() : "";
    }

    public class ResourceViewModel{

        private MediaType type;

        public ResourceViewModel(MediaType type) {
            this.type = type;
        }

        public int getImageResource(){

            switch (type){

                case MEDIA_TYPE_TEXT: return R.drawable.reading_icon;

                case MEDIA_TYPE_AUDIO: return R.drawable.audio_icon;

                case MEDIA_TYPE_VIDEO: return R.drawable.video_icone;

                default: return -1;
            }
        }

        public DownloadState getDownloadState(Context context){
            return DataFileManager.getStateOfContent(context, version, type);
        }

        public String getTitle(){
            switch (type){

                case MEDIA_TYPE_TEXT: return "Text";

                case MEDIA_TYPE_AUDIO: return "Audio";

                case MEDIA_TYPE_VIDEO: return "Video";

                default: return "";
            }
        }

        public int getCheckingLevelImage(){
            return ViewContentHelper.getDarkCheckingLevelImageResource(Integer.parseInt(version.getStatusCheckingLevel()));
        }
    }
}

