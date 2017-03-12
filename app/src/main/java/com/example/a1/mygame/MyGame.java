package com.example.a1.mygame;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class MyGame extends Activity {
    private static int StartX,StartY;
    private static int EndX,EndY;

    final static int onTick = 1000;//скорость движение робота 'млс'

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

    static KodParser kodParser;
    static Robot robot;

    private static long back_pressed;

    TextView textView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_game);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenX=metrics.widthPixels;
        screenY=metrics.heightPixels;

        textView = (TextView)findViewById(R.id.TextView1);
        editText = (EditText) findViewById(R.id.editText);

        MyTimer timer = new MyTimer();timer.start();

        LEVEL_NUM = getIntent().getIntExtra("level_num", 1);
        level_key=LevelMenu.getLevel(LEVEL_NUM);//ключ создания уровня
        square = new Square[level_key.length][level_key[0].length];
        size=screenX/2/square[0].length;//РАЗМЕР КЛЕТОК
        StartX = LevelMenu.StartX;//ЗАДАНИЕ-
        StartY = LevelMenu.StartY;//-НАЧАЛЬНЫХ КООРДИНАТ
        EndX = LevelMenu.EndX;// И КОНЕЧНЫХ
        EndY = LevelMenu.EndY;

        for (int y = 0; y < square.length; y++) {
            for (int x = 0; x < square[0].length; x++) {
                switch (level_key[y][x]) {
                    case (1):square[y][x] = new Square_lava(this, (size * x), (size * y) + screenY/17, size);
                        break;
                    default:square[y][x] = new Square_empty(this, (size * x), (size * y) + screenY/17, size);
                        break;
                }
            }
        }

        robot = new Robot(this,square[0][0].x,square[0][0].y,size,screenX,screenY);//!ВАЖНО!создание робота в [0][0]
        robot.RobotMove(square[StartY][StartX].y,square[StartY][StartX].x,StartY,StartX);//ПЕРЕДВИЖЕНИЕ В "СТАРТОВЫЕ"
        kodParser = new KodParser(StartX,StartY,square);//СОЗДАНИЕ "ПАРСЕРА"
        // И ПЕРЕДАЧА НАЧАЛЬНЫХ КООРДИНАТ ДЛЯ СИНХРОНИЗАЦИИ c положением робота

        if (LEVEL_NUM==1 || LEVEL_NUM==2||LEVEL_NUM==3) {
            Tutorial tutorial = new Tutorial(LEVEL_NUM, this);
        }
        }



    public void Comands(View view) {
        if(!NOTshowPopUp)showPopupMenu(view);
        else Utils.AlertDialog(this,"Задание "+LEVEL_NUM,
                "Постарайся выполнить это задание без помощи автоввода.",
                "ок");
    }
    public void onClickStart(View view) {
        if (!move) {
            if(!Tutorial.IsTutorial) {
                robot.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX);//ПЕРЕДВИЖЕНИЕ В "СТАРТОВЫЕ"
                kodParser.x = StartX;
                kodParser.y = StartY;
            }
            String text = editText.getText().toString();
            action = kodParser.kodParser(text);
            move = true;
            //if(!text.isEmpty())editText.setText(reformatKOD(text));
        }
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
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Utils.makeToast(this,"Нажми еще раз для выхода.");
        back_pressed = System.currentTimeMillis();
    }
    public void update() {
        //начало выполнения программы
        if (move){//включается при нажатии ПУСК
            //если в коде ошибка
            if (AlertDialogMessage!=null && kodParser.isKodERROR()){
                Utils.AlertDialog(this,"Ошибка в коде...",AlertDialogMessage,"ок");
                editText.setSelection(kodParser.start, kodParser.stop);
                move=false; count = 0; action = 0;
                kodParser.setAction(0);
            }
            else if (action!=0) {
                textView.setText("  робот шагает " + kodParser.ComandName[count]);
                robot.RobotMove(
                        square[(kodParser.ARy[count])][(kodParser.ARx[count])].y,
                        square[(kodParser.ARy[count])][(kodParser.ARx[count])].x,
                        kodParser.ARy[count],kodParser.ARx[count]);//перемещение в клетку [y][x]
                count++;//перебор элементов массивов "положения" до action
            }
        }

        else {textView.setText("  робот стоит "+kodParser.y+" "+kodParser.x);
            robot.RobotMove(robot.y,robot.x,robot.sqY,robot.sqX); //КОСТЫЛЬ, ИНАЧЕ АНИМАЦИЯ ВИСНЕТ
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
            else if (kodParser.x==EndX && kodParser.y == EndY && LEVEL_NUM>=1 && !Tutorial.isTask())
                Utils.AlertDialog(this,"Уровень "+LEVEL_NUM+"  Пройден!",
                        "Ух ты! А ты не такой салага, как я думал...",
                        "ок");
            else if (LEVEL_NUM>1 && !Tutorial.IsTutorial){
                Utils.AlertDialog(this,"Уровень"+LEVEL_NUM,
                        "Боб не дошел до конца лабиринта.\nПопробуй еще...",
                        "ок");
            }
            else Utils.makeToast(this,toast);//отчет об выполении

        }

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

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popup_menu); // Для Android 4.0
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

    String reformatKOD(String text){
        String text2="";
        String line[]=text.split(";");
        text2+=line[0]+";";
        text2+=line[line.length-1];
        for (int i =1;i<line.length-1;i++){
            if(!line[i].contains("\n")){
                line[i]+=";\n";
                text2+=line[i];
            }
            else text2+=line[i]+";";
        }
        return text2;
    }
}
