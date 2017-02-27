package com.example.a1.mygame;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;

public class LevelMenu extends Activity {
    private static int test_zone[][]= new int[6][6];
    private static int lava_zone[][]= new int [3][3];
    private static int level_1[][];
    //УРОВНИ
    static int col,row;//"строки" и "столбцы" уровней
    static int StartX,StartY,EndX,EndY;//начальные и конечные координаты
    static String AlertDialogMessage,AlertDialogTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_menu);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        for (int y = 0; y < test_zone.length; y++) {
            for (int x = 0; x < test_zone[y].length; x++) {
                test_zone[y][x] = 0;
            }
        }
        for (int y = 0; y < lava_zone.length; y++) {
            for (int x = 0; x < lava_zone[y].length; x++) {
                lava_zone[y][x] = 1;
            }
        }
        lava_zone[1][1] = 0;
    }

    public void onClickTestZone(View view) {
        Intent intent = new Intent(LevelMenu.this, MyGame .class);
        intent.putExtra("level_num",0);
        col=test_zone.length;
        row=test_zone[0].length;
        StartX = 0; StartY = 0;EndX=-1;EndY=-1;
        AlertDialogMessage = "Это Зона с каменными плитами.\nЕсли Вы не разрабатываете эту игру, то делать здесь нечего...";
        AlertDialogTitle = "Тестовая Зона";
        startActivity(intent);
    }
    public void onClickLavaZone(View view) {
        Intent intent = new Intent(LevelMenu.this, MyGame .class);
        intent.putExtra("level_num",-1);
        col=lava_zone.length;
        row=lava_zone[0].length;
        StartX = 1; StartY = 1;EndX=-1;EndY=-1;
        AlertDialogMessage = "Это Зона с лавой.\nЕсли Вы не разрабатываете эту игру, то делать здесь нечего...";
        AlertDialogTitle = "Зона Лавы";
        startActivity(intent);
    }

    public void onClickLevel1(View view) {
        Intent intent = new Intent(LevelMenu.this, MyGame .class);
        intent.putExtra("level_num",1);
        String sLab=getStringFromAssetFile("level1.txt"); // считываем лабиринт из файла в строку
        col=6; row=6; StartX = 5; StartY = 0;EndX=0;EndY=0;  // размерность массива, будущего лабиринта
        AlertDialogMessage = "Пройди Лабиринт по каменным плитам.\nНа Лаву наступать НЕЛЬЗЯ.";
        AlertDialogTitle = "Уровень 1";
        level_1 = new int[col][row]; // создаём лабиринт, в него будем читать массив из строки
        level_1 = parseLab(sLab,col,row); // парсим лабиринт из строки в массив целых
        startActivity(intent);
    }
    public static int[][] getLevel(int level_num){
        switch (level_num) {
            case -1: return lava_zone;
            case 0: return test_zone;
            case 1: return level_1;
            default:return test_zone;
        }
    }





    String getStringFromAssetFile(String filename) {
        byte[] buffer = null;
        InputStream is;
        try {
            is = getAssets().open(filename);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = new String(buffer);
        return str;
    }
    int [][] parseLab(String s, int x, int y){
        int lab[][] = new int[x][y];
        int k=0;
        for(int j=0; j<y; j++){
            for(int i=0; i<x; i++) {
                lab[j][i] =Character.getNumericValue(s.charAt(k++));// берём следующий по очереди символ из строки
            }
            k+=2; // в конце строки двухбайтный перевод строки на новую строчку, его пропускаем
        }
        return lab;
    }
}
