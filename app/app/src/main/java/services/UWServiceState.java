/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package services;

/**
 * Created by PJ Fechner on 6/7/15.
 * Enum for keeping track of a service's state
 */
public enum UWServiceState {

    STATE_NONE(0), STATE_RUNNING(1), STATE_FINISHED(2), STATE_ERROR(3);

    final int num;

    private UWServiceState(int num){
        this.num = num;
    }
}
