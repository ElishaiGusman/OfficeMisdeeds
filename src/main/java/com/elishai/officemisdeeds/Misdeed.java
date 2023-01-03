package com.elishai.officemisdeeds;

import java.util.Date;
import java.util.UUID;

public class Misdeed {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;

    //The default constructor
    public Misdeed() {
        this(UUID.randomUUID());
    }

    public Misdeed(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public String getPhotoFilename() {
        return "IMG_ " +getId().toString() + ".jpg";
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
}