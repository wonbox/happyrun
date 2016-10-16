package org.androidtown.streetmovement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * 데이터베이스 생성 및 쿼리문 코드
 */
public class Database extends SQLiteOpenHelper {
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //새로운 테이블 생성
        db.execSQL("CREATE TABLE IF NOT EXISTS RUN_LIST(_id INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "rundate DATETIME NOT NULL , runtime TEXT NOT NULL, distance REAL NOT NULL , " +
                "runhour TEXT NOT NULL , runcal REAL NOT NULL , " +
                "startlat REAL NOT NULL , startlon REAL NOT NULL , stoplat REAL NOT NULL , stoplon REAL NOT NULL);");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String query)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
    public void delete(String query){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
    public Cursor PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select * from RUN_LIST;", null);
    }

    public Cursor GraphPrint(){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select rundate , Sum(distance) , Sum(runcal) from RUN_LIST group by rundate;", null);
    }

    public Cursor MapPrint(int id){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select startlat, startlon, stoplat, stoplon from RUN_LIST where _id = " + id + ";", null);
    }

}
