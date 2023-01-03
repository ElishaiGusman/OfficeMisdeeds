package com.elishai.officemisdeeds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elishai.officemisdeeds.DB.DBHelper;
import com.elishai.officemisdeeds.DB.DBScheme.MisdeedTable;
import com.elishai.officemisdeeds.DB.MisdeedCursorWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//Singleton class
public class MisdeedManager {
    private static MisdeedManager sMisdeedManager;

    //DELETED private List<Misdeed> mMisdeeds;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    //Singleton method "get" returns the only instance
    //                                    of the class
    public static MisdeedManager get(Context context) {
        if(sMisdeedManager == null) {
            sMisdeedManager = new MisdeedManager(context);
        }
        return sMisdeedManager;
    }
    //Default constructor
    private MisdeedManager(Context context) {
        mContext = context;
        mDatabase = new DBHelper(mContext).getWritableDatabase();

      /* DELETED mMisdeeds = new ArrayList<>();
      //TEMPORARY GENERATOR
        for(int i = 0; i < 100; i++) {
            Misdeed misdeed = new Misdeed();
            misdeed.setTitle("MISDEED #" + i);
            misdeed.setSolved(i % 2 == 0);
            mMisdeeds.add(misdeed);
        }*/
    }

    public void addMisdeed(Misdeed misdeed) {
        ContentValues values = getContentValues(misdeed);

        mDatabase.insert(MisdeedTable.NAME, null, values);
    }

    public void deleteMisdeed(UUID misdeedId) {
        String idString = misdeedId.toString();
        mDatabase.delete(MisdeedTable.NAME,
                        MisdeedTable.Cols.UUID + " = ?",
                new String[]{idString});
       /* DELETED Misdeed misdeed = getMisdeed(misdeedId);
        mMisdeeds.remove(misdeed);*/
    }

    public List<Misdeed> getMisdeeds() {
        List<Misdeed> misdeeds = new ArrayList<>();

        MisdeedCursorWrapper cursorWrapper = queryGetMisdeeds(null, null);

        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                misdeeds.add(cursorWrapper.getMisdeed());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return misdeeds;
    }

    public Misdeed getMisdeed(UUID id) {
        MisdeedCursorWrapper cursorWrapper = queryGetMisdeeds(
                MisdeedTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if(cursorWrapper.getCount() == 0) {
                return null;
            }
            cursorWrapper.moveToFirst();

            return cursorWrapper.getMisdeed();
        } finally {
            cursorWrapper.close();
        }
        /* DELETED
          for(Misdeed misdeed: mMisdeeds) {
            if(misdeed.getId().equals(id)) {
                return misdeed;
            }
        }
        /**this requires API level 24**\
        String particularId = id.toString();
        Misdeed misdeed = mMisdeeds.stream()
                .filter(particularMisdeed -> particularId.equals(particularMisdeed.getId()))
                .findAny()
                .orElse(null);*/
    }

    public File getPhotoFile(Misdeed misdeed) {
        File filesDirectory = mContext.getFilesDir();
        return new File(filesDirectory, misdeed.getPhotoFilename());
    }

    public void updateMisdeed(Misdeed misdeed) {
        String idString = misdeed.getId().toString();
        ContentValues values = getContentValues(misdeed);

        mDatabase.update(MisdeedTable.NAME, values,
                        MisdeedTable.Cols.UUID + " = ?",
                                  new String[] {idString});
    }

    private static ContentValues getContentValues(Misdeed misdeed) {
        ContentValues values = new ContentValues();
        values.put(MisdeedTable.Cols.UUID, misdeed.getId().toString());
        values.put(MisdeedTable.Cols.TITLE, misdeed.getTitle());
        values.put(MisdeedTable.Cols.DATE, misdeed.getDate().getTime());
        values.put(MisdeedTable.Cols.SOLVED, misdeed.isSolved() ? 1 : 0);
        values.put(MisdeedTable.Cols.SUSPECT, misdeed.getSuspect());

        return values;
    }

    private MisdeedCursorWrapper queryGetMisdeeds(String whereSection, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                MisdeedTable.NAME,
                null, //columns; null - all columns
                whereSection,
                whereArgs,
                null,//groupBy
                null,       //having
                null        //orderBy
        );

        return new MisdeedCursorWrapper(cursor);
    }
}