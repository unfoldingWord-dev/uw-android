/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model;

import org.json.JSONObject;

import model.daoModels.DaoSession;

/**
 * Created by Fechner on 6/17/15.
 */
public abstract class UWDatabaseModel {

    /**
     * @param json
     * @return A Model based on the json
     */
    abstract public UWDatabaseModel setupModelFromJson(JSONObject json);

    /**
     * @param json
     * @param parent
     * @return a model based on json, based on it's parent
     */
    abstract public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent);

    /**
     * @return a Slug unique to only this object
     */
    abstract public String getUniqueSlug();

    /**
     * Update based on the passed model and save to DB
     * @param newModel
     * @return
     */
    abstract public boolean updateWithModel(UWDatabaseModel newModel);

    /**
     * Easy way to insert model
     * @param session
     */
    abstract public void insertModel(DaoSession session);
}
