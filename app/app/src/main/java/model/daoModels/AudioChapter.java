package model.daoModels;

import java.util.List;
import model.daoModels.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.Gson;
import model.AudioBitrate;
import de.greenrobot.dao.AbstractDao;

import org.json.JSONException;
import org.json.JSONObject;
import model.UWDatabaseModel;
import model.parsers.AudioChapterParser;
// KEEP INCLUDES END
/**
 * Entity mapped to table "AUDIO_CHAPTER".
 */
public class AudioChapter extends model.UWDatabaseModel  implements java.io.Serializable, Comparable<AudioChapter> {

    private Long id;
    private String bitrateJson;
    private String uniqueSlug;
    private String source;
    private String sourceSignature;
    private Integer chapter;
    private Integer length;
    private long audioBookId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient AudioChapterDao myDao;

    private AudioBook audioBook;
    private Long audioBook__resolvedKey;

    private List<Verification> verifications;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public AudioChapter() {
    }

    public AudioChapter(Long id) {
        this.id = id;
    }

    public AudioChapter(Long id, String bitrateJson, String uniqueSlug, String source, String sourceSignature, Integer chapter, Integer length, long audioBookId) {
        this.id = id;
        this.bitrateJson = bitrateJson;
        this.uniqueSlug = uniqueSlug;
        this.source = source;
        this.sourceSignature = sourceSignature;
        this.chapter = chapter;
        this.length = length;
        this.audioBookId = audioBookId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAudioChapterDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBitrateJson() {
        return bitrateJson;
    }

    public void setBitrateJson(String bitrateJson) {
        this.bitrateJson = bitrateJson;
    }

    public String getUniqueSlug() {
        return uniqueSlug;
    }

    public void setUniqueSlug(String uniqueSlug) {
        this.uniqueSlug = uniqueSlug;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceSignature() {
        return sourceSignature;
    }

    public void setSourceSignature(String sourceSignature) {
        this.sourceSignature = sourceSignature;
    }

    public Integer getChapter() {
        return chapter;
    }

    public void setChapter(Integer chapter) {
        this.chapter = chapter;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public long getAudioBookId() {
        return audioBookId;
    }

    public void setAudioBookId(long audioBookId) {
        this.audioBookId = audioBookId;
    }

    /** To-one relationship, resolved on first access. */
    public AudioBook getAudioBook() {
        long __key = this.audioBookId;
        if (audioBook__resolvedKey == null || !audioBook__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AudioBookDao targetDao = daoSession.getAudioBookDao();
            AudioBook audioBookNew = targetDao.load(__key);
            synchronized (this) {
                audioBook = audioBookNew;
            	audioBook__resolvedKey = __key;
            }
        }
        return audioBook;
    }

    public void setAudioBook(AudioBook audioBook) {
        if (audioBook == null) {
            throw new DaoException("To-one property 'audioBookId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.audioBook = audioBook;
            audioBookId = audioBook.getId();
            audioBook__resolvedKey = audioBookId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Verification> getVerifications() {
        if (verifications == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VerificationDao targetDao = daoSession.getVerificationDao();
            List<Verification> verificationsNew = targetDao._queryAudioChapter_Verifications(id);
            synchronized (this) {
                if(verifications == null) {
                    verifications = verificationsNew;
                }
            }
        }
        return verifications;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetVerifications() {
        verifications = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here

    @Override
    public int compareTo(AudioChapter another) {
        return 0;
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json) {
        return null;
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent) {

        try {
            return AudioChapterParser.parseAudioChapter(json, (AudioBook) parent);
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {

        AudioChapter newAudioChapter = (AudioChapter) newModel;

        this.uniqueSlug = newAudioChapter.uniqueSlug;
        this.source = newAudioChapter.source;
        this.sourceSignature = newAudioChapter.sourceSignature;
        this.chapter = newAudioChapter.chapter;
        this.bitrateJson = newAudioChapter.bitrateJson;
        this.length = newAudioChapter.length;

        update();

        return false;
    }

    @Override
    public void insertModel(DaoSession session) {

        session.getAudioChapterDao().insert(this);
        this.refresh();
    }

    /**
     * @param uniqueSlug Slug that is unique to only one model
     * @param session Session to use
     * @return Unique Model with the passed slug
     */
    static public AudioChapter getModelForUniqueSlug(String uniqueSlug, DaoSession session){

        AudioChapterDao dao = session.getAudioChapterDao();
        return dao.queryBuilder()
                .where(AudioChapterDao.Properties.UniqueSlug.eq(uniqueSlug))
                .unique();
    }

    public AudioBitrate[] getBitRates(){
        return new Gson().fromJson(getBitrateJson(), AudioBitrate[].class);
    }

    private String getUrlForBitrate(String url, int bitrate){
        return url.replace("{bitrate}", Integer.toString(bitrate));
    }

    public String getAudioUrl(){
        return getAudioUrl(findHighestBitrate(getBitRates()).getBitrate());
    }

    public String getAudioUrl(int bitrate){
        return getUrlForBitrate(getSource(), bitrate);
    }

    public String getSignatureUrl(int bitrate){
        return getUrlForBitrate(getSourceSignature(), bitrate);
    }

    private AudioBitrate findHighestBitrate(AudioBitrate[] bitRates){
        AudioBitrate currentBitrate = null;

        for (AudioBitrate bitrate : bitRates){
            if(currentBitrate == null || bitrate.getBitrate() > currentBitrate.getBitrate()){
                currentBitrate = bitrate;
            }
        }
        return currentBitrate;
    }
    // KEEP METHODS END

}
