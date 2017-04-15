package com.example.a1.mygame;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActivityLevelEditor extends AppCompatActivity {
    EditText etName,etID,etStartx,etStarty,etEndx,etEndy,etLines,etColumns,etMap;
    Button delButton;
    Button saveButton;
    DBHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long back_pressed;
    long userId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_editor);

        etName = (EditText) findViewById(R.id.etName);
        etID = (EditText) findViewById(R.id.etID);
        etStartx = (EditText) findViewById(R.id.etSTX);
        etStarty = (EditText) findViewById(R.id.edSTY);
        etEndx = (EditText) findViewById(R.id.etENDX);
        etEndy = (EditText) findViewById(R.id.edENDY);
        etLines = (EditText) findViewById(R.id.etLINES);
        etColumns = (EditText) findViewById(R.id.etCOLUMNS);
        etMap = (EditText) findViewById(R.id.etMAP);

        delButton = (Button) findViewById(R.id.deleteButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        db = ActivityLevelMenu.dbHelperMylvl.getReadableDatabase();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }
        // если 0, то добавление
        if (userId > 0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + "my_levels" + " where " +
                    "_id" + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            etName.setText(userCursor.getString(userCursor.getColumnIndex("name")));
            etID.setText(userCursor.getString(userCursor.getColumnIndex("_id")));
            etStartx.setText(userCursor.getString(userCursor.getColumnIndex("startx")));
            etStarty.setText(userCursor.getString(userCursor.getColumnIndex("starty")));
            etEndx.setText(userCursor.getString(userCursor.getColumnIndex("endx")));
            etEndy.setText(userCursor.getString(userCursor.getColumnIndex("endy")));
            etLines.setText(userCursor.getString(userCursor.getColumnIndex("lines")));
            etColumns.setText(userCursor.getString(userCursor.getColumnIndex("columns")));
            etMap.setText(userCursor.getString(userCursor.getColumnIndex("map")));
            userCursor.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }
    }
    public void save(View view){
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_NAME, etName.getText().toString());
        cv.put(DBHelper.COLUMN_STARTX, Integer.parseInt(etStartx.getText().toString()));
        cv.put(DBHelper.COLUMN_STARTY, Integer.parseInt(etStarty.getText().toString()));
        cv.put(DBHelper.COLUMN_ENDX, Integer.parseInt(etEndx.getText().toString()));
        cv.put(DBHelper.COLUMN_ENDY, Integer.parseInt(etEndy.getText().toString()));
        cv.put(DBHelper.COLUMN_LINES, Integer.parseInt(etLines.getText().toString()));
        cv.put(DBHelper.COLUMN_COLUMNS, Integer.parseInt(etColumns.getText().toString()));
        cv.put(DBHelper.COLUMN_MAP, etMap.getText().toString());
        if (userId > 0) {
            db.update("my_levels", cv, DBHelper.COLUMN_ID + "=" + String.valueOf(userId), null);
        } else {
            db.insert("my_levels", null, cv);
        }
        goHome();
    }
    public void delete(View view){
        db.delete("my_levels", "_id = ?", new String[]{String.valueOf(userId)});
        goHome();
    }
    private void goHome(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, ActivityLevelMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ActivityLevelMenu.class);
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            // закрываем подключение
            db.close();
            // переход к главной activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.startActivity(intent);
            this.finish();
            super.onBackPressed();
        }
        else
            Utils.makeToast(this,"Нажми еще раз для выхода.");
        back_pressed = System.currentTimeMillis();
    }
}
