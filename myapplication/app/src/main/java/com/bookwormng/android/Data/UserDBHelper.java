package com.bookwormng.android.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tofunmi Seun on 23/09/2015.
 */
public class UserDBHelper extends SQLiteOpenHelper {
    public UserDBHelper (Context c){ super(c,dbName,null,dbVersion);}
    private  static final int dbVersion = 1;
    public static final String dbName = "user.db";
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String sqlCreateString = "CREATE TABLE " + Contract.UserEntry.tableName + " (" +
                Contract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.UserEntry.handleColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.nameColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.locationColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.bioColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.avatarColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.picColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.accessTokenColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.accessTokenSecretColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.followerColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.friendsColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.scoreColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.adColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.userIdColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.swipeLimitColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.lastQuestionIdColumn + " TEXT NOT NULL, " +
                Contract.UserEntry.lastAdvertIdColumn + " TEXT NOT NULL, " +
                " UNIQUE (" + Contract.UserEntry.handleColumn + " ) ON CONFLICT REPLACE);";
        db.execSQL(sqlCreateString);
    }
    public UserDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.DataEntry.tableName);
        onCreate(db);
    }

    public void UpdateUserDatabase(User theUser)
    {
        ContentValues values = new ContentValues();
        values.put(Contract.UserEntry.handleColumn,theUser.getHandle());
        values.put(Contract.UserEntry.nameColumn,theUser.getName());
        values.put(Contract.UserEntry.locationColumn,theUser.getLocation());
        values.put(Contract.UserEntry.bioColumn,theUser.getBio());
        values.put(Contract.UserEntry.avatarColumn,theUser.getAvatarString());
        values.put(Contract.UserEntry.picColumn, String.valueOf(theUser.getPicString()));
        values.put(Contract.UserEntry.accessTokenColumn, String.valueOf(theUser.getAccessToken()));
        values.put(Contract.UserEntry.accessTokenSecretColumn, String.valueOf(theUser.getAccessTokenSecret()));
        values.put(Contract.UserEntry.followerColumn,String.valueOf(theUser.getFollowers()));
        values.put(Contract.UserEntry.friendsColumn,String.valueOf(theUser.getFriends()));
        values.put(Contract.UserEntry.scoreColumn,String.valueOf(theUser.getScore()));
        values.put(Contract.UserEntry.adColumn, String.valueOf(theUser.getAdshared()));
        values.put(Contract.UserEntry.userIdColumn, String.valueOf(theUser.getUserId()));
        values.put(Contract.UserEntry.swipeLimitColumn,String.valueOf(theUser.getSwipeLimit()));
        values.put(Contract.UserEntry.lastQuestionIdColumn, String.valueOf(theUser.getLastQuestionId()));
        values.put(Contract.UserEntry.lastAdvertIdColumn, String.valueOf(theUser.getLastAdvertId()));
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Contract.UserEntry.tableName, values, Contract.UserEntry.handleColumn + "=?", new String[]{theUser.getHandle()});
        db.close();
    }

    public User CreateUserFromDatabase()
    {
        SQLiteDatabase userBase = this.getReadableDatabase();
        Cursor c = userBase.query(Contract.UserEntry.tableName, new String []
                {
                        Contract.UserEntry.handleColumn,
                        Contract.UserEntry.nameColumn,
                        Contract.UserEntry.locationColumn,
                        Contract.UserEntry.bioColumn,
                        Contract.UserEntry.avatarColumn,
                        Contract.UserEntry.picColumn,
                        Contract.UserEntry.accessTokenColumn,
                        Contract.UserEntry.accessTokenSecretColumn,
                        Contract.UserEntry.followerColumn,
                        Contract.UserEntry.friendsColumn,
                        Contract.UserEntry.scoreColumn,
                        Contract.UserEntry.adColumn,
                        Contract.UserEntry.userIdColumn,
                        Contract.UserEntry.swipeLimitColumn,
                        Contract.UserEntry.lastQuestionIdColumn,
                        Contract.UserEntry.lastAdvertIdColumn
                },null,null,null,null,null);
        c.moveToFirst();
        int count = c.getCount();
        if (count == 1)
        {
            User TheUser = new User();
            TheUser.setHandle(c.getString(c.getColumnIndex(Contract.UserEntry.handleColumn)));
            TheUser.setName(c.getString(c.getColumnIndex(Contract.UserEntry.nameColumn)));
            TheUser.setLocation(c.getString(c.getColumnIndex(Contract.UserEntry.locationColumn)));
            TheUser.setBio(c.getString(c.getColumnIndex(Contract.UserEntry.bioColumn)));
            TheUser.setAvatarString(c.getString(c.getColumnIndex(Contract.UserEntry.avatarColumn)));
            TheUser.setPicString(c.getString(c.getColumnIndex(Contract.UserEntry.picColumn)));
            TheUser.setAccessToken(c.getString(c.getColumnIndex(Contract.UserEntry.accessTokenColumn)));
            TheUser.setAccessTokenSecret(c.getString(c.getColumnIndex(Contract.UserEntry.accessTokenSecretColumn)));
            TheUser.setFollowers(Integer.parseInt(c.getString(c.getColumnIndex(Contract.UserEntry.followerColumn))));
            TheUser.setFriends(Integer.parseInt(c.getString(c.getColumnIndex(Contract.UserEntry.friendsColumn))));
            TheUser.setScore(Integer.parseInt(c.getString(c.getColumnIndex(Contract.UserEntry.scoreColumn))));
            TheUser.setAdshared(Integer.parseInt(c.getString(c.getColumnIndex(Contract.UserEntry.adColumn))));
            TheUser.setUserId(Long.parseLong(c.getString(c.getColumnIndex(Contract.UserEntry.userIdColumn))));
            TheUser.setSwipeLimit(Integer.parseInt(c.getString(c.getColumnIndex(Contract.UserEntry.swipeLimitColumn))));
            TheUser.setLastQuestionId(Long.parseLong(c.getString(c.getColumnIndex(Contract.UserEntry.lastQuestionIdColumn))));
            TheUser.setLastAdvertId(Long.parseLong(c.getString(c.getColumnIndex(Contract.UserEntry.lastAdvertIdColumn))));
            return  TheUser;
        }
        return null;
    }
    public void InsertUser(User theUser)
    {
        ContentValues values = new ContentValues();
        values.put(Contract.UserEntry.handleColumn,theUser.getHandle());
        values.put(Contract.UserEntry.nameColumn,theUser.getName());
        values.put(Contract.UserEntry.locationColumn,theUser.getLocation());
        values.put(Contract.UserEntry.bioColumn,theUser.getBio());
        values.put(Contract.UserEntry.avatarColumn,theUser.getAvatarString());
        values.put(Contract.UserEntry.picColumn, String.valueOf(theUser.getPicString()));
        values.put(Contract.UserEntry.accessTokenColumn, String.valueOf(theUser.getAccessToken()));
        values.put(Contract.UserEntry.accessTokenSecretColumn, String.valueOf(theUser.getAccessTokenSecret()));
        values.put(Contract.UserEntry.followerColumn,String.valueOf(theUser.getFollowers()));
        values.put(Contract.UserEntry.friendsColumn,String.valueOf(theUser.getFriends()));
        values.put(Contract.UserEntry.scoreColumn,String.valueOf(theUser.getScore()));
        values.put(Contract.UserEntry.adColumn, String.valueOf(theUser.getAdshared()));
        values.put(Contract.UserEntry.userIdColumn, String.valueOf(theUser.getUserId()));
        values.put(Contract.UserEntry.swipeLimitColumn,String.valueOf(theUser.getSwipeLimit()));
        values.put(Contract.UserEntry.lastQuestionIdColumn, String.valueOf(theUser.getLastQuestionId()));
        values.put(Contract.UserEntry.lastAdvertIdColumn, String.valueOf(theUser.getLastAdvertId()));
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(Contract.UserEntry.tableName,null,values);
        db.close();
    }

    public boolean DeleteUser(User theUser)
    {
        try
        {
        SQLiteDatabase userBase = this.getReadableDatabase();
        userBase.delete(Contract.UserEntry.tableName,Contract.UserEntry.handleColumn + "=?", new String[]{theUser.getHandle()});
        }
        catch (Exception e)
        {
            return  false;
        }
        return true;
    }

    public boolean IsEmpty()
    {
        SQLiteDatabase userBase = this.getReadableDatabase();
        Cursor c = userBase.query(Contract.UserEntry.tableName, new String []{
                Contract.UserEntry.handleColumn,
                Contract.UserEntry.nameColumn,
                Contract.UserEntry.locationColumn,
                Contract.UserEntry.bioColumn,
                Contract.UserEntry.avatarColumn,
                Contract.UserEntry.picColumn,
                Contract.UserEntry.accessTokenColumn,
                Contract.UserEntry.accessTokenSecretColumn,
                Contract.UserEntry.followerColumn,
                Contract.UserEntry.friendsColumn,
                Contract.UserEntry.scoreColumn,
                Contract.UserEntry.adColumn,
                Contract.UserEntry.userIdColumn,
                Contract.UserEntry.swipeLimitColumn,
                Contract.UserEntry.lastQuestionIdColumn,
                Contract.UserEntry.lastAdvertIdColumn
        },null,null,null,null,null);
        c.moveToFirst();
        int count = c.getCount();
        if (count == 0){return true;}
        return false;
    }
}
