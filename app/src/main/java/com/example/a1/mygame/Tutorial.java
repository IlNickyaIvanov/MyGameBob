package com.example.a1.mygame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.CountDownTimer;
import android.os.ParcelFileDescriptor;
import android.text.method.SingleLineTransformationMethod;
import android.widget.SimpleCursorAdapter;

import java.io.IOException;
import java.io.InputStream;

class Tutorial {
    private static final String APP_PREFERENCES = "mytutor";
    private static  String APP_PREFERENCES_COMPL = "comlpete1";
    private static  String file_name="tutor1";
    private SharedPreferences mTutor;
    Activity activity;
    int level;

    private boolean choose;
    static boolean tutorCOMPL=false;
    static boolean task;
    private int targetX=-1,targetY=-1;
    String steps[];

    int stepNum;


    Tutorial(int level,Activity activity){
        this.activity=activity;
        this.level=level;
        switch (level){
            case 1:
                APP_PREFERENCES_COMPL = "complete1";
                file_name="tutor1";
                idetifyTutor();
                break;
            case 2:
                APP_PREFERENCES_COMPL = "complete2";
                file_name="tutor2";
                idetifyTutor();
                break;
            default:break;
        }
     }

    void idetifyTutor(){
        ActivitySinglePlayer.RestartRobotXY = false;
        MyTimer timer = new MyTimer();
        timer.start();
        mTutor = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(!mTutor.contains(APP_PREFERENCES_COMPL)) {
            SharedPreferences.Editor editor = mTutor.edit();
            editor.putBoolean(APP_PREFERENCES_COMPL, false);
            tutorCOMPL=false;
            editor.apply();
        }
        if (!mTutor.getBoolean(APP_PREFERENCES_COMPL, true)) {
            startTutor();
        }
        else {
            Utils.TwoButtonAllertDialog(activity, "Обучение", "Вы уже прошли это обучение. Хотите повторить?", "нет", "да", 0);
            choose=true;
        }
    }

     String getStringFromAssetFile(Activity activity,String filename)
     {
         String text = filename;
         byte[] buffer = null;
         InputStream is;
         try {
             is = activity.getAssets().open(text);
             int size = is.available();
             buffer = new byte[size];
             is.read(buffer);
             is.close();
         } catch (IOException e) {
             e.printStackTrace();
         }

         String str_data = new String(buffer);
         return str_data;
     }

    private void update() {
        if(mTutor.getBoolean(APP_PREFERENCES_COMPL, true) && !Utils.isAlertDialogVisible() && Utils.isADPositBut()&&choose){
            choose=false;
            startTutor();
        }
        if (steps!=null && !mTutor.getBoolean(APP_PREFERENCES_COMPL, true)){
            if (task)checkComplTask();
            if (stepNum==steps.length && !Utils.isAlertDialogVisible() && !task){
                stepNum=0;
                steps=null;
                SharedPreferences.Editor editor = mTutor.edit();
                editor.putBoolean(APP_PREFERENCES_COMPL, true);
                editor.apply();
                tutorCOMPL=true;
                Utils.TwoButtonAllertDialog(activity,"Обучение "+level+" пройдено!",
                        "Хотите перейти к следующему уроку?","НЕТ", "ДА",level);
                ActivitySinglePlayer.RestartRobotXY = true;
            }else if (!Utils.isAlertDialogVisible() && !task) {
                Utils.AlertDialog(activity, "обучение "+level, setTask(steps[stepNum]), "ок");
                stepNum++;
            }
        }
    }

    String setTask(String step){
        String message;
        if (step.contains("<") && step.contains(">")){
            message = step.substring(step.indexOf("n")+1,step.indexOf("<"));
            targetX = Integer.parseInt(step.substring(step.indexOf("<")+1,step.indexOf("|")));
            targetY = Integer.parseInt(step.substring(step.indexOf("|")+1,step.indexOf(">")));
            ActivitySinglePlayer.square[targetY][targetX].setTarget();
            task=true;
        }else if (step.contains("[") && step.contains("]")){
            message = step.substring(step.indexOf("\n")+1,step.indexOf("["));
            int foodCount=0;
            for (int i=0;i<step.length();i++){
                if (step.charAt(i) == '[')foodCount++;
            }
            for (int j=0;j<foodCount;j++) {
                int x = Integer.parseInt(step.substring(step.indexOf("[") + 1, step.indexOf("|")));
                int y = Integer.parseInt(step.substring(step.indexOf("|") + 1, step.indexOf("]")));
                step=step.substring(step.indexOf("]")+1,step.length());
                ActivitySinglePlayer.square[y][x].setFood();
                int food[] = {y, x};
                ActivitySinglePlayer.foodSquares.add(food);
            }
            task=true;
        } else {
            task=false;
            message = step.substring(step.indexOf("n")+1,step.length());
        }
        return message;
    }
    boolean checkComplTask(){
        if (targetX == ActivitySinglePlayer.robot.sqX && targetY==ActivitySinglePlayer.robot.sqY) {
                ActivitySinglePlayer.square[targetY][targetX].clearTarget();
                task = false; targetY=-1; targetX=-1;
        }
       else if (ActivitySinglePlayer.foodSquares.size()!=0){
            task = false;
            for (int i = 0; i < ActivitySinglePlayer.foodSquares.size(); i++) {
                int food[] = ActivitySinglePlayer.foodSquares.get(i);
                if (!ActivitySinglePlayer.square[food[0]][food[1]].food.isEaten())
                    task = true;
            }
            if (!task) ActivitySinglePlayer.foodSquares.clear();
        }
        return task;
    }
    void startTutor(){
        SharedPreferences.Editor editor = mTutor.edit();
        editor.putBoolean(APP_PREFERENCES_COMPL, false);
        editor.apply();
        tutorCOMPL=false;
        steps=getStringFromAssetFile(activity, file_name).split("#");
    }
    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, 100); // продолжительность работы таймера в милисекундах, интервал срабатывания
            }
        @Override
        public void onTick(long millisUntilFinished) {
             update(); // вызываем метод, в котором происходит обновление игры
             }
        @Override
        public void onFinish() {
            }
    }
}
