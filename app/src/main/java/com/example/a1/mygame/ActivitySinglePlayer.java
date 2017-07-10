package com.example.a1.mygame;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

public class ActivitySinglePlayer extends FragmentActivity {
    static int StartX, StartY;
    static boolean RestartRobotXY = true;
    private static int EndX, EndY;

    private final int onTick = 1000;//скорость движение робота 'млс'
    private String level_name;
    static int ComandLimit = 100;//лимит команд для робота

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
    static ArrayList<int[]> foodSquares;
    static ArrayList<ArrayList<int[]>> blinkySquares;
    Tutorial tutorial;

    static KodParser kodParser;
    static Robot robot;

    private static long back_pressed;

    static Activity singleplayer;
    static EditText editText;
    FancyButton button;
    static String onComplete;//сообщение по выполнению

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_single_player);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenX = metrics.widthPixels;
        screenY = metrics.heightPixels;

        editText = (EditText) findViewById(R.id.editText);

        button = (FancyButton) findViewById(R.id.button_mov);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopupMenu(view, true);
                return false;
            }
        });

        MyTimer timer = new MyTimer();
        timer.start();
        singleplayer = ActivitySinglePlayer.this;
        LEVEL_GETTER();
        float startY = (screenY - size * square.length) / 2;
        float startX = (screenX / 2 - size * square[0].length) / 2;
        robot = new Robot(this, square[0][0].x, square[0][0].y, size, onTick, 1, 0, startX, startY);//!ВАЖНО!создание робота в [0][0]
        robot.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX, false);//ПЕРЕДВИЖЕНИЕ В "СТАРТОВЫЕ"
        kodParser = new KodParser(StartX, StartY, square, ComandLimit, this);//СОЗДАНИЕ "ПАРСЕРА"
        // И ПЕРЕДАЧА НАЧАЛЬНЫХ КООРДИНАТ ДЛЯ СИНХРОНИЗАЦИИ c положением робота
        if (!getIntent().getBooleanExtra("own_level", false))
            tutorial = new Tutorial(LEVEL_NUM, ActivitySinglePlayer.this);
        restartBLINKY();
    }

    void LEVEL_GETTER() {
        LEVEL_NUM = getIntent().getIntExtra("level_num", 1);
        level_name = (ActivityLevelMenu.level_name == null || ActivityLevelMenu.level_name.equals(""))
                ? "" + LEVEL_NUM : ActivityLevelMenu.level_name;
        level_key = ActivityLevelMenu.getLevel();//ключ создания уровня
        foodSquares = new ArrayList<>();
        blinkySquares = new ArrayList<>();
        square = new Square[level_key.length][level_key[0].length];
        size = screenX / 2 / square[0].length;//РАЗМЕР КЛЕТОК
        StartX = ActivityLevelMenu.StartX;//ЗАДАНИЕ-
        StartY = ActivityLevelMenu.StartY;//-НАЧАЛЬНЫХ КООРДИНАТ
        EndX = ActivityLevelMenu.EndX;// И КОНЕЧНЫХ
        EndY = ActivityLevelMenu.EndY;
        ActivitySinglePlayer.RestartRobotXY = true;

        int sizeX = screenX / 2 / square[0].length;
        int sizeY = (screenY - screenY / 10) / square.length;
        if (sizeY < sizeX) size = sizeY;
        else size = sizeX;
        float startY = (screenY - size * square.length) / 2;
        float startX = (screenX / 2 - size * square[0].length) / 2;
        //ну, а здесь, просто, чтение карты и создание по ней уровня
        for (int y = 0; y < square.length; y++) {
            for (int x = 0; x < square[0].length; x++) {
                switch (level_key[y][x]) {
                    case (1):
                        square[y][x] = new Square_lava(this, (size * x) + startX, (size * y) + startY, size);
                        break;
                    case (2):
                        square[y][x] = new Square_kislota(this, (size * x) + startX, (size * y) + startY, size);
                        break;
                    case (3):
                        square[y][x] = new Square_empty(this, (size * x) + startX, (size * y) + startY, size, 3);
                        int[] food = {y, x};
                        foodSquares.add(food);
                        break;
                    case (4):
                        square[y][x] = new Square_empty(this, (size * x) + startX, (size * y) + startY, size, 4);
                        int blinky4[] = {y, x};
                        groupFinder(blinky4, 4);
                        break;
                    case (5):
                        square[y][x] = new Square_empty(this, (size * x) + startX, (size * y) + startY, size, 5);
                        int blinky5[] = {y, x};
                        groupFinder(blinky5, 5);
                        break;
                    case (6):
                        square[y][x] = new Square_empty(this, (size * x) + startX, (size * y) + startY, size, 6);
                        int blinky6[] = {y, x};
                        groupFinder(blinky6, 6);
                        break;
                    case (7):
                        square[y][x] = new Square_empty(this, (size * x) + startX, (size * y) + startY, size, 7);
                        int blinky7[] = {y, x};
                        groupFinder(blinky7, 7);
                        break;
                    default:
                        square[y][x] = new Square_empty(this, (size * x) + startX, (size * y) + startY, size, 0);
                        //на ноль или другое
                        break;
                }
            }
        }
        if (EndX != -1 && EndY != -1) {
            square[EndY][EndX].setTarget();
        }
    }

    void groupFinder(int[] blinky, int id) {
        int x = 0, y = 0;
        if (blinkySquares.size() != 0) for (int i = 0; i < blinkySquares.size(); i++) {
            y = blinkySquares.get(i).get(0)[0];//получение координат уже существующей клетки в одной из групп
            x = blinkySquares.get(i).get(0)[1];
            if (square[y][x].ID_NUMBER == id) {
                blinkySquares.get(i).add(blinky);
                break;
            }
            y = 0;
            x = 0;
        }
        if (x == 0 && y == 0) createBlinkyGroup(blinky);
    }

    void createBlinkyGroup(int[] square) {
        ArrayList<int[]> group = new ArrayList<>();
        group.add(square);
        blinkySquares.add(group);
    }

    void restartBLINKY() {
        for (int i = 0; i < blinkySquares.size(); i++) {
            ArrayList<int[]> group = blinkySquares.get(i);
            for (int j = 0; j < group.size(); j++) {
                int y = group.get(j)[0];
                int x = group.get(j)[1];
                square[y][x].blinkyVISIBLE();
            }
        }
    }

    void setVISIBLEGROUP(int id) {
        for (int i = 0; i < blinkySquares.size(); i++) {
            ArrayList<int[]> group = blinkySquares.get(i);
            for (int j = 0; j < group.size(); j++) {
                int y = group.get(j)[0];
                int x = group.get(j)[1];
                if (i == id) square[y][x].VISIBLE();
                else square[y][x].INVISIBLE();
            }
        }
    }

    //кнопки
    public void Move(View view) {
        if (!NOTshowPopUp) showPopupMenu(view, false);
        else Utils.AlertDialog(this, getString(R.string.task) + LEVEL_NUM,
                getString(R.string.try_to),
                getString(R.string.ok));
    }

    public void Operators(View view) {
        if (!NOTshowPopUp) showPopupMenu(view, false);
        else Utils.AlertDialog(this, getString(R.string.task) + LEVEL_NUM,
                getString(R.string.try_to),
                getString(R.string.ok));
    }

    //НАЧАЛО ВСЕГО!!!
    public void onClickStart(View view) {
        if (!move) {
            if (blinkySquares.size() != 0)
                setVISIBLEGROUP((int) (Math.random() * blinkySquares.size()));
            //при туториале возвращение в стартовые только мешает
            if (RestartRobotXY) {
                robot.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX, false);//ПЕРЕДВИЖЕНИЕ В "СТАРТОВЫЕ"
                kodParser.x = StartX;
                kodParser.y = StartY;
                for (int i = 0; i < foodSquares.size(); i++) {
                    int food[] = foodSquares.get(i);
                    if (square[food[0]][food[1]].food != null)
                        square[food[0]][food[1]].Restart();
                }
            }
            String text = editText.getText().toString();
            //а вот здесь самое интересное)
            action = kodParser.kodParser(text);
            move = true;
        }
    }

    public void onClickRef(View view) {
        String text = editText.getText().toString();
        if (!text.isEmpty()) {
            editText.setText(reformatKOD(text));
            Utils.AlertDialog(this, "...", getString(R.string.re), getString(R.string.ok));
        } else Utils.AlertDialog(this, "...", getString(R.string.no_text), getString(R.string.ok));
    }

    public void onClickHTP(View view) {
        if (Tutorial.isTutor) {
            Tutorial.stepNum--;
            Tutorial.task = false;
        } else {
            Intent intent = new Intent(this, ActivityHowToPlay.class);
            startActivity(intent);
        }
    }


    //действия, запускаемые синхронно с жизненным циклом активнсти
    @Override
    protected void onPause() {
        super.onPause();
        pause = true;
        Tutorial.ZEROING();
    }

    @Override
    protected void onResume() {
        super.onPause();
        singleplayer = ActivitySinglePlayer.this;
        pause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        kodParser.ZEROING();
        move = false;
    }


    public void update() {
        //начало выполнения программы
        if (move) {//включается при нажатии ПУСК
            Handler();
        } else {
            robot.MoveMySelf(false);
        }
        //конец списка команд
        if (count >= action && move) {//конец движения
            restartBLINKY();
            move = false;
            kodParser.action = 0;
            count = 0;

            if (AlertDialogMessage != null) {
                Utils.AlertDialog(this, getString(R.string.cant), AlertDialogMessage, getString(R.string.ok));
                editText.setSelection(kodParser.start, kodParser.stop);
            }
            //по прохождению уровня...
            else if (checkTask())
                if (!getIntent().getBooleanExtra("own_level", false))
                    Utils.TwoButtonAllertDialog(this, getString(R.string.level) + " " + level_name + getString(R.string.compl2),
                            onComplete,
                            getString(R.string.menu), getString(R.string.next), LEVEL_NUM);
                else
                    Utils.AlertDialog(this, getString(R.string.level) + " " + level_name + getString(R.string.compl2),
                            onComplete,
                            getString(R.string.ok));
            else if (!Tutorial.task) {
                Utils.makeToast(this, getString(R.string.try_more));
            } else Utils.makeToast(this, toast);//отчет об выполении

        }

    }

    void Handler() {
        //если в коде ошибка
        if (AlertDialogMessage != null && kodParser.isKodERROR()) {
            Utils.AlertDialog(this, getString(R.string.error_code), AlertDialogMessage, getString(R.string.ok));
            editText.setSelection(kodParser.start, kodParser.stop);
            move = false;
            count = 0;
            action = 0;
            kodParser.setAction(0);
        } else if (action != 0) {
            //вот здесь и запускается то, что видет пользователь
            if (kodParser.Anim[count] != 0) {
                if (kodParser.Anim[count] == 5) for (int i = 0; i < foodSquares.size(); i++) {
                    int food[] = foodSquares.get(i);
                    if ((kodParser.ARy[count]) == food[0] && (kodParser.ARx[count]) == food[1] && !square[food[0]][food[1]].food.isEaten())
                        square[food[0]][food[1]].EAT();
                }
                else robot.SearchAnim(kodParser.Anim[count]);
                kodParser.Anim[count] = 0;
            }
            robot.RobotMove(
                    square[(kodParser.ARy[count])][(kodParser.ARx[count])].y,
                    square[(kodParser.ARy[count])][(kodParser.ARx[count])].x,
                    kodParser.ARy[count], kodParser.ARx[count], false);//перемещение в клетку [sqY][sqX]
            count++;//перебор элементов массивов "положения" до action
        }
    }

    //проверка исполнения задания
    static boolean checkTask() {
        boolean complete = true;
        if (Tutorial.isTutor)
            complete = false;
        if (EndX != -1 && EndY != -1)
            if (kodParser.x != EndX || kodParser.y != EndY)
                complete = false;
        for (int i = 0; i < foodSquares.size(); i++) {
            int food[] = foodSquares.get(i);
            if (!square[food[0]][food[1]].food.isEaten())
                complete = false;
        }

        switch ((int) (Math.random() * 6)) {
            case 1:
                onComplete = singleplayer.getString(R.string.task_compl1);
                break;
            case 2:
                onComplete = singleplayer.getString(R.string.task_compl2);
                break;
            case 3:
                onComplete = singleplayer.getString(R.string.task_compl3);
                break;
            case 4:
                onComplete = singleplayer.getString(R.string.task_compl4);
                break;
            case 5:
                onComplete = singleplayer.getString(R.string.task_compl5);
                break;
            default:
                onComplete = singleplayer.getString(R.string.task_compl0);
                break;
        }

        return complete;
    }

    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, onTick);
        }

        @Override
        public void onTick(long millisIntilFinished) {
            if (!pause) update();
        }

        @Override
        public void onFinish() {
        }
    }

    //огромный метод для обработки всех раскрывающихся окон
    void showPopupMenu(View v, boolean isLongClick) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        if (v.getId() == R.id.button_mov && !isLongClick)
            popupMenu.inflate(R.menu.popup_move_menu); // Для Android 4.0
        else if (v.getId() == R.id.button_mov && isLongClick)
            popupMenu.inflate(R.menu.popup_condition_menu);
        else if (v.getId() == R.id.button_oper)
            popupMenu.inflate(R.menu.popup_operators_menu);
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.eat:
                                editText.getText().insert(editText.getSelectionStart(), "eat ;\n");
                                return true;
                            case R.id.up:
                                editText.getText().insert(editText.getSelectionStart(), "up ;\n");
                                return true;
                            case R.id.down:
                                editText.getText().insert(editText.getSelectionStart(), "down ;\n");
                                return true;
                            case R.id.right:
                                editText.getText().insert(editText.getSelectionStart(), "right ;\n");
                                return true;
                            case R.id.left:
                                editText.getText().insert(editText.getSelectionStart(), "left ;\n");
                                return true;
                            case R.id.repeat:
                                editText.getText().insert(editText.getSelectionStart(), "repeat (2){\n \n};\n");
                                return true;
                            case R.id.my_if:
                                editText.getText().insert(editText.getSelectionStart(), "if ( ){\n \n};\n");
                                return true;
                            case R.id.my_else:
                                editText.getText().insert(editText.getSelectionStart(), "if ( ){\n \n}else {\n \n};\n");
                                return true;
                            case R.id.my_while:
                                editText.getText().insert(editText.getSelectionStart(), "while ( ){\n \n};\n");
                                return true;
                            case R.id.con_not:
                                editText.getText().insert(editText.getSelectionStart(), "not_");
                                return true;
                            case R.id.con_up:
                                editText.getText().insert(editText.getSelectionStart(), "up_");
                                return true;
                            case R.id.con_down:
                                editText.getText().insert(editText.getSelectionStart(), "down_");
                                return true;
                            case R.id.con_right:
                                editText.getText().insert(editText.getSelectionStart(), "right_");
                                return true;
                            case R.id.con_left:
                                editText.getText().insert(editText.getSelectionStart(), "left_");
                                return true;
                            case R.id.con_wall:
                                editText.getText().insert(editText.getSelectionStart(), "wall");
                                return true;
                            case R.id.con_sweet:
                                editText.getText().insert(editText.getSelectionStart(), "sweet");
                                return true;
                            default:
                                return false;
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
            Tutorial.ZEROING();
            this.startActivity(intent);
            this.finish();
            super.onBackPressed();
        } else
            Utils.makeToast(this, getString(R.string.exit));
        back_pressed = System.currentTimeMillis();
        foodSquares.clear();
        blinkySquares.clear();
    }

    //для красивого кода
    String reformatKOD(String text) {
        text = text.replaceAll("\n", "");
        text = text.replaceAll(" ", "");
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ';' || text.charAt(i) == '{') {
                if (text.charAt(i) == '{')
                    text = text.substring(0, i + 1) + tapBody(text.substring(i + 1, text.length()));
                text = text.substring(0, i + 1) + "\n" + text.substring(i + 1, text.length());
            }
        }
        return text;
    }

    //это метод, по суте, зеркало моего любимого idetifyBODY из парсера
    //он выделят тело цикла тапами
    String tapBody(String text) {
        boolean enLOOP_EXIST = false;
        String[] line = text.split(";");
        text = "";
        for (int j = 0; j < line.length; j++) {
            if (line[j].contains("}") && !enLOOP_EXIST) {
                text += line[j] + ";";
                for (int k = ++j; k < line.length; k++) {
                    text += line[k] + ";";
                }
                break;
            }
            if (line[j].contains("{")) {
                enLOOP_EXIST = true;
                String comnd = line[j].substring(line[j].indexOf("{") + 1, line[j].length());
                text += "    " + line[j].substring(0, line[j].indexOf("{")) + "{    " + comnd + ";";
            } else if (line[j].contains("}") && enLOOP_EXIST) {
                enLOOP_EXIST = false;
                text += "    " + line[j] + ";";
            } else text += "    " + line[j] + ";";
        }
        return text;
    }
}
