package com.example.a1.mygame;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a1.mygame.fragments.FragmentEdit1;
import com.example.a1.mygame.fragments.FragmentEdit2;

import java.util.Arrays;

import mehdi.sakout.fancybuttons.FancyButton;

public class ActivityTwoPlayers extends ActivitySinglePlayer {

    private FragmentEdit1 oneFragment;
    private FragmentEdit2 twoFragment;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private final int onTick2P = 1000;//У КАЖДГО РЕЖИМА СВОЙ onTick!

    private Robot player1, player2;
    static KodParser First_kodParser;
    static KodParser Second_kodParser;

    FancyButton button;

    static int count1, count2, action1, action2;
    static boolean move, pause;
    private String text1 = "", text2 = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_players);
        ActivityLevelMenu.TwoPlayers = false;

        manager = getSupportFragmentManager();

        oneFragment = new FragmentEdit1();
        twoFragment = new FragmentEdit2();
        transaction = manager.beginTransaction();
        transaction.add(R.id.container, oneFragment, FragmentEdit1.TAG);
        transaction.commit();

        button = (FancyButton) findViewById(R.id.button_mov);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopupMenu(view,true);
                return false;
            }
        });


        MyTimer2 timer = new MyTimer2();
        timer.start();

        LEVEL_GETTER();
        player1 = new Robot(this, square[0][0].x, square[0][0].y, size, screenY, onTick2P, 1, size / 4);
        player2 = new Robot(this, square[0][0].x, square[0][0].y, size, screenY, onTick2P, 2, -size / 4);
        player1.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX, false);
        player2.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX, false);

        First_kodParser = new KodParser(StartX, StartY, square,ComandLimit);
        Second_kodParser = new KodParser(StartX, StartY, square,ComandLimit);
    }

    @Override
    public void onClickStart(View view) {
        if (!move) {
            RESTART();
            START_PARSER(3);
            move = true;
//            //if(!text.isEmpty())editText.setText(reformatKOD(text));
        }
    }
    public void onSwitch(View view) {
        transaction = manager.beginTransaction();
        switch (view.getId()) {
            case (R.id.button_sw):
                try {
                    if (move) break;
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    if (manager.findFragmentByTag(FragmentEdit1.TAG) != null) {
                        if (START_PARSER(1))
                            transaction.replace(R.id.container, twoFragment, FragmentEdit2.TAG);
                    } else if (manager.findFragmentByTag(FragmentEdit2.TAG) != null) {
                        if (START_PARSER(2))
                            transaction.replace(R.id.container, oneFragment, FragmentEdit1.TAG);
                    }
                } catch (Throwable t) {
                    break;
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    public void update2P() {
        if (move) {
            //если в коде ошибка
            if (AlertDialogMessage != null && (First_kodParser.isKodERROR() || Second_kodParser.isKodERROR())) {
                Utils.AlertDialog(this, "Ошибка в коде...", AlertDialogMessage, "ок");
                editText.setSelection(kodParser.start, kodParser.stop);
                ZEROING();
                move = false;
                if (First_kodParser.isKodERROR()) {
                    if (manager.findFragmentByTag(FragmentEdit2.TAG) != null) {
                        transaction.replace(R.id.container, oneFragment, FragmentEdit1.TAG);
                    }
                    oneFragment.SELECTION(First_kodParser.start, First_kodParser.stop);
                }
                if (Second_kodParser.isKodERROR()) {
                    if (manager.findFragmentByTag(FragmentEdit1.TAG) != null) {
                        transaction.replace(R.id.container, twoFragment, FragmentEdit2.TAG);
                    }
                    twoFragment.SELECTION(Second_kodParser.start, Second_kodParser.stop);
                }


                //если ошибок нет
            } else if (action1 != 0 || action2 != 0) {
                //textView.setText("  робот 1 шагает " + First_kodParser.Anim[count1]);
                if (count1 != action1) {
                    MOTION(First_kodParser, player1, count1);
                    count1++;
                }
                if (count2 != action2) {
                    MOTION(Second_kodParser, player2, count2);
                    count2++;
                }
            }


            //ожидание команд
        } else if (!move) {
            //textView.setText("роботы стоят " + First_kodParser.y + " " + First_kodParser.x);
            MOTION_MYSELF(player1, count1);
            MOTION_MYSELF(player2, count2);
        }


        //конец списка команд
        if (count1 == action1 && count2 == action2 && move) {
            move = false;
            ZEROING();
            if (AlertDialogMessage != null && (First_kodParser.isPause() || Second_kodParser.isPause())) {
                Utils.AlertDialog(this, "Дальше не могу.", AlertDialogMessage, "ок");
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    private boolean START_PARSER(int numParser) {
        if (numParser == 1 || numParser == 3) {
            EditText edit1 = (EditText) findViewById(R.id.editText1);
            String text1 = "";
            if (edit1 != null) {
                text1 = edit1.getText().toString();
                this.text1 = text1;
            }
            //запуск и задание конечного сетчика первого игрока
            //RESTART();
            if (numParser == 3)
                action1 = First_kodParser.kodParser(this.text1);
            else if (!text1.isEmpty()) {
                action1 = First_kodParser.kodParser(text1);
                this.text1 = text1;
            } else this.text1 = "";
            if (First_kodParser.isKodERROR()) {
                move = true;
                return false;
            } else ZEROING();
        }
        if (numParser == 2 || numParser == 3) {
            EditText edit2 = (EditText) findViewById(R.id.editText2);
            String text2 = "";
            if (edit2 != null) {
                text2 = edit2.getText().toString();
                this.text2 = text2;
            }
            //запуск и задание конечного сетчика второго игрока
            //RESTART();
            if (numParser == 3)
                action2 = Second_kodParser.kodParser(this.text2);
            else if (!text2.isEmpty()) {
                action2 = Second_kodParser.kodParser(text2);
                this.text2 = text2;
            } else this.text2 = "";
            if (Second_kodParser.isKodERROR() && !text2.isEmpty()) {
                move = true;
                return false;
            } else ZEROING();
        }
        return true;
    }

    private boolean CHECK_PLYRS(int count) {

        int NEXT1player[] = {First_kodParser.ARy[count], First_kodParser.ARx[count]};
        int NEXT2player[] = {Second_kodParser.ARy[count], Second_kodParser.ARx[count]};

        int NOW1player[] = {player1.sqY, player1.sqX};
        int NOW2player[] = {player2.sqY, player2.sqX};
        if (Arrays.equals(NEXT1player, NEXT2player)
                || Arrays.equals(NEXT1player, NOW2player)
                || Arrays.equals(NEXT2player, NOW1player))
        return true;
        else if (Arrays.equals(NOW1player, NOW2player) && !move)
            return true;
        else return false;
    }

    private void MOTION_MYSELF(Robot robot, int count) {
        robot.MoveMySelf(CHECK_PLYRS(count));
    }

    private void MOTION(KodParser kodParser, Robot robot, int count) {
        robot.RobotMove(
                square[(kodParser.ARy[count])][(kodParser.ARx[count])].y,
                square[(kodParser.ARy[count])][(kodParser.ARx[count])].x,
                kodParser.ARy[count], kodParser.ARx[count],CHECK_PLYRS(count));
    }

    void RESTART() {
        player1.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX, true);
        player2.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX, true);
        First_kodParser.x = StartX;
        First_kodParser.y = StartY;
        Second_kodParser.x = StartX;
        Second_kodParser.y = StartY;
    }

    void ZEROING() {
        First_kodParser.action = 0;
        Second_kodParser.action = 0;
        count1 = 0;
        count2 = 0;
        First_kodParser.setKodERROR(false);
        Second_kodParser.setKodERROR(false);
    }

    @Override
    void showPopupMenu(View v,boolean isLongClick) {
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
                        switch (item.getItemId()) {
                            case R.id.up:
                                if (oneFragment.isVisible()) oneFragment.SetText("up ;\n");
                                else if (twoFragment.isVisible()) twoFragment.SetText("up ;\n");
                                return true;
                            case R.id.down:
                                if (oneFragment.isVisible()) oneFragment.SetText("down ;\n");
                                else if (twoFragment.isVisible()) twoFragment.SetText("down ;\n");
                                return true;
                            case R.id.right:
                                if (oneFragment.isVisible()) oneFragment.SetText("right ;\n");
                                else if (twoFragment.isVisible()) twoFragment.SetText("right ;\n");
                                return true;
                            case R.id.left:
                                if (oneFragment.isVisible()) oneFragment.SetText("left ;\n");
                                else if (twoFragment.isVisible()) twoFragment.SetText("left ;\n");
                                return true;
                            case R.id.repeat:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("repeat (2){\n \n};\n");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("repeat (2){\n \n};\n");
                                return true;
                            case R.id.my_if:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("if ( ){\n \n};\n");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("if ( ){\n \n};\n");
                             return true;
                            case R.id.my_else:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("if ( ){\n \n}else {\n \n};\n");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("if ( ){\n \n}else {\n \n};\n");
                                return true;
                            case R.id.my_while:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("while ( ){\n \n};\n");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("while ( ){\n \n};\n");
                                return true;
                            case R.id.con_up:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("up_");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("up_");
                               return true;
                            case R.id.con_down:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("down_");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("down_");
                                return true;
                            case R.id.con_right:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("right_");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("right_");
                                return true;
                            case R.id.con_left:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("left_");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("left_");
                               return true;
                            case R.id.con_wall:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("wall");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("wall");
                               return true;
                            case R.id.con_sweet:
                                if (oneFragment.isVisible())
                                    oneFragment.SetText("sweet");
                                else if (twoFragment.isVisible())
                                    twoFragment.SetText("sweet");
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

    class MyTimer2 extends CountDownTimer {
        MyTimer2() {
            super(Integer.MAX_VALUE, onTick2P);
        }

        @Override
        public void onTick(long millisIntilFinished) {
            if (!pause)
                update2P();
        }

        @Override
        public void onFinish() {
        }
    }
}
