package com.example.kimea.myapplication.util;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
    private static final String TAG = "DB";
    private static  final String DATABASE_TABLE_ONEUSER = "CREATE TABLE oneUser(user_seq INTEGER PRIMARY KEY, userId TEXT )";
    private static  final String DATABASE_TABLE_TOKEN = "create table token(token text primary key);";
    private static  final String DATABASE_TABLE_FRIEND = "create table friend(friendemail text, friendnick text, friendimg text, friendText text DEFAULT '') ";
    static Context mycontext;
    static final String name = "divice.db";

    public DBHelper(Context context) {
        super(context, name, null, 1);
        mycontext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String sql =
                "create table token("+
                        "token text primary key);";
        String sql2 =
                "create table profile(profileText text,profileImg BLOB);";
       // String sql3 =
            //    "create table divice(user text primary key,token text);";
        db.execSQL(DATABASE_TABLE_TOKEN);
        db.execSQL(DATABASE_TABLE_FRIEND);
      //  db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists divice;");
    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM token", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)+"\n";
        }

        return result;
    }

    public String getUser() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT user FROM divice", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)+"\n";
        }

        return result;
    }
    public void drop(String token){

    }


}
