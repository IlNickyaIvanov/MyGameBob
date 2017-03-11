package com.example.a1.mygame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;

public class LevelMenu extends AppCompatActivity {
    private static int test_zone[][]= new int[6][6];
    private static int lava_zone[][]= new int [3][3];
    int ButtonID;

    private static int level_1[][];
    //УРОВНИ
    static int StartX,StartY,EndX,EndY;//начальные и конечные координаты
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_menu);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

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

    public void onClick(View view) {
        int level=0;
        switch (view.getId()){
            case R.id.button1:level=1;
                break;
            case R.id.button2:level=2;
                break;
            case R.id.button3:level=3;
                break;
            default:break;
        }
        SELECT_LEVEL(level,this);
        ButtonID=view.getId();
    }

    public void SELECT_LEVEL(int level, Activity activity) {
        if (level==1 || level==2 || level==3) {
            Intent intent = new Intent(activity, MyGame.class);
            String sLab;
            switch (level) {
                case (1):
                    intent.putExtra("level_num", 1);
                     sLab = getStringFromAssetFile("level1.txt");
                    StartX = 0;
                    StartY = 0;
                    EndX = 2;
                    EndY = 0;
                    level_1 = parseLab(sLab, 1, 3);
                    break;
                case (2):
                    intent.putExtra("level_num", 2);
                     sLab = getStringFromAssetFile("level2.txt"); // считываем лабиринт из файла в строку
                    StartX = 5;
                    StartY = 0;
                    EndX = 0;
                    EndY = 0;  // размерность массива, будущего лабиринта
                    level_1 = parseLab(sLab, 6, 6); // парсим лабиринт из строки в массив целых
                    break;

                case (3):
                    intent.putExtra("level_num", 3);
                    StartX = 1;
                    StartY = 1;
                    EndX = -1;
                    EndY = -1;
                    break;
                default:
                    break;
            }
             startActivity(intent);
        }
    }






    public static int[][] getLevel(int level_num){
        switch (level_num) {
            case 1: return level_1;
            case 2: return level_1;
            case 3: return lava_zone;
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
        for(int j=0; j<x; j++){
            for(int i=0; i<y; i++) {
                lab[j][i] =Character.getNumericValue(s.charAt(k++));// берём следующий по очереди символ из строки
            }
            k+=2; // в конце строки двухбайтный перевод строки на новую строчку, его пропускаем
        }
        return lab;
    }
    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FragmentFirst
                    return FragmentFirst.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show FragmentFirst different title
                    return FragmentSecond.newInstance(1, "Page # 2");
                case 2: // Fragment # 0 - This will show FragmentFirst different title
                    return FragmentTherd.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case (0):return "Уровни ";
                case (1):return "Батл " ;
                case (2):return "Мои Уровни";
                default: break;
            }
            return "страница"+position;
        }

    }
}
