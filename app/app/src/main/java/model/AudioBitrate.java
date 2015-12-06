/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import model.daoModels.AudioBook;
import model.daoModels.AudioChapter;

/**
 * Created by Fechner on 10/1/15.
 */
public class AudioBitrate implements Serializable{

    private int bitrate;
    private long mod;
    private int size;


    private transient AudioChapter audioChapter;

    public AudioBitrate(int bitrate, long mod, int size) {
        this.bitrate = bitrate;
        this.mod = mod;
        this.size = size;
    }

    public AudioChapter getAudioChapter() {
        return audioChapter;
    }

    public void setAudioChapter(AudioChapter audioChapter) {
        this.audioChapter = audioChapter;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public long getMod() {
        return mod;
    }

    public void setMod(long mod) {
        this.mod = mod;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Bitrate: " + bitrate + "kbps (" + Math.round(((double) getAudioChapter().getAudioSize(this)) / 1000.0 / 1000.0) + " MB)";
    }
}
