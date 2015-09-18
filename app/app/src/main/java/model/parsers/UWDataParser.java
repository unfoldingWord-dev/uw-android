package model.parsers;

import java.util.Date;

/**
 * Created by PJ Fechner on 6/22/15.
 * Class for parsing ubiquitous Data
 */
public class UWDataParser {

    public static Date getDateFromSecondString(String seconds){
        return getDate(Long.parseLong(seconds));
    }

    public static Date getDate(long seconds){
        seconds *= 1000;
        return new Date(seconds);
    }
}
