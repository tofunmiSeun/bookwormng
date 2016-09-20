package com.bookwormng.android.Data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Tofunmi Seun on 17/08/2015.
 */
public class QuestionDBHelper extends SQLiteOpenHelper {
    //Constructor
    public QuestionDBHelper(Context c)
    {
        super(c,dbName,null,dbVersion);
    }
    private  static final int dbVersion = 1;
    public static final String dbName = "bookworm.db";
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String sqlCreateString = "CREATE TABLE " + Contract.DataEntry.tableName + " (" +
                Contract.DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.DataEntry.columnQuestion + " TEXT NOT NULL, " +
                Contract.DataEntry.columnAnswer + " TEXT NOT NULL, " +
                Contract.DataEntry.columnId + " TEXT NOT NULL, " +
                Contract.DataEntry.columnAnswered + " TEXT NOT NULL, " +
                " UNIQUE (" + Contract.DataEntry.columnQuestion + " ) ON CONFLICT REPLACE);";
        db.execSQL(sqlCreateString);
    }
    public QuestionDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, null, dbVersion);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.DataEntry.tableName);
        onCreate(db);
    }
    public void AddQuestion(Question q)
    {
        ContentValues cValues = new ContentValues();
        cValues.put(Contract.DataEntry.columnQuestion, q.getQuestion());
        cValues.put(Contract.DataEntry.columnAnswer, q.getAnswer());
        cValues.put(Contract.DataEntry.columnId, String.valueOf(q.getId()));
        cValues.put(Contract.DataEntry.columnAnswered, "UNANSWERED");
        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(Contract.DataEntry.tableName, null, cValues);
        database.close();
    }
    public ArrayList<Question> readUnansweredQuestion()
    {
        SQLiteDatabase database = this.getReadableDatabase();
        //Querying my database........
        Cursor cursor = database.query(Contract.DataEntry.tableName, new String[]
                        {
                                Contract.DataEntry.columnQuestion,
                                Contract.DataEntry.columnAnswer,
                                Contract.DataEntry.columnId
                        },
                Contract.DataEntry.columnAnswered + "=?", new String[]{"UNANSWERED"}, null, null, null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        if(count > 0) {
            ArrayList<Question> myQuestions = new ArrayList<Question>();
            for (int i = 0; i < count; i++) {
                myQuestions.add(new Question(cursor.getString(cursor.getColumnIndex(Contract.DataEntry.columnQuestion)),
                        cursor.getString(cursor.getColumnIndex(Contract.DataEntry.columnAnswer)),
                        Long.parseLong(cursor.getString(cursor.getColumnIndex(Contract.DataEntry.columnId)))));
                cursor.moveToNext();
            }
            database.close();
            cursor.close();
            return myQuestions;
        }
        else
        {
            return  null;
        }
    }
    public void updateQuestion(Question q)
    {
        ContentValues values = new ContentValues();
        values.put(Contract.DataEntry.columnQuestion,q.getQuestion());
        values.put(Contract.DataEntry.columnAnswer,q.getAnswer());
        values.put(Contract.DataEntry.columnId, String.valueOf(q.getId()));
        values.put(Contract.DataEntry.columnAnswered, "ANSWERED");
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Contract.DataEntry.tableName, values, Contract.DataEntry.columnQuestion + "=?", new String[]{q.getQuestion()});
        db.close();
    }
    public boolean DataBaseIsEmpty()
    {
        ArrayList<Question> q = readUnansweredQuestion();
        if (q == null){return true;}
        return false;
    }
    public boolean DeleteQuestion(Question question)
    {
        try
        {
            SQLiteDatabase questionBase = this.getReadableDatabase();
            questionBase.delete(Contract.DataEntry.tableName,Contract.DataEntry.columnQuestion + "=?", new String[]{question.getQuestion()});
        }
        catch (Exception e)
        {
            return  false;
        }
        return true;
    }

}
