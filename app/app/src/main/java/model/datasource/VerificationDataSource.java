package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.PageModel;
import model.modelClasses.mainData.VerificationModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class VerificationDataSource extends AMDatabaseDataSourceAbstract {

    static final String TAG = "VerificationDataSource";
    static final String TABLE_VERIFICATION = "_table_verification";

    // Table columns of TABLE_VERIFICATION
    static final String TABLE_VERIFICATION_COLUMN_UID = "_column_page_uid";
    static final String TABLE_VERIFICATION_COLUMN_PARENT_ID = "_column_parent_id";
    static final String TABLE_VERIFICATION_COLUMN_SLUG = "_column_slug";
    static final String TABLE_VERIFICATION_COLUMN_SIGNING_INSTITUTION = "_column_signing_institution";
    static final String TABLE_VERIFICATION_COLUMN_SIGNATURE = "_column_signature";
    static final String TABLE_VERIFICATION_COLUMN_VERIFICATION_STATUS = "_column_verification_status";

    public VerificationDataSource(Context context) {
        super(context);
    }

    @Override
    protected String getSlugColumnName() {
        return TABLE_VERIFICATION_COLUMN_SLUG;
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
        return TABLE_VERIFICATION_COLUMN_PARENT_ID;
    }

    @Override
    public String getTableName() {
        return TABLE_VERIFICATION;
    }

    @Override
    public String getUIDColumnName() {
        return TABLE_VERIFICATION_COLUMN_UID;
    }

    @Override
    public VerificationModel getModel(String uid) {
        return (VerificationModel) this.getModelForKey(uid);
    }

    @Override
    public VerificationModel getModelForSlug(String slug){
        return (VerificationModel) this.getModelFromDatabaseForSlug(slug);
    }

    public ArrayList<VerificationModel> getVerificationsForParentId(String id){

        ArrayList<AMDatabaseModelAbstractObject>  models = this.getModelsFromDatabase(this.getParentIdColumnName(), id);
        ArrayList<VerificationModel> verifications = new ArrayList<VerificationModel>();

        for(AMDatabaseModelAbstractObject model : models){
            verifications.add((VerificationModel) model);
        }

        return verifications;
    }

    @Override
    public AMDatabaseModelAbstractObject saveOrUpdateModel(JSONObject json, long parentID, boolean sideLoaded) {
        VerificationModel newModel = new VerificationModel(json, parentID, sideLoaded);
        VerificationModel currentModel = getModelForSlug(newModel.slug);

        if(currentModel != null) {
            newModel.uid = currentModel.uid;
        }

        saveModel(newModel);
        return getModelForSlug(newModel.slug);
    }

    @Override
    public ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject abstractModel) {

        VerificationModel model = (VerificationModel) abstractModel;
        ContentValues values = new ContentValues();

        if(model.uid > 0) {
            values.put(TABLE_VERIFICATION_COLUMN_UID, model.uid);
        }
        values.put(TABLE_VERIFICATION_COLUMN_PARENT_ID, model.parentId);
        values.put(TABLE_VERIFICATION_COLUMN_SLUG, model.slug);

        values.put(TABLE_VERIFICATION_COLUMN_SIGNING_INSTITUTION, model.signingInstitution);
        values.put(TABLE_VERIFICATION_COLUMN_SIGNATURE, model.signature);
        values.put(TABLE_VERIFICATION_COLUMN_VERIFICATION_STATUS, model.verificationStatus);

        return values;
    }

    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        VerificationModel model = new VerificationModel();
        model.uid = cursor.getLong(cursor.getColumnIndex(TABLE_VERIFICATION_COLUMN_UID));
        model.parentId = cursor.getLong(cursor.getColumnIndex(TABLE_VERIFICATION_COLUMN_PARENT_ID));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_VERIFICATION_COLUMN_SLUG));

        model.signingInstitution = cursor.getString(cursor.getColumnIndex(TABLE_VERIFICATION_COLUMN_SIGNING_INSTITUTION));
        model.signature = cursor.getString(cursor.getColumnIndex(TABLE_VERIFICATION_COLUMN_SIGNATURE));
        model.verificationStatus = cursor.getInt(cursor.getColumnIndex(TABLE_VERIFICATION_COLUMN_VERIFICATION_STATUS));

        return model;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                VerificationDataSource.TABLE_VERIFICATION_COLUMN_UID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                VerificationDataSource.TABLE_VERIFICATION_COLUMN_PARENT_ID + " INTEGER," +
                VerificationDataSource.TABLE_VERIFICATION_COLUMN_SLUG + " VARCHAR," +

                VerificationDataSource.TABLE_VERIFICATION_COLUMN_SIGNING_INSTITUTION + " VARCHAR," +
                VerificationDataSource.TABLE_VERIFICATION_COLUMN_SIGNATURE + " VARCHAR," +
                VerificationDataSource.TABLE_VERIFICATION_COLUMN_VERIFICATION_STATUS + " INTEGER)";
        return creationString;
    }
}
