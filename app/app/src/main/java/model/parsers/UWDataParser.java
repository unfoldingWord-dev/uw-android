/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

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
