package model.daoModels;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.internal.SqlUtils;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "AUDIO_BOOK".
*/
public class AudioBookDao extends AbstractDao<AudioBook, Long> {

    public static final String TABLENAME = "AUDIO_BOOK";

    /**
     * Properties of entity AudioBook.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UniqueSlug = new Property(1, String.class, "uniqueSlug", false, "UNIQUE_SLUG");
        public final static Property Contributors = new Property(2, String.class, "contributors", false, "CONTRIBUTORS");
        public final static Property Revision = new Property(3, String.class, "revision", false, "REVISION");
        public final static Property TextVersion = new Property(4, String.class, "textVersion", false, "TEXT_VERSION");
        public final static Property BookId = new Property(5, long.class, "bookId", false, "BOOK_ID");
    }

    private DaoSession daoSession;


    public AudioBookDao(DaoConfig config) {
        super(config);
    }
    
    public AudioBookDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AUDIO_BOOK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"UNIQUE_SLUG\" TEXT," + // 1: uniqueSlug
                "\"CONTRIBUTORS\" TEXT," + // 2: contributors
                "\"REVISION\" TEXT," + // 3: revision
                "\"TEXT_VERSION\" TEXT," + // 4: textVersion
                "\"BOOK_ID\" INTEGER NOT NULL );"); // 5: bookId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AUDIO_BOOK\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AudioBook entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uniqueSlug = entity.getUniqueSlug();
        if (uniqueSlug != null) {
            stmt.bindString(2, uniqueSlug);
        }
 
        String contributors = entity.getContributors();
        if (contributors != null) {
            stmt.bindString(3, contributors);
        }
 
        String revision = entity.getRevision();
        if (revision != null) {
            stmt.bindString(4, revision);
        }
 
        String textVersion = entity.getTextVersion();
        if (textVersion != null) {
            stmt.bindString(5, textVersion);
        }
        stmt.bindLong(6, entity.getBookId());
    }

    @Override
    protected void attachEntity(AudioBook entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public AudioBook readEntity(Cursor cursor, int offset) {
        AudioBook entity = new AudioBook( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uniqueSlug
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // contributors
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // revision
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // textVersion
            cursor.getLong(offset + 5) // bookId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AudioBook entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUniqueSlug(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setContributors(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRevision(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTextVersion(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBookId(cursor.getLong(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(AudioBook entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(AudioBook entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getBookDao().getAllColumns());
            builder.append(" FROM AUDIO_BOOK T");
            builder.append(" LEFT JOIN BOOK T0 ON T.\"BOOK_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected AudioBook loadCurrentDeep(Cursor cursor, boolean lock) {
        AudioBook entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Book book = loadCurrentOther(daoSession.getBookDao(), cursor, offset);
         if(book != null) {
            entity.setBook(book);
        }

        return entity;    
    }

    public AudioBook loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<AudioBook> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<AudioBook> list = new ArrayList<AudioBook>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<AudioBook> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<AudioBook> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
