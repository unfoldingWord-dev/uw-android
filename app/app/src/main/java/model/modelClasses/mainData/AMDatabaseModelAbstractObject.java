package model.modelClasses.mainData;

import android.content.Context;

import org.json.JSONObject;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import model.datasource.AMDatabaseDataSourceAbstract;

/**
 * Created by Fechner on 1/12/15.
 */
public abstract class AMDatabaseModelAbstractObject {

    public long uid;
    public long parentId;
    public String slug;

    /**
     * Should set all the Object's attributes that are necessary to load from JSON
     * @return
     */
    abstract public void initModelFromJsonObject(JSONObject jsonObject);

    abstract public void initModelFromJsonObject(JSONObject jsonObject, AMDatabaseModelAbstractObject parent);

    abstract public AMDatabaseDataSourceAbstract getDataSource(Context context);

    public AMDatabaseModelAbstractObject(){

    }

    public AMDatabaseModelAbstractObject(JSONObject jsonObject){
        this();
        this.initModelFromJsonObject(jsonObject);
    }

    public AMDatabaseModelAbstractObject(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        this.initModelFromJsonObject(jsonObject, parent);
    }

    static public long getDateFromString(String date) {
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        GregorianCalendar calDate = new GregorianCalendar(gmt);

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        calDate.set(year, month, day);

        long seconds = calDate.getTimeInMillis();
        return Long.parseLong(date);
    }

    @Override
    public String toString() {
        return "AMDatabaseModelAbstractObject{" +
                "uid=" + uid +
                ", parentId=" + parentId +
                '}';
    }
}
