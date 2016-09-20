package com.bookwormng.android.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Tofunmi Seun on 08/11/2015.
 */
public class AdvertDBHelper extends SQLiteOpenHelper {
    private  static final int dbVersion = 1;
    public static final String dbName = "ads.db";
    public AdvertDBHelper(Context c){ super(c,dbName,null,dbVersion);}

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String sqlCreateString = "CREATE TABLE " + Contract.AdsEntry.tableName + " (" +
                Contract.AdsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.AdsEntry.brandColumn + " TEXT NOT NULL, " +
                Contract.AdsEntry.contentColumn + " TEXT NOT NULL, " +
                Contract.AdsEntry.idColumn + " TEXT NOT NULL, " +
                Contract.AdsEntry.sharedColumn + " TEXT NOT NULL, " +
                " UNIQUE (" + Contract.AdsEntry.contentColumn + " ) ON CONFLICT REPLACE);";
        db.execSQL(sqlCreateString);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.AdsEntry.tableName);
        onCreate(db);
    }
    public void AddAdvert(Advert a)
    {
        ContentValues cValues = new ContentValues();
        cValues.put(Contract.AdsEntry.brandColumn, a.getBrand());
        cValues.put(Contract.AdsEntry.contentColumn, a.getContent());
        cValues.put(Contract.AdsEntry.idColumn, String.valueOf(a.getId()));
        cValues.put(Contract.AdsEntry.sharedColumn, "UNSHARED");
        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(Contract.AdsEntry.tableName, null, cValues);
        database.close();
    }
    public ArrayList<Advert> getUnsharedAdverts()
    {
        SQLiteDatabase database = this.getReadableDatabase();
        //Querying my database........
        Cursor cursor = database.query(Contract.AdsEntry.tableName, new String[]
                        {
                                Contract.AdsEntry.brandColumn,
                                Contract.AdsEntry.contentColumn,
                                Contract.AdsEntry.idColumn
                        },
                Contract.AdsEntry.sharedColumn + "=?", new String[]{"UNSHARED"}, null, null, null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        if(count > 0) {
            ArrayList<Advert> myAds = new ArrayList<Advert>();
            for (int i = 0; i < count; i++) {
                Advert temp = new Advert();
                temp.setBrand(cursor.getString(cursor.getColumnIndex(Contract.AdsEntry.brandColumn)));
                temp.setContent(cursor.getString(cursor.getColumnIndex(Contract.AdsEntry.contentColumn)));
                temp.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(Contract.AdsEntry.idColumn))));
                myAds.add(temp);
                cursor.moveToNext();
            }
            database.close();
            cursor.close();
            return myAds;
        }
        else
        {
            return  null;
        }
    }
    public void updateAdvert(Advert a)
    {
        ContentValues values = new ContentValues();
        values.put(Contract.AdsEntry.sharedColumn, "SHARED");
        values.put(Contract.AdsEntry.contentColumn, a.getContent());
        values.put(Contract.AdsEntry.idColumn, String.valueOf(a.getId()));
        values.put(Contract.AdsEntry.brandColumn, a.getBrand());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Contract.AdsEntry.tableName, values, Contract.AdsEntry.contentColumn + "=?", new String[]{a.getContent()});
        db.close();
    }
    public boolean IsEmpty()
    {
       ArrayList<Advert> a = getUnsharedAdverts();
        if (a == null)
        {
            return  true;
        }
        return false;
    }
    public boolean DeleteAdvert(Advert advert)
    {
        try
        {
            SQLiteDatabase adBase = this.getReadableDatabase();
            adBase.delete(Contract.AdsEntry.tableName,Contract.AdsEntry.contentColumn + "=?", new String[]{advert.getContent()});
        }
        catch (Exception e)
        {
            return  false;
        }
        return true;
    }
}
