package model.modelClasses.AMDatabase;

import android.content.Context;
import android.util.Log;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;

/**
 * Created by Fechner on 1/12/15.
 */
public abstract class AMDatabaseModelAbstractObject {

    public static final String ABSTRACT_TAG = "AMDBAbstractModel";
    public long uid;
    public long parentId;
    public String slug;

    /**
     * Should set all the Object's attributes that are necessary to load from JSON
     * @return
     */
    abstract public void initModelFromJson(String json, boolean sideLoaded);

    /**
     * Should set all the Object's attributes that are necessary to load from JSON
     * @param json
     * @param parentId
     */
    abstract public void initModelFromJson(String json, long parentId, boolean sideLoaded);

    /**
     *
     * @param context
     * @return
     */
    abstract public AMDatabaseDataSourceAbstract getDataSource(Context context);

    public AMDatabaseModelAbstractObject(){

    }

    public AMDatabaseModelAbstractObject(String json, boolean sideLoaded){
        this();
        this.initModelFromJson(json, sideLoaded);
    }

    public AMDatabaseModelAbstractObject(String json, long parentId, boolean sideLoaded) {
        this.initModelFromJson(json, parentId, sideLoaded);
//        Log.d(ABSTRACT_TAG, this.toString());
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
