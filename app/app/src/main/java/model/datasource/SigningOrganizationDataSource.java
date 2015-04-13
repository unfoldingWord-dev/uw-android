package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONObject;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.SigningOrganizationModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class SigningOrganizationDataSource extends AMDatabaseDataSourceAbstract {

    static final String TAG = "VerificationDataSource";
    static final String TABLE_ORGANIZATION = "_table_organization";

    // Table columns of TABLE_VERIFICATION
    static final String TABLE_ORGANIZATION_COLUMN_UID = "_column_page_uid";
    static final String TABLE_ORGANIZATION_COLUMN_SLUG = "_column_slug";
    static final String TABLE_ORGANIZATION_COLUMN_CREATED = "_column_created";
    static final String TABLE_ORGANIZATION_COLUMN_EXPIRES = "_column_expires";
    static final String TABLE_ORGANIZATION_COLUMN_MODIFIED = "_column_modified";
    static final String TABLE_ORGANIZATION_COLUMN_EMAIL = "_column_email";
    static final String TABLE_ORGANIZATION_COLUMN_NAME = "_column_name";
    static final String TABLE_ORGANIZATION_COLUMN_URL = "_column_url";

    public SigningOrganizationDataSource(Context context) {
        super(context);
    }

    @Override
    protected String getSlugColumnName() {
        return TABLE_ORGANIZATION_COLUMN_SLUG;
    }

    @Override
    public AMDatabaseDataSourceAbstract getChildDataSource() {
        return null;
    }

    @Override
    public AMDatabaseDataSourceAbstract getParentDataSource() {
        return new StoriesChapterDataSource(this.context);
    }

    @Override
    protected String getParentIdColumnName() {
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_ORGANIZATION;
    }

    @Override
    public String getUIDColumnName() {
        return TABLE_ORGANIZATION_COLUMN_UID;
    }

    @Override
    public SigningOrganizationModel getModel(String uid) {
        return (SigningOrganizationModel) this.getModelForKey(uid);
    }

    @Override
    public SigningOrganizationModel getModelForSlug(String slug){
        AMDatabaseModelAbstractObject model = this.getModelFromDatabaseForSlug(slug);
        if(model != null) {
            return (SigningOrganizationModel) model;
        }
        else {
            return null;
        }
    }

    @Override
    public AMDatabaseModelAbstractObject saveOrUpdateModel(JSONObject json, long parentID, boolean sideLoaded) {
        SigningOrganizationModel newModel = new SigningOrganizationModel(json, parentID, sideLoaded);
        SigningOrganizationModel currentModel = getModelForSlug(newModel.slug);

        if(currentModel != null) {
            newModel.uid = currentModel.uid;
        }

        saveModel(newModel);
        return getModelForSlug(newModel.slug);
    }

    @Override
    public ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject abstractModel) {

        SigningOrganizationModel model = (SigningOrganizationModel) abstractModel;
        ContentValues values = new ContentValues();

        if(model.uid > 0) {
            values.put(TABLE_ORGANIZATION_COLUMN_UID, model.uid);
        }
        values.put(TABLE_ORGANIZATION_COLUMN_SLUG, model.slug);
        values.put(TABLE_ORGANIZATION_COLUMN_CREATED, model.createdAt);
        values.put(TABLE_ORGANIZATION_COLUMN_EXPIRES, model.expiresAt);
        values.put(TABLE_ORGANIZATION_COLUMN_MODIFIED, model.modifiedAt);

        values.put(TABLE_ORGANIZATION_COLUMN_EMAIL, model.email);
        values.put(TABLE_ORGANIZATION_COLUMN_NAME, model.name);
        values.put(TABLE_ORGANIZATION_COLUMN_URL, model.url);

        return values;
    }

    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        SigningOrganizationModel model = new SigningOrganizationModel();
        model.uid = cursor.getLong(cursor.getColumnIndex(TABLE_ORGANIZATION_COLUMN_UID));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_ORGANIZATION_COLUMN_SLUG));
        model.createdAt = cursor.getLong(cursor.getColumnIndex(TABLE_ORGANIZATION_COLUMN_CREATED));
        model.expiresAt = cursor.getLong(cursor.getColumnIndex(TABLE_ORGANIZATION_COLUMN_EXPIRES));
        model.modifiedAt = cursor.getLong(cursor.getColumnIndex(TABLE_ORGANIZATION_COLUMN_MODIFIED));

        model.email = cursor.getString(cursor.getColumnIndex(TABLE_ORGANIZATION_COLUMN_EMAIL));
        model.name = cursor.getString(cursor.getColumnIndex(TABLE_ORGANIZATION_COLUMN_NAME));
        model.url = cursor.getString(cursor.getColumnIndex(TABLE_ORGANIZATION_COLUMN_URL));

        return model;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                SigningOrganizationDataSource.TABLE_ORGANIZATION_COLUMN_UID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                SigningOrganizationDataSource.TABLE_ORGANIZATION_COLUMN_SLUG + " VARCHAR," +
                SigningOrganizationDataSource.TABLE_ORGANIZATION_COLUMN_CREATED + " INTEGER," +
                SigningOrganizationDataSource.TABLE_ORGANIZATION_COLUMN_EXPIRES + " INTEGER," +
                SigningOrganizationDataSource.TABLE_ORGANIZATION_COLUMN_MODIFIED + " INTEGER," +

                SigningOrganizationDataSource.TABLE_ORGANIZATION_COLUMN_EMAIL + " VARCHAR," +
                SigningOrganizationDataSource.TABLE_ORGANIZATION_COLUMN_NAME + " VARCHAR," +
                SigningOrganizationDataSource.TABLE_ORGANIZATION_COLUMN_URL + " VARCHAR)";

        return creationString;
    }
}
