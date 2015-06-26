package model;

import org.json.JSONObject;

import model.daoModels.DaoSession;

/**
 * Created by Fechner on 6/17/15.
 */
public abstract class UWDatabaseModel {

    abstract public UWDatabaseModel setupModelFromJson(JSONObject json);
    abstract public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent);
    abstract public String getSlug();
    abstract public boolean updateWithModel(UWDatabaseModel newModel);
    abstract public void insertModel(DaoSession session);
}
