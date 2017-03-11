package com.example.a1.mygame;

import android.app.Activity;
import android.os.CountDownTimer;

 class Tutorial {
     static boolean IsTutorial;
    private int level;
    private Activity activity;
    private boolean pause;
    private static boolean isTask;
    private int task;
    private int MessageCount;
    private String tutor[];
    private String tutor1[]={
            "Добро Пожаловать в игру 'Боб'!",
            "Цель этой игры - обучать основам\nалгоритмического мышления...",
            "Давай  приступим к первому заданию!",
            "Попробуй переместить Боба в конец корридора\nДля этого используй кнопку в правом верхнем углу...",
            "Здорово!",
            "А теперь попробуй написать команды самостоятельно..." +
                    "\nПеремести Боба назад, в начало коридора\nup;   down;\nright;   left;",
            "Молодец!"
    };
    private String tutor2[]={
            "Видишь эти клетки с лавой?",
            "Навярняка ничего хорошего не будет,если на них наступить...",
            "Попробуй провести Боба через весь лабиринт, не трогая на лавы.",
            "Чтобы было легче выполнять это задание используй скобки после команд:\n" +
                    "down (3); - три шага вниз\n" +
                    "В скобках указывается число - колличесвто щагов."
    };
    Tutorial (int level, Activity activity){
        this.activity=activity;
        this.level=level;
        switch (level){
            case 1: tutor=tutor1;
                break;
            case 2:tutor=tutor2;
                break;
            default: pause=true; break;
        }
        Tutorial.MyTimer timer = new Tutorial.MyTimer();
        timer.start();
    }
    private void update(){
        LevelTasker(level);
        if (!isTask && !Utils.isAlertDialogVisible()) {
            if (MessageCount<tutor.length) {
                Utils.AlertDialog(activity, "Урок "+level, tutor[MessageCount], "ок");
                MessageCount++; IsTutorial=true;
            }
            else {Utils.TwoButtonAllertDialog(activity,"Урок "+level+" завершен!",
                    "Поздравляем, Вы прошли этот урок!","Выбрать уровень","Следующий уровень",level);
                pause = true;IsTutorial=false;}
        }

    }
    private void LevelTasker(int i){
        if (MessageCount==4 && i==1){
            if(!isTask){isTask=true;}
            if (MyGame.robot.sqX==2 && MyGame.robot.sqY==0)isTask=false;
        }
        if (MessageCount==6 && i==1){
            if(!isTask){isTask=true;MyGame.NOTshowPopUp =true;}
            if (MyGame.robot.sqX==0 && MyGame.robot.sqY==0) {
                isTask = false;MyGame.NOTshowPopUp =false;
            }
        }
        if (MessageCount==3 && i == 2){
            if(!isTask){isTask=true;}
            if (MyGame.robot.sqX==5 && (MyGame.robot.sqY==1 || MyGame.robot.sqY==2))isTask=false;
        }
    }
    private class MyTimer extends CountDownTimer {
        MyTimer(){
            super(Integer.MAX_VALUE, 5);
        }
        public void onTick(long millisUntilFinished) {if(!pause)update();}
        public void onFinish(){
        }
    }
    static boolean isTask() {
        return isTask;
    }
}
