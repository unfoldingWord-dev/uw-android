package model.daoModels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.dao.DaoException;
import model.UWDatabaseModel;
import model.parsers.LanguageParser;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS
// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "LANGUAGE".
 */
public class Language extends model.UWDatabaseModel  implements java.io.Serializable {

    private Long id;
    private String uniqueSlug;
    private String slug;
    private String languageAbbreviation;
    private java.util.Date modified;
    private long projectId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient LanguageDao myDao;

    private Project project;
    private Long project__resolvedKey;

    private List<Version> versions;

    // KEEP FIELDS - put your custom fields here
//    static private final String TAG = "Language";
    // KEEP FIELDS END

    public Language() {
    }

    public Language(Long id) {
        this.id = id;
    }

    public Language(Long id, String uniqueSlug, String slug, String languageAbbreviation, java.util.Date modified, long projectId) {
        this.id = id;
        this.uniqueSlug = uniqueSlug;
        this.slug = slug;
        this.languageAbbreviation = languageAbbreviation;
        this.modified = modified;
        this.projectId = projectId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLanguageDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueSlug() {
        return uniqueSlug;
    }

    public void setUniqueSlug(String uniqueSlug) {
        this.uniqueSlug = uniqueSlug;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLanguageAbbreviation() {
        return languageAbbreviation;
    }

    public void setLanguageAbbreviation(String languageAbbreviation) {
        this.languageAbbreviation = languageAbbreviation;
    }

    public java.util.Date getModified() {
        return modified;
    }

    public void setModified(java.util.Date modified) {
        this.modified = modified;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    /** To-one relationship, resolved on first access. */
    public Project getProject() {
        long __key = this.projectId;
        if (project__resolvedKey == null || !project__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProjectDao targetDao = daoSession.getProjectDao();
            Project projectNew = targetDao.load(__key);
            synchronized (this) {
                project = projectNew;
            	project__resolvedKey = __key;
            }
        }
        return project;
    }

    public void setProject(Project project) {
        if (project == null) {
            throw new DaoException("To-one property 'projectId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.project = project;
            projectId = project.getId();
            project__resolvedKey = projectId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Version> getVersions() {
        if (versions == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VersionDao targetDao = daoSession.getVersionDao();
            List<Version> versionsNew = targetDao._queryLanguage_Versions(id);
            synchronized (this) {
                if(versions == null) {
                    versions = versionsNew;
                }
            }
        }
        return versions;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetVersions() {
        versions = null;
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

    //region UWDatabaseModel

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json) {
        return null;
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent) {
        try {
            return LanguageParser.parseLanguage(json, parent);
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void insertModel(DaoSession session) {
        session.getLanguageDao().insert(this);
        this.refresh();
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {

        Language newLanguage = (Language) newModel;

        this.uniqueSlug = newLanguage.uniqueSlug;
        this.languageAbbreviation = newLanguage.languageAbbreviation;
        this.projectId = newLanguage.projectId;

        boolean wasUpdated = (newLanguage.modified.compareTo(this.modified) > 0);
        this.modified = newLanguage.modified;
        update();
        return wasUpdated;
    }

    //endregion

    /**
     * @param uniqueSlug Slug that is unique to one model
     * @param session Session to use
     * @return Unique Language model
     */
    static public Language getModelForUniqueSlug(String uniqueSlug, DaoSession session){
        LanguageDao dao = session.getLanguageDao();
        return dao.queryBuilder()
                .where(LanguageDao.Properties.UniqueSlug.eq(uniqueSlug))
                .unique();
    }

    @Override
    public String toString() {
        return "Language{" +
                "id=" + id +
                ", modified=" + modified +
                ", project__resolvedKey=" + project__resolvedKey +
                ", projectId=" + projectId +
                ", slug='" + slug + '\'' +
                ", uniqueSlug='" + uniqueSlug + '\'' +
                '}';
    }
    // KEEP METHODS END

}
