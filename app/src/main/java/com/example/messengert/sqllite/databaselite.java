package com.example.messengert.sqllite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class databaselite extends SQLiteOpenHelper {
     
    public databaselite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public Cursor getToken(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }
    public void deleteToken( ){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM LOGIN");

    }
    public void addToken(String token){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("INSERT INTO LOGIN VALUES(?)", new String[]{token});
    }
    public void addUser(String token,String id,String name){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("INSERT INTO LOGIN VALUES(?)", new String[]{token,id,name});
    }
    public void QueryDb(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
