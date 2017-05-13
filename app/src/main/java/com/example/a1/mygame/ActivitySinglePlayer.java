package com.example.a1.mygame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class ActivitySinglePlayer extends FragmentActivity {
    static int StartX,StartY;
    static boolean RestartRobotXY=true;
    private static int EndX,EndY;

    private final int onTick = 1000;//скорость движение робота 'млс'
    private String level_name;
    static int ComandLimit=100;//лимит команд для робота

    boolean pause, move;
    static boolean NOTshowPopUp;

    static String toast = "выполнение\n" + "программы завершилось\n" + "УСПЕШНО";
    static String AlertDialogMessage;
    //информаторы

    static int screenX, screenY, size;

    static int count, action;//счетчики движения и срабатывания таймера
    //!!!ВНИМАНИЕ!!!Важно, чтобы они были статическими Т.К. KodParser их использует

    private int LEVEL_NUM;
    int level_key[][];// шифр, по нему будет создан уровень
    static Square square[][];
    static ArrayList<int[]> foodSquares = new ArrayList<>();

    static KodParser kodParser;
    static Robot robot;

    private static long back_pressed;

    static EditText editText;
    FancyButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_single_player);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenX=metrics.widthPixels;
        screenY=metrics.heightPixels;

        editText = (EditText) findViewById(R.id.editText);

        button = (FancyButton) findViewById(R.id.button_mov);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopupMenu(view,true);
                return false;
            }
        });

        level_name = (ActivityLevelMenu.level_name==null) ? ""+LEVEL_NUM: ActivityLevelMenu.level_name;

        MyTimer timer = new MyTimer();timer.start();
        LEVEL_GETTER();
        float startY=(screenY-size*square.length)/2;
        float startX = (screenX/2-size*square[0].length)/2;
        robot = new Robot(this,square[0][0].x,square[0][0].y,size,screenY,onTick,1,0,startX,startY);//!ВАЖНО!создание робота в [0][0]
        robot.RobotMove(square[StartY][StartX].y,square[StartY][StartX].x,StartY,StartX,false);//ПЕРЕДВИЖЕНИЕ В "СТАРТОВЫЕ"
        kodParser = new KodParser(StartX,StartY,square,ComandLimit);//СОЗДАНИЕ "ПАРСЕРА"
        // И ПЕРЕДАЧА НАЧАЛЬНЫХ КООРДИНАТ ДЛЯ СИНХРОНИЗАЦИИ c положением робота
        if(!getIntent().getBooleanExtra("own_level",false))
            new Tutorial(LEVEL_NUM,this);
        }
    void LEVEL_GETTER(){
        LEVEL_NUM = getIntent().getIntExtra("level_num", 1);
        level_key= ActivityLevelMenu.getLevel();//ключ создания уровня
        square = new Square[level_key.length][level_key[0].length];
        size=screenX/2/square[0].length;//РАЗМЕР КЛЕТОК
        StartX = ActivityLevelMenu.StartX;//ЗАДАНИЕ-
        StartY = ActivityLevelMenu.StartY;//-НАЧАЛЬНЫХ КООРДИНАТ
        EndX = ActivityLevelMenu.EndX;// И КОНЕЧНЫХ
        EndY = ActivityLevelMenu.EndY;
        ActivitySinglePlayer.RestartRobotXY=true;

        int sizeX = screenX/2/square[0].length;
        int sizeY = (screenY-screenY/10)/square.length;
        if(sizeY<sizeX)size = sizeY;
        else size = sizeX;
        float startY=(screenY-size*square.length)/2;
        float startX = (screenX/2-size*square[0].length)/2;
        for (int y = 0; y < square.length; y++) {
            for (int x = 0; x < square[0].length; x++) {
                switch (level_key[y][x]) {
                    case (1):square[y][x] = new Square_lava(this, (size * x)+startX, (size * y) + startY, size);
                        break;
                    case (2):square[y][x] = new Square_kislota(this, (size * x)+startX, (size * y) + startY, size);
                        break;
                    case (3):square[y][x] = new Square_empty(this, (size * x)+startX, (size * y) + startY, size,true);
                        int []food = {y,x};
                        foodSquares.add(food);
                        break;
                    default:square[y][x] = new Square_empty(this, (size * x)+startX, (size * y) + startY, size,false);
                        break;
                }
            }
        }
        if (EndX!=-1 && EndX!=-1){
            square[EndY][EndX].setTarget();
        }
    }



    public void Move(View view) {
        if(!NOTshowPopUp)showPopupMenu(view,false);
        else Utils.AlertDialog(this,"Задание "+LEVEL_NUM,
                "Постарайся выполнить это задание без помощи автоввода.",
                "ок");
    }
    public void Operators(View view) {
        if(!NOTshowPopUp)showPopupMenu(view,false);
        else Utils.AlertDialog(this,"Задание "+LEVEL_NUM,
                "Постарайся выполнить это задание без помощи автоввода.",
                "ок");
    }


    public void onClickStart(View view) {
        if (!move) {
            if(RestartRobotXY) {
                robot.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX,false);//ПЕРЕДВИЖЕНИЕ В "СТАРТОВЫЕ"
                kodParser.x = StartX;
                kodParser.y = StartY;
                for (int i=0;i<foodSquares.size();i++){
                    int food []=foodSquares.get(i);
                    if(square[food[0]][food[1]].food != null)
                        square[food[0]][food[1]].food.Restart();
                }
            }
            String text = editText.getText().toString();
            action = kodParser.kodParser(text);
            move = true;
        }
    }


    public void onClickRef(View view) {
        String text = editText.getText().toString();
        if(!text.isEmpty()){editText.setText(reformatKOD(text));
            Utils.AlertDialog(this,"...","Код успешно переформатирован!","ок");
        }
        else Utils.AlertDialog(this,"...","Нет текста для\n переформатирования кода!","ок");
    }

    @Override
    protected void onPause(){
        super.onPause();
        pause=true;
    }
    @Override
    protected void onResume(){
        super.onPause();
        pause=false;
    }
    public void update() {
        //начало выполнения программы
        if (move){//включается при нажатии ПУСК
           Handler();
        }
        else {
            robot.MoveMySelf(false);
        }
        //конец списка команд
        if (count == action && move ){//конец движения
            move=false;
            kodParser.action=0;
            count = 0;

            if (AlertDialogMessage!=null){
                Utils.AlertDialog(this,"Дальше не могу.",AlertDialogMessage,"ок");
                editText.setSelection(kodParser.start, kodParser.stop);}
            //по прохождению уровня...
            else if (checkTask())
                if (!getIntent().getBooleanExtra("own_level",false))
                    Utils.TwoButtonAllertDialog(this,"Уровень "+level_name+"  Пройден!",
                            "Ух ты! А ты не такой салага, как я думал...",
                            "МЕНЮ","ДАЛЬШЕ",LEVEL_NUM);
                else Utils.AlertDialog(this,"Уровень '"+level_name+"'  пройден!",
                        "Ух ты! А ты не такой салага, как я думал!",
                       "ОК");
            else if (!Tutorial.task){
                Utils.makeToast(this, "Боб не выполнил задание.\nПопробуй еще...");
            }
            else Utils.makeToast(this,toast);//отчет об выполении

        }

    }

    void Handler(){
        //если в коде ошибка
        if (AlertDialogMessage!=null && kodParser.isKodERROR()){
            Utils.AlertDialog(this,"Ошибка в коде...",AlertDialogMessage,"ок");
            editText.setSelection(kodParser.start, kodParser.stop);
            move=false; count = 0; action = 0;
            kodParser.setAction(0);
        } else if (action!=0) {
            //textView.setText("  робот шагает " + kodParser.Anim[count]);
            if (kodParser.Anim[count]!=0) {
                robot.SearchAnim(kodParser.Anim[count]);
                kodParser.Anim[count] = 0;
            }
            for (int i=0;i<foodSquares.size();i++){
                int food []= foodSquares.get(i);
                if ((kodParser.ARy[count])==food[0] && (kodParser.ARx[count])==food[1])
                    square[food[0]][food[1]].food.Eat();
            }
            robot.RobotMove(
                    square[(kodParser.ARy[count])][(kodParser.ARx[count])].y,
                    square[(kodParser.ARy[count])][(kodParser.ARx[count])].x,
                    kodParser.ARy[count], kodParser.ARx[count], false);//перемещение в клетку [sqY][sqX]
            count++;//перебор элементов массивов "положения" до action
        }
    }

    static boolean checkTask(){
        boolean complete=true;
        if (!Tutorial.tutorCOMPL)complete=false;
        if (EndX !=-1 && EndY != -1)
            if (kodParser.x!=EndX || kodParser.y != EndY)
                complete = false;
        for (int i = 0; i < foodSquares.size(); i++) {
            int food[] = foodSquares.get(i);
            if (!square[food[0]][food[1]].food.isEaten())
                complete = false;
        }
        return complete;
    }


    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, onTick);
        }
        @Override
        public void onTick(long millisIntilFinished){if(!pause)update();}
        @Override
        public void onFinish() {
        }
    }

     void showPopupMenu(View v, boolean isLongClick) {
        PopupMenu popupMenu = new PopupMenu(this, v);
         if (v.getId()==R.id.button_mov && !isLongClick)
             popupMenu.inflate(R.menu.popup_move_menu); // Для Android 4.0
         else if (v.getId()==R.id.button_mov && isLongClick)
             popupMenu.inflate(R.menu.popup_condition_menu);
         else if (v.getId()==R.id.button_oper)
             popupMenu.inflate(R.menu.popup_operators_menu);
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.up:editText.getText().insert(editText.getSelectionStart(),"up ;\n");return true;
                            case R.id.down:editText.getText().insert(editText.getSelectionStart(),"down ;\n");return true;
                            case R.id.right:editText.getText().insert(editText.getSelectionStart(),"right ;\n");return true;
                            case R.id.left:editText.getText().insert(editText.getSelectionStart(),"left ;\n");return true;
                            case R.id.repeat:
                                editText.getText().insert(editText.getSelectionStart(),"repeat (2){\n \n};\n");return true;
                            case R.id.my_if:
                                editText.getText().insert(editText.getSelectionStart(),"if ( ){\n \n};\n");return true;
                            case R.id.my_else:
                                editText.getText().insert(editText.getSelectionStart(),"if ( ){\n \n}else {\n \n};\n");return true;
                            case R.id.my_while:
                                editText.getText().insert(editText.getSelectionStart(),"while ( ){\n \n};\n");return true;
                            case R.id.con_up:
                                editText.getText().insert(editText.getSelectionStart(),"up_");return true;
                            case R.id.con_down:
                                editText.getText().insert(editText.getSelectionStart(),"down_");return true;
                            case R.id.con_right:
                                editText.getText().insert(editText.getSelectionStart(),"right_");return true;
                            case R.id.con_left:
                                editText.getText().insert(editText.getSelectionStart(),"left_");return true;
                            case R.id.con_wall:
                                editText.getText().insert(editText.getSelectionStart(),"wall");return true;
                            case R.id.con_sweet:
                                editText.getText().insert(editText.getSelectionStart(),"sweet");return true;
                            default:return false;
                        }
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        popupMenu.show();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivitySinglePlayer.this, ActivityLevelMenu.class);
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            Tutorial.task = false;
            Utils.AlertDialogVisible=false;
            this.startActivity(intent);
            this.finish();
            super.onBackPressed();
        }
        else
            Utils.makeToast(this,"Нажми еще раз для выхода.");
        back_pressed = System.currentTimeMillis();
        foodSquares.clear();
    }
    String  reformatKOD(String text){
        text=text.replaceAll("\n","");
        for (int i=0;i<text.length();i++){
            if(text.charAt(i)==';' || text.charAt(i)=='{')
                text = text.substring(0,i+1)+"\n"+text.substring(i+1,text.length());
        }
        return  text;
    }
}
