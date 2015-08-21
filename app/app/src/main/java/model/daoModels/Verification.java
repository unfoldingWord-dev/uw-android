package model.daoModels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.dao.DaoException;
import model.UWDatabaseModel;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS
// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "VERIFICATION".
 */
public class Verification extends model.UWDatabaseModel  implements java.io.Serializable {

    private Long id;
    private String signingInstitution;
    private String signature;
    private Integer status;
    private long bookId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient VerificationDao myDao;

    private Book book;
    private Long book__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Verification() {
    }

    public Verification(Long id) {
        this.id = id;
    }

    public Verification(Long id, String signingInstitution, String signature, Integer status, long bookId) {
        this.id = id;
        this.signingInstitution = signingInstitution;
        this.signature = signature;
        this.status = status;
        this.bookId = bookId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getVerificationDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSigningInstitution() {
        return signingInstitution;
    }

    public void setSigningInstitution(String signingInstitution) {
        this.signingInstitution = signingInstitution;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    /** To-one relationship, resolved on first access. */
    public Book getBook() {
        long __key = this.bookId;
        if (book__resolvedKey == null || !book__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookDao targetDao = daoSession.getBookDao();
            Book bookNew = targetDao.load(__key);
            synchronized (this) {
                book = bookNew;
            	book__resolvedKey = __key;
            }
        }
        return book;
    }

    public void setBook(Book book) {
        if (book == null) {
            throw new DaoException("To-one property 'bookId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.book = book;
            bookId = book.getId();
            book__resolvedKey = bookId;
        }
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
    public String getUniqueSlug() {
        return null;
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json) {

        try {
            this.signingInstitution = json.getString("si");
            this.signature = json.getString("sig");
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return this;
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {
        return false;
    }

    @Override
    public Verification setupModelFromJson(JSONObject json, UWDatabaseModel parent) {

        try{
            this.signingInstitution = json.getString("si");
            this.signature = json.getString("sig");
            this.bookId = ((Book) parent).getId();
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public void insertModel(DaoSession session) {

        session.getVerificationDao().insert(this);
        refresh();
    }

    //endregion

    /**
     * @param bookId ID of the book for which you're requesting Verifications
     * @param session Session to use
     * @return List of verifications for the passed book id
     */
    static public List<Verification> getModelForBookId(long bookId, DaoSession session){

        VerificationDao dao = session.getVerificationDao();
        return dao.queryBuilder()
                .where(VerificationDao.Properties.BookId.eq(bookId))
                .list();
    }

    @Override
    public String toString() {
        return "Verification{" +
                "bookId=" + bookId +
                ", id=" + id +
                ", signature='" + signature + '\'' +
                ", signingInstitution='" + signingInstitution + '\'' +
                ", status=" + status +
                '}';
    }

    // KEEP METHODS END

}
