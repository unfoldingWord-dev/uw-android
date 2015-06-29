package model.daoModels;

import java.util.List;
import model.daoModels.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import model.UWDatabaseModel;
import model.parsers.ProjectParser;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
// KEEP INCLUDES END
/**
 * Entity mapped to table PROJECT.
 */
public class Project extends model.UWDatabaseModel  implements java.io.Serializable {

    private Long id;
    private String slug;
    private String title;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ProjectDao myDao;

    private List<Language> languages;

    // KEEP FIELDS - put your custom fields here
    static private final String TAG = "Project";
    // KEEP FIELDS END

    public Project() {
    }

    public Project(Long id) {
        this.id = id;
    }

    public Project(Long id, String slug, String title) {
        this.id = id;
        this.slug = slug;
        this.title = title;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProjectDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Language> getLanguages() {
        if (languages == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LanguageDao targetDao = daoSession.getLanguageDao();
            List<Language> languagesNew = targetDao._queryProject_Languages(id);
            synchronized (this) {
                if(languages == null) {
                    languages = languagesNew;
                }
            }
        }
        return languages;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetLanguages() {
        languages = null;
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

    static public List<Project> getAllModels(DaoSession session){

        return session.getProjectDao().queryBuilder().list();
    }

    static public Project getModelForSlug(String slug, DaoSession session){
        ProjectDao dao = session.getProjectDao();
        Project model = dao.queryBuilder()
                .where(ProjectDao.Properties.Slug.eq(slug))
                .unique();

        return (model == null)? null : model;
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json) {

        try {
            return ProjectParser.parseProject(json);
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent) {
        return null;
    }

    @Override
    public void insertModel(DaoSession session) {

        session.getProjectDao().insert(this);
        refresh();
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {

        Project newProject = (Project) newModel;

        this.slug = newProject.slug;
        this.title = newProject.title;
        update();
        return true;
    }
    // KEEP METHODS END

}
