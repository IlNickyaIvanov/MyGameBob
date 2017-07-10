package com.example.a1.mygame;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

//редактор уровней (используются методы из kodParser)
public class ActivityLevelEditor extends AppCompatActivity {

    int screenX, screenY;

    static LinearLayout layoutEnd, layoutStart;

    static TextView etEndx, etEndy, etStartx, etStarty;
    public static String name, map;
    public static int lines, columns;
    FancyButton delButton;
    FancyButton saveButton;

    static SQLiteDatabase db;
    Cursor userCursor;
    static long userId = 0;

    public static int IconType;
    public static boolean setEnd, setStart;
    static ArrayList<EditSquare> Squares = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> edMap = new ArrayList<>();
    int size, fromX, fromY;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_editor);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenX = metrics.widthPixels;
        screenY = metrics.heightPixels;

        etStartx = (TextView) findViewById(R.id.etSTX);
        etStarty = (TextView) findViewById(R.id.etSTY);
        etEndx = (TextView) findViewById(R.id.etENDX);
        etEndy = (TextView) findViewById(R.id.etENDY);
        delButton = (FancyButton) findViewById(R.id.deleteButton);
        saveButton = (FancyButton) findViewById(R.id.saveButton);
        layoutEnd = (LinearLayout) findViewById(R.id.end_layout);
        layoutStart = (LinearLayout) findViewById(R.id.start_layout);

        db = ActivityLevelMenu.dbHelperMylvl.open();

        Bundle extras = getIntent().getExtras();

        userId = 0;
        //если уровень не новый, то получаются уже сущестсвующие данные из Базы по id
        if (extras != null) {
            userId = extras.getLong("id");
        }
        // если 0, то добавление
        if (userId > 0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + "my_levels" + " where " +
                    "_id" + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();

            etStartx.setText(userCursor.getString(userCursor.getColumnIndex("startx")));
            etStarty.setText(userCursor.getString(userCursor.getColumnIndex("starty")));
            etEndx.setText(userCursor.getString(userCursor.getColumnIndex("endx")));
            etEndy.setText(userCursor.getString(userCursor.getColumnIndex("endy")));
            lines = userCursor.getInt(userCursor.getColumnIndex("lines"));
            columns = userCursor.getInt(userCursor.getColumnIndex("columns"));
            map = userCursor.getString(userCursor.getColumnIndex("map"));

            userCursor.close();
        } else {
            // создается новый уровень, поэтому
            // скрываем кнопку удаления
            map = "000\n000\n000";
            lines = 3;
            columns = 3;
            delButton.setVisibility(View.GONE);
        }
        int k = 0;
        edMap.clear();
        //создание карты
        for (int i = 0; i < lines; i++) {
            ArrayList<Integer> line = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                line.add(Character.getNumericValue(map.charAt(k)));
                k++;
            }
            k++;
            edMap.add(line);
        }
        setMAP(edMap);
        ReStartTarget();
    }

    //метод определяет положение цели на карте или её отсутствие
    void ReStartTarget() {
        if (etEndx.getText().toString().equals(""))
            EditSquare.target.clearTarget();
        else {
            int targetX = Integer.parseInt(etEndx.getText().toString());
            int targetY = Integer.parseInt(etEndy.getText().toString());
            EditSquare.target.SetNewTarget(targetX, targetY);
        }
        if (etStartx.getText().toString().equals(""))
            EditSquare.start.clearStart();
        else {
            int targetX = Integer.parseInt(etStartx.getText().toString());
            int targetY = Integer.parseInt(etStarty.getText().toString());
            EditSquare.start.SetNewStart(targetX, targetY);
        }
    }

    //создание видимой карты по ключу
    public void setMAP(ArrayList<ArrayList<Integer>> edmap) {
        if (EditSquare.target != null) EditSquare.target.clearTarget();
        if (EditSquare.start != null) EditSquare.start.clearStart();
        for (int i = 0; i < Squares.size(); i++) {
            Squares.get(i).DELETE();
        }
        int sizeX = screenX / 2 / columns;
        int sizeY = (screenY - screenY / 10) / lines;
        if (sizeY < sizeX) size = sizeY;
        else size = sizeX;

        Squares.clear();
        fromX = screenX / 4 + (screenX / 2 - size * columns) / 2;
        fromY = (screenY - size * lines) / 2;
        int bigLoop = edmap.size();
        for (int i = 0; i < bigLoop; i++) {
            int smallLoop = edmap.get(i).size();
            for (int j = 0; j < smallLoop; j++) {
                Squares.add(
                        new EditSquare(this, fromX + j * size, fromY + i * size, size, edmap.get(i).get(j), i, j));
            }
        }
        ReStartTarget();
    }

    //парсинг карты в формат, нужный для БД
    public String getStringMAP(List<ArrayList<Integer>> edmap) {
        String map = "";
        for (int j = 0; j < lines; j++) {
            for (int i = 0; i < columns; i++) {
                map += edmap.get(j).get(i);
            }
            map += "\n";
        }
        return map;
    }

    //методы, которые говорят сами за себя XD
    void linePLUS() {
        ArrayList<Integer> edLine = new ArrayList<>();
        for (int i = 0; i < columns; i++) {
            edLine.add(i, 0);
        }
        edMap.add(edLine);
        setMAP(edMap);
    }

    void lineMIN() {
        edMap.remove(edMap.size() - 1);
        setMAP(edMap);
    }

    void colPLUS() {
        for (int i = 0; i < edMap.size(); i++) {
            edMap.get(i).add(0);
        }
        setMAP(edMap);
    }

    void colMIN() {
        for (int i = 0; i < edMap.size(); i++) {
            edMap.get(i).remove(edMap.get(i).size() - 1);
        }
        setMAP(edMap);
    }

    public void coline(View view) {

        switch (view.getId()) {
            case (R.id.colplus):
                if (columns < 20) {
                    columns++;
                    colPLUS();
                }
                break;
            case (R.id.colmin):
                if (columns > 1) {
                    columns--;
                    colMIN();
                }
                break;
            case (R.id.lineplus):
                if (lines < 20) {
                    lines++;
                    linePLUS();
                }
                break;
            case (R.id.linemin):
                if (lines > 1) {
                    lines--;
                    lineMIN();
                }
                break;
            case (R.id.stone):
                IconType = 0;
                break;
            case (R.id.lava):
                IconType = 1;
                break;
            case (R.id.kislota):
                IconType = 2;
                break;
            case (R.id.food):
                IconType = 3;
                break;
            default:
                break;
        }
    }

    //проверка на правильность данных и запуск окна с именем
    public void save(View view) {
        if (etStartx.getText().toString().equals("")) {
            Utils.AlertDialog(this, getString(R.string.attention), getString(R.string.no_start), getString(R.string.enter));
        } else {
            db = ActivityLevelMenu.dbHelperMylvl.open();
            userCursor = db.rawQuery("select name from " + "my_levels" + " where " +
                    "_id" + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            if (userId > 0) Utils.EditAlert(getString(R.string.name),
                    userCursor.getString(userCursor.getColumnIndex("name")), this, this);
            else Utils.EditAlert(getString(R.string.name), null, this, this);
            userCursor.close();
            map = getStringMAP(edMap);
        }
    }

    //загрузка в БД
    public static void upload() {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_NAME, name);
        int startx = (!etStartx.getText().toString().equals("")) ? Integer.parseInt(etStartx.getText().toString()) : 0;
        int starty = (!etStarty.getText().toString().equals("")) ? Integer.parseInt(etStarty.getText().toString()) : 0;
        int endx = (!etEndx.getText().toString().equals("")) ? Integer.parseInt(etEndx.getText().toString()) : -1;
        int endy = (!etEndy.getText().toString().equals("")) ? Integer.parseInt(etEndy.getText().toString()) : -1;
        if (endx == -1 && endy > -1) endx = 0;
        else if (endy == -1 && endx > -1) endy = 0;
        cv.put("startx", startx);
        cv.put("starty", starty);
        cv.put("endx", endx);
        cv.put("endy", endy);
        cv.put("lines", lines);
        cv.put("columns", columns);
        cv.put("map", map);
        db = ActivityLevelMenu.dbHelperMylvl.open();
        if (userId > 0) {
            db.update("my_levels", cv, DBHelper.COLUMN_ID + "=" + String.valueOf(userId), null);
        } else {
            db.insert("my_levels", null, cv);
        }
    }

    public void delete(View view) {
        db = ActivityLevelMenu.dbHelperMylvl.open();
        db.delete("my_levels", "_id = ?", new String[]{String.valueOf(userId)});
        goHome();
    }

    //закрытие без сохранения
    private void goHome() {
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
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, ActivityLevelMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
        super.onBackPressed();
    }

    public void setEnd(View view) {
        setEnd = true;
        layoutEnd.startAnimation(AnimationUtils.loadAnimation(this, R.anim.lava));
    }

    public void setStart(View view) {
        setStart = true;
        layoutStart.startAnimation(AnimationUtils.loadAnimation(this, R.anim.lava));
    }
}