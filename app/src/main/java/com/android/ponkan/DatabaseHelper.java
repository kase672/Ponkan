package com.android.ponkan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "booklist.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_BOOKS = "books";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**テーブルを作成し列(カラム)を作成*/
    //スーパクラスであるSQLiteOpenHelperclassからもアクセスする必要があるのでprivateではだめ
    @Override
    public void onCreate(SQLiteDatabase db) {
        //MainFragmentのテーブル
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT" +
                ")";
        db.execSQL(CREATE_BOOKS_TABLE);
    }

    /**古いテーブルを削除*/
    //DBバージョンとはアプリを起動した際にDBやテーブルの定義を変更する
    // （今回の場合、テーブルをDROPしてCREATEし直す）かを判定するためのパラメータ
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    /**データベースのダウングレードが必要なときに呼び出される*/
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    /**レコードを追加*/
    public void addBook(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        db.insert(TABLE_BOOKS, null, values);
        db.close();
        Log.d("addBook", ""+name+"");
    }

    public List<Map<String, Object>> getAllBooks() {
        List<Map<String, Object>> bookList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BOOKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//rawQueryは生のSQL文が使える
        //最初カーソルは-1にあるので0から始まるように
        //moveToNextを実行してtrueならwhileへ
        while (cursor.moveToNext()) {
            //カーソルの行の名前を取ってきてStringに変換
            int columnIndex = cursor.getColumnIndex(COLUMN_NAME);
            String bookName = cursor.getString(columnIndex);
            Map<String, Object> book = new HashMap<>();
            book.put("name", bookName);
            bookList.add(book);
        }
        cursor.close();
        db.close();
        return bookList;
    }



    public void deleteBook(int position) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_BOOKS + " LIMIT 1 OFFSET " + position, null);
        cursor.moveToPosition(0);
        Log.d("delete", ""+ cursor+"");
        if (cursor != null) {

            String idToDelete = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
            db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{idToDelete});
            cursor.close();
            db.close();
        }
    }
}

