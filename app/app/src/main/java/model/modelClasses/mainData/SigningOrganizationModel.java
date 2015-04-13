package model.modelClasses.mainData;

import android.content.Context;

import org.json.JSONObject;

import model.datasource.SigningOrganizationDataSource;
import model.datasource.VerificationDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import signing.Organization;

/**
 * Created by Fechner on 1/9/15.
 */
public class SigningOrganizationModel extends AMDatabaseModelAbstractObject {

    private static final String TAG = "PageModel";

    public long createdAt;
    public long expiresAt;
    public long modifiedAt;
    public String email;
    public String name;
    public String url;

    public SigningOrganizationModel() {
        super();
    }

    public SigningOrganizationModel(Organization org){
        this.createdAt = org.createdAt.getTime();
        this.expiresAt = org.expiresAt.getTime();
        this.modifiedAt = org.modifiedAt.getTime();
        this.email = org.email;
        this.name = org.name;
        this.url = org.url;
        this.slug = org.slug;
    }

    public SigningOrganizationModel(JSONObject jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    public SigningOrganizationModel(JSONObject jsonObject, long parentId, boolean sideLoaded) {
        super(jsonObject, parentId, sideLoaded);
    }


    public SigningOrganizationDataSource getDataSource(Context context) {
        return new SigningOrganizationDataSource(context);
    }

    @Override
    public void initModelFromJson(JSONObject json, boolean sideLoaded) {

        return;
    }

    @Override
    public void initModelFromJson(JSONObject json, long parentId, boolean sideLoaded) {

        return;
    }
}








