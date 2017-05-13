package com.example.a1.mygame;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.a1.mygame.fragments.FragmentFirst;
import com.example.a1.mygame.fragments.FragmentSecond;
import com.example.a1.mygame.fragments.FragmentTherd;

public class ActivityLevelMenu extends AppCompatActivity {
    public static boolean TwoPlayers;
    private static int test_zone[][] = new int[6][6];
    public static int level[][];
    int ButtonID;

    public static Activity activity;

    public static DBHelper dbHelper1,dbHelperMylvl;
    static SQLiteDatabase db;
    static Cursor userCursor;
    static SimpleCursorAdapter userAdapter;

    //УРОВНИ
    public static int StartX, StartY, EndX, EndY;//начальные и конечные координаты
    public static String level_name;
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
        activity = this;
        FragmentTherd.mylevelsList = (ListView)findViewById(R.id.my_level_list);

        for (int y = 0; y < test_zone.length; y++) {
            for (int x = 0; x < test_zone[y].length; x++) {
                test_zone[y][x] = 0;
            }
        }

        dbHelper1 = new DBHelper(getApplicationContext(), 1);
        dbHelper1.create_db();
        dbHelperMylvl = new DBHelper(getApplicationContext());

        int level = getIntent().getIntExtra("level_num", -1);
        if (level != -1) SELECT_LEVEL(level, this,false);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getWindow().setBackgroundDrawableResource(R.drawable.background);
        }else{
            getWindow().setBackgroundDrawableResource(R.drawable.backgroundvertical);
        }
    }

    public void onLevelBtn(View view) {
        int level;
        switch (view.getId()) {
            case R.id.button1:
                level = 1;
                break;
            case R.id.button2:
                level = 2;
                break;
            case R.id.button3:
                level = 3;
                break;
            case R.id.twoplayers:
                level = 0;
                TwoPlayers = true;
                break;
            default:
                level = -1;
                break;
        }
        if (level != -1) SELECT_LEVEL(level, this,false);
        ButtonID = view.getId();
    }

    public void SELECT_LEVEL(int level, Activity activity, boolean own_level) {
        Intent intent;
        if (!TwoPlayers)
            intent = new Intent(activity, ActivitySinglePlayer.class);
        else intent = new Intent(activity, ActivityTwoPlayers.class);
        DBHelper dbHelper;
        if (own_level){
            dbHelper = dbHelperMylvl;
        }
        else dbHelper = dbHelper1;
        if (dbHelper.isRowEx(level)) {

            String sLab = dbHelper.getMAP(level);
            intent.putExtra("level_num", level);
            intent.putExtra("own_level",own_level);

            level_name = dbHelper.getNAME(level);
            StartX = dbHelper.getSTARTX(level);
            StartY = dbHelper.getSTARTY(level);
            EndX = dbHelper.getENDX(level);
            EndY = dbHelper.getENDY(level);
            ActivityLevelMenu.level = parseLab(sLab, dbHelper.getLINES(level), dbHelper.getCOLUMNS(level));

            startActivity(intent);
            this.finish();
        }else  Utils.AlertDialog(this, "Уровень", "Следующий уровень находится в разработке.\n" +
                "В скором времени он будет доступен...", "Выбрать другой");
    }


    public static int[][] getLevel() {
        return level;
    }

    public static int[][] parseLab(String s, int Y, int X) {
        int lab[][] = new int[Y][X];
        int k = 0;
        for (int j = 0; j < Y; j++) {
            for (int i = 0; i < X; i++) {
                lab[j][i] = Character.getNumericValue(s.charAt(k));// берём следующий по очереди символ из строки
                k++;
            }
            k += 1; // в конце строки двухбайтный перевод строки на новую строчку, его пропускаем
        }
        return lab;
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FragmentFirst
                    return FragmentFirst.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show FragmentFirst different title
                    return FragmentSecond.newInstance(1, "Page # 2");
                case 2: // Fragment # 0 - This will show FragmentFirst different title
                    return FragmentTherd.newInstance(2, "Page # 3",ActivityLevelMenu.activity);
                default:
                    return null;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case (0):
                    return "Уровни ";
                case (1):
                    return "Батл ";
                case (2):
                    return "Мои Уровни";
                default:
                    break;
            }
            return "страница" + position;
        }

    }
    public static void updateList(){
        // открываем подключение
        db = dbHelperMylvl.getWritableDatabase();
        //получаем данные из бд в виде курсора
        userCursor = db.rawQuery("select * from "+ dbHelperMylvl.TABLE_NAME, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DBHelper.COLUMN_NAME, DBHelper.COLUMN_ID};
        // создаем адаптер, передаем в него курсор
        userAdapter = new SimpleCursorAdapter(activity, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        FragmentTherd.updateMyLevelList(userAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }
    // по нажатию на кнопку запускаем LevelEditor для добавления данных
    public void addnewlvl(View view){
        Intent intent = new Intent(this, ActivityLevelEditor.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }
}
