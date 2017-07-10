package com.example.a1.mygame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DBHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static String DB_PATH; // полный путь к базе данных
    private static String DB_NAME;
    String TABLE_NAME = "";

    static String COLUMN_ID = "_id";
    private static String COLUMN_STARTX = "startx";
    private static String COLUMN_STARTY = "starty";
    private static String COLUMN_ENDX = "endx";
    private static String COLUMN_ENDY = "endy";
    private static String COLUMN_MAP = "map";
    static String COLUMN_NAME = "name";
    private static String COLUMN_LINES = "lines";
    private static String COLUMN_COLUMNS = "columns";

    private Context myContext;

    private SQLiteDatabase db;
    private boolean ownDB;

    DBHelper(Context context, int DATABASE_VERSION) {
        super(context, "custom_lvls.db", null, DATABASE_VERSION);
        DB_NAME = "custom_lvls.db";
        this.TABLE_NAME = "levels";
        this.myContext = context;
        DB_PATH = context.getFilesDir().getPath() + DB_NAME;
        ownDB = true;
    }

    DBHelper(Context context) {
        super(context, "own_lvls", null, DATABASE_VERSION);
        DB_NAME = "own_lvls";
        this.TABLE_NAME = "my_levels";
        ownDB = false;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (TABLE_NAME.equals("my_levels")) {
            sqLiteDatabase.execSQL("CREATE TABLE `my_levels` (\n" +
                    "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "\t`startx`\tINTEGER NOT NULL,\n" +
                    "\t`starty`\tINTEGER NOT NULL,\n" +
                    "\t`endx`\tINTEGER,\n" +
                    "\t`endy`\tINTEGER,\n" +
                    "\t`map`\tTEXT NOT NULL,\n" +
                    "\t`name`\tTEXT UNIQUE,\n" +
                    "\t`lines`\tINTEGER NOT NULL,\n" +
                    "\t`columns`\tINTEGER NOT NULL\n" +
                    ");");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (TABLE_NAME.equals("my_levels")) {
            sqLiteDatabase.execSQL("drop table if exist " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    //инициализация существующей БД
    //---------------------------------------------------------------------------------------------
    void create_db() {
        InputStream myInput;
        OutputStream myOutput;
        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getReadableDatabase();
                //получаем локальную бд как поток
                myInput = myContext.getAssets().open(DB_NAME);
                // Путь к новой бд
                String outFileName = DB_PATH;
                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);
                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        } catch (IOException ex) {
            Log.d("DatabaseHelper", ex.getMessage());
        }
    }

    public SQLiteDatabase open() throws SQLException {
        if (ownDB)
            return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        else return getWritableDatabase();
    }

    //методы работы с данными в БД
    //----------------------------------------------------------------------------------------
    void ADD_NEW_LINE(int startx, int starty,
                      int endx, int endy,
                      String map, String name,
                      int lines, int columns) {
        if (ownDB)
            db = open();
        else
            db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STARTX, startx);
        cv.put(COLUMN_STARTY, starty);
        cv.put(COLUMN_ENDX, endx);
        cv.put(COLUMN_ENDY, endy);
        cv.put(COLUMN_MAP, map);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_LINES, lines);
        cv.put(COLUMN_COLUMNS, columns);
        db.insert(TABLE_NAME, null, cv);
    }

    private Cursor READ(int id) {
        if (id == 0) id = 1;
        if (ownDB)
            db = open();
        else
            db = getWritableDatabase();
        Cursor c;
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        c = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    int CLEAR() {
        if (ownDB)
            db = open();
        else
            db = getWritableDatabase();
        int clearCount = db.delete(TABLE_NAME, null, null);
        return clearCount;
    }

    String UPDATE(int id,
                  int startx, int starty,
                  int endx, int endy,
                  String map, String name,
                  int lines, int columns) {
        if (ownDB)
            db = open();
        else
            db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (id != 0) {
            cv.put(COLUMN_STARTX, startx);
            cv.put(COLUMN_STARTY, starty);
            cv.put(COLUMN_ENDX, endx);
            cv.put(COLUMN_ENDY, endy);
            cv.put(COLUMN_MAP, map);
            cv.put(COLUMN_NAME, name);
            cv.put(COLUMN_LINES, lines);
            cv.put(COLUMN_COLUMNS, columns);
            db.update(TABLE_NAME,
                    cv, COLUMN_ID + "= ?", new String[]{id + ""});
        }
        return "обновлена строка " + id;
    }

    String DELETEbyid(String id) {
        if (ownDB)
            db = open();
        else
            db = getWritableDatabase();
        int delCount = 0;
        if (!id.equals("")) {
            delCount = db.delete(TABLE_NAME,
                    COLUMN_ID + "=" + id, null);
        }
        if (delCount != 0)
            return "удалено " + delCount + "строчек";
        else
            return "элемента с заданным ID НЕТ!";
    }

    boolean isRowEx(int id) {
        boolean ch = false;
        Cursor cursor = READ(id);
        if (cursor.getCount() > 0)
            ch = true;
        return ch;

    }

    //ГЕТЕРЫ
    //-------------------------------------------------------------------------------------------
    public int getSTARTX(int id) {
        Cursor c = READ(id);
        int result = -1;
        if (c.moveToFirst())
            result = c.getInt(c.getColumnIndex(COLUMN_STARTX));
        return result;
    }

    public int getSTARTY(int id) {
        Cursor c = READ(id);
        int result = -1;
        if (c.moveToFirst())
            result = c.getInt(c.getColumnIndex(COLUMN_STARTY));
        return result;
    }

    public int getENDX(int id) {
        Cursor c = READ(id);
        int result = -1;
        if (c.moveToFirst())
            result = c.getInt(c.getColumnIndex(COLUMN_ENDX));
        return result;
    }

    public int getENDY(int id) {
        Cursor c = READ(id);
        int result = -1;
        if (c.moveToFirst())
            result = c.getInt(c.getColumnIndex(COLUMN_ENDY));
        return result;
    }

    public String getMAP(int id) {
        Cursor c = READ(id);
        String result = "";
        if (c.moveToFirst())
            result = c.getString(c.getColumnIndex(COLUMN_MAP));
        return result;
    }

    public String getNAME(int id) {
        Cursor c = READ(id);
        String result = "";
        if (c.moveToFirst())
            result = c.getString(c.getColumnIndex(COLUMN_NAME));
        return result;
    }

    public int getLINES(int id) {
        Cursor c = READ(id);
        int result = -1;
        if (c.moveToFirst())
            result = c.getInt(c.getColumnIndex(COLUMN_LINES));
        return result;
    }

    public int getCOLUMNS(int id) {
        Cursor c = READ(id);
        int result = -1;
        if (c.moveToFirst())
            result = c.getInt(c.getColumnIndex(COLUMN_COLUMNS));
        return result;
    }
}
