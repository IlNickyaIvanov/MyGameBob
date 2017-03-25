package com.example.a1.mygame;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a1.mygame.fragments.FragmentEdit1;
import com.example.a1.mygame.fragments.FragmentEdit2;

public class ActivityTwoPlayers extends ActivitySinglePlayer {

    private FragmentEdit1 oneFragment;
    private FragmentEdit2 twoFragment;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    static int onTick = 1000;//У КАЖДГО РЕЖИМА СВОЙ onTick!
    static Robot player1, player2;
    static KodParser First_kodParser;
    static KodParser Second_kodParser;

    TextView textView;

    static int count1,count2, action1,action2;
    private String text1="",text2="";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_players);

        textView = (TextView) findViewById(R.id.TextView1);
        ActivityLevelMenu.TwoPlayers = false;

        manager = getSupportFragmentManager();

        oneFragment = new FragmentEdit1();
        twoFragment = new FragmentEdit2();
        transaction = manager.beginTransaction();
        transaction.add(R.id.container, oneFragment, FragmentEdit1.TAG);
        transaction.commit();

        MyTimer2 timer = new MyTimer2();
        timer.start();

        LEVEL_GETTER();
        player1 = new Robot(this, square[0][0].x, square[0][0].y, size, screenY,onTick,1);
        player2 = new Robot(this, square[0][0].x, square[0][0].y, size, screenY,onTick,2);
        player1.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX,size/4);
        player2.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX,-size/4);

        First_kodParser = new KodParser(StartX, StartY, square);
        Second_kodParser = new KodParser(StartX, StartY, square);
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
            case (R.id.buttonSW):
                try {
                    if (move)break;
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    if (manager.findFragmentByTag(FragmentEdit1.TAG) != null) {
                        if (START_PARSER(1))
                            transaction.replace(R.id.container, twoFragment, FragmentEdit2.TAG);
                    }
                    else if (manager.findFragmentByTag(FragmentEdit2.TAG) != null) {
                        if (START_PARSER(2))
                            transaction.replace(R.id.container, oneFragment, FragmentEdit1.TAG);
                    }
                }
                catch (Throwable t){break;}
                break;
            default:break;
        }
        transaction.commit();
    }

    @Override
    public void update() {
        if (move) {
            //если в коде ошибка
            if (AlertDialogMessage != null && (First_kodParser.isKodERROR()||Second_kodParser.isKodERROR())) {
                Utils.AlertDialog(this, "Ошибка в коде...", AlertDialogMessage, "ок");
                editText.setSelection(kodParser.start, kodParser.stop);
                ZEROING();
                move=false;
                if(First_kodParser.isKodERROR()) {
                    if (manager.findFragmentByTag(FragmentEdit2.TAG) != null) {
                        transaction.replace(R.id.container, oneFragment, FragmentEdit1.TAG);
                    }
                    oneFragment.SELECTION(First_kodParser.start,First_kodParser.stop);
                }
                if(Second_kodParser.isKodERROR()) {
                    if (manager.findFragmentByTag(FragmentEdit1.TAG) != null) {
                        transaction.replace(R.id.container, twoFragment, FragmentEdit2.TAG);
                    }
                    twoFragment.SELECTION(Second_kodParser.start,Second_kodParser.stop);
                }



                //если ошибок нет
            } else if (action1 != 0 || action2 !=0) {
                textView.setText("  робот 1 шагает " + First_kodParser.ComandName[count1]);
                if(count1!=action1 && player1.translateAnimation.hasEnded()){
                    MOTION(First_kodParser,player1,count1,size/4);
                    count1++;
                }
                if(count2!=action2 && player2.translateAnimation.hasEnded()){
                    MOTION(Second_kodParser, player2, count2, -size / 4);
                    count2++;
                }
                //перебор элементов массивов "положения" до action
            }


            //ожидание команд
        } else if (!move){
            textView.setText("роботы стоят " + First_kodParser.y + " " + First_kodParser.x);
                //player1.RobotMove(player1.y, player1.x, player1.sqY, player1.sqX);
                //player2.RobotMove(player2.y, player2.x, player2.sqY, player2.sqX);
            player1.MoveMySelf();
            player2.MoveMySelf();
        }


        //конец списка команд
        if (count1 == action1 && count2 == action2 && move) {
            move = false;
            ZEROING();
            if (AlertDialogMessage != null && (First_kodParser.isPause()||Second_kodParser.isPause())) {
                Utils.AlertDialog(this, "Дальше не могу.", AlertDialogMessage, "ок");
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    private boolean START_PARSER(int numParser){
        if (numParser==1 || numParser==3) {
            EditText edit1 = (EditText) findViewById(R.id.editText1);
            String text1="";
            if(edit1!=null) {
                text1 = edit1.getText().toString();
                this.text1=text1;
            }
            //запуск и задание конечного сетчика первого игрока
            RESTART();
            if (numParser==3)
                action1= First_kodParser.kodParser(this.text1);
            else if(!text1.isEmpty()) {
                action1 = First_kodParser.kodParser(text1);
                this.text1=text1;
            }
            else this.text1="";
            if (First_kodParser.isKodERROR()) {
                move=true;
                return false;
            }else  ZEROING();
        }
        if (numParser==2 || numParser==3) {
            EditText edit2 = (EditText) findViewById(R.id.editText2);
            String text2="";
            if(edit2!=null) {
                text2 = edit2.getText().toString();
                this.text2=text2;
            }
            //запуск и задание конечного сетчика второго игрока
            RESTART();
            if (numParser==3)
                action2= Second_kodParser.kodParser(this.text2);
            else if(!text2.isEmpty()){
                action2= Second_kodParser.kodParser(text2);
                this.text2=text2;
            }
            else this.text2="";
            if (Second_kodParser.isKodERROR()&&!text2.isEmpty()) {
                move=true;
                return false;
            }else ZEROING();
        }
        return true;
    }

    void MOTION(KodParser kodParser, Robot robot,int count,int Yset) {
        if (First_kodParser.ARy[count]==Second_kodParser.ARy[count] && First_kodParser.ARx[count]==Second_kodParser.ARx[count]
                || First_kodParser.ARy[count]==player2.sqY && First_kodParser.ARx[count]==player2.sqX
                || Second_kodParser.ARy[count]==player1.sqY && Second_kodParser.ARx[count]==player2.sqX){
            robot.RobotMove(
                    square[(kodParser.ARy[count])][(kodParser.ARx[count])].y,
                    square[(kodParser.ARy[count])][(kodParser.ARx[count])].x,
                    kodParser.ARy[count],kodParser.ARx[count],Yset);
        }
        else robot.RobotMove(
                square[(kodParser.ARy[count])][(kodParser.ARx[count])].y,
                square[(kodParser.ARy[count])][(kodParser.ARx[count])].x,
                kodParser.ARy[count],kodParser.ARx[count],0);

    }

    void RESTART (){
        player1.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX,size/4);
        player2.RobotMove(square[StartY][StartX].y, square[StartY][StartX].x, StartY, StartX,-size/4);
        First_kodParser.x = StartX;First_kodParser.y = StartY;
        Second_kodParser.x = StartX;Second_kodParser.y = StartY;
    }
    void ZEROING(){
        First_kodParser.action = 0;
        Second_kodParser.action = 0;
        count1 = 0;
        count2 = 0;
        First_kodParser.setKodERROR(false);
        Second_kodParser.setKodERROR(false);
    }
    @Override
    void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popup_menu); // Для Android 4.0
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.up:
                                if (oneFragment.isVisible())oneFragment.SetText("up ;\n");
                                else if (twoFragment.isVisible())twoFragment.SetText("up ;\n");
                                return true;
                            case R.id.down:
                                if (oneFragment.isVisible())oneFragment.SetText("down ;\n");
                                else if (twoFragment.isVisible())twoFragment.SetText("down ;\n");
                                return true;
                            case R.id.right:
                                if (oneFragment.isVisible())oneFragment.SetText("right ;\n");
                                else if (twoFragment.isVisible())twoFragment.SetText("right ;\n");
                                return true;
                            case R.id.left:
                                if (oneFragment.isVisible())oneFragment.SetText( "left ;\n");
                                else if (twoFragment.isVisible())twoFragment.SetText( "left ;\n");
                                return true;
                            case R.id.repeat:
                                if (oneFragment.isVisible())oneFragment.SetText(  "repeat (2){\n \n};\n");
                                else if (twoFragment.isVisible())twoFragment.SetText(  "repeat (2){\n \n};\n");
                                return true;
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
    class MyTimer2 extends CountDownTimer {
        MyTimer2() {
            super(Integer.MAX_VALUE, onTick*2);
        }
        @Override
        public void onTick(long millisIntilFinished){if(!pause)update();}
        @Override
        public void onFinish() {
        }
    }
}
