package com.elishai.officemisdeeds.DB;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.elishai.officemisdeeds.DB.DBScheme.MisdeedTable;
import com.elishai.officemisdeeds.Misdeed;

import java.util.Date;
import java.util.UUID;

public class MisdeedCursorWrapper extends CursorWrapper {

    public MisdeedCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Misdeed getMisdeed() {
        String uuidString   = getString(getColumnIndex(MisdeedTable.Cols.UUID));
        String title        = getString(getColumnIndex(MisdeedTable.Cols.TITLE));
        long date           = getLong(getColumnIndex(MisdeedTable.Cols.DATE));
        int isSolved        = getInt(getColumnIndex(MisdeedTable.Cols.SOLVED));
        String suspect      = getString(getColumnIndex(MisdeedTable.Cols.SUSPECT));

        Misdeed misdeed = new Misdeed(UUID.fromString(uuidString));
        misdeed.setTitle(title);
        misdeed.setDate(new Date(date));
        misdeed.setSolved(isSolved != 0);
        misdeed.setSuspect(suspect);

        return misdeed;
    }
}
