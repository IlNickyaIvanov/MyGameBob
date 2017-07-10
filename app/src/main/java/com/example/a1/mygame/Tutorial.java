package com.example.a1.mygame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import java.io.IOException;
import java.io.InputStream;

//это класс, использующийся в первых трех уровнях - обучения
class Tutorial {
    private static final String APP_PREFERENCES = "mytutor";
    private static String APP_PREFERENCES_COMPL = "comlpete1";
    private static String file_name = "tutor1";
    private SharedPreferences mTutor;
    static Activity activity;
    private static int level;

    private boolean choose;
    static boolean isTutor = false;
    static boolean task = false;
    private int targetX = -1, targetY = -1;
    private static String steps[];
    static int stepNum;

    Tutorial(int level, Activity activity) {
        ZEROING();
        Tutorial.level = 0;
        if (Tutorial.level == 0) {
            Tutorial.level = level;
        }
        Tutorial.activity = activity;
        boolean ruloc = activity.getResources().getConfiguration().locale.toString().equals("ru_RU");
        switch (level) {
            case 1:
                APP_PREFERENCES_COMPL = "complete1";
                file_name = (ruloc) ? "tutor1ru.txt" : "tutor1";
                idetifyTutor();
                break;
            case 2:
                APP_PREFERENCES_COMPL = "complete2";
                file_name = (ruloc) ? "tutor2ru.txt" : "tutor2";
                idetifyTutor();
                break;
            case 3:
                APP_PREFERENCES_COMPL = "complete3";
                file_name = (ruloc) ? "tutor3ru.txt" : "tutor3";
                idetifyTutor();
                break;
            default:
                break;
        }
    }

    private void idetifyTutor() {
        MyTimer timer = new MyTimer();
        timer.start();
        mTutor = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (!mTutor.contains(APP_PREFERENCES_COMPL)) {
            setComplete(false);
        }
        if (!mTutor.getBoolean(APP_PREFERENCES_COMPL, true)) {
            startTutor();
        } else {
            Utils.TwoButtonAllertDialog(activity, activity.getString(R.string.tutorial) + " " + level, activity.getString(R.string.done_tutor),
                    activity.getString(R.string.no), activity.getString(R.string.yes), 0);
            choose = true;
        }
    }

    private void startTutor() {
        ActivitySinglePlayer.RestartRobotXY = false;//во время туториала "Возвращение в стартовы" не нужно
        isTutor = true;
        setComplete(false);
        String text = getStringFromAssetFile(activity, file_name);
        steps = text.split("#");
    }

    private void update() {
        if (choose && Utils.isADPositBut()) {
            choose = false;
            Utils.setADPositBut(false);
            startTutor();
        }
        if (steps != null && isTutor) {
            if (task) checkComplTask();
            if (stepNum == steps.length && !task)//конец туториала
                onTutorComplete();
            else if (!task) {//следующее сообщение
                Utils.AlertDialog(activity, activity.getString(R.string.tutorial) + " " + level + "." + stepNum, setTask(steps[stepNum]), "оK");
                stepNum++;
            }
        }
    }


    private void onTutorComplete() {
        stepNum = 0;
        steps = null;
        setComplete(true);
        if (level != 3)
            Utils.TwoButtonAllertDialog(activity, activity.getString(R.string.tutorial) + " " + level + activity.getString(R.string.complete),
                    activity.getString(R.string.next_tutor), activity.getString(R.string.no), activity.getString(R.string.yes), level);
        else
            Utils.TwoButtonAllertDialog(activity, activity.getString(R.string.tutorial) + " " + level + activity.getString(R.string.complete),
                    activity.getString(R.string.all_tutor), activity.getString(R.string.no), activity.getString(R.string.yes), level);
    }

    private String setTask(String step) {
        String message;
        if (step.contains("<") && step.contains(">")) {
            message = step.substring(0, step.indexOf("<"));
            targetX = Integer.parseInt(step.substring(step.indexOf("<") + 1, step.indexOf("|")));
            targetY = Integer.parseInt(step.substring(step.indexOf("|") + 1, step.indexOf(">")));
            ActivitySinglePlayer.square[targetY][targetX].setTarget();
            task = true;
        } else if (step.contains("[") && step.contains("]")) {
            message = step.substring(0, step.indexOf("["));
            int foodCount = 0;
            for (int i = 0; i < step.length(); i++) {
                if (step.charAt(i) == '[') foodCount++;
            }
            for (int j = 0; j < foodCount; j++) {
                int x = Integer.parseInt(step.substring(step.indexOf("[") + 1, step.indexOf("|")));
                int y = Integer.parseInt(step.substring(step.indexOf("|") + 1, step.indexOf("]")));
                step = step.substring(step.indexOf("]") + 1, step.length());
                ActivitySinglePlayer.square[y][x].setFood();
                int food[] = {y, x};
                ActivitySinglePlayer.foodSquares.add(food);
            }
            task = true;
        } else {
            task = false;
            message = step.substring(0, step.length());
        }
        return message;
    }

    private boolean checkComplTask() {
        if (targetX == ActivitySinglePlayer.robot.sqX && targetY == ActivitySinglePlayer.robot.sqY) {
            ActivitySinglePlayer.square[targetY][targetX].clearTarget();
            task = false;
            targetY = -1;
            targetX = -1;
        }
        if (ActivitySinglePlayer.foodSquares.size() != 0) {
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

    private String getStringFromAssetFile(Activity activity, String filename) {
        byte[] buffer = null;
        InputStream is;
        try {
            is = activity.getAssets().open(filename);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert buffer != null;
        return new String(buffer);
    }

    private class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, 100); // продолжительность работы таймера в милисекундах, интервал срабатывания
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (!Utils.isADVisible())
                update();// вызываем метод, в котором происходит обновление игры
        }

        @Override
        public void onFinish() {
        }
    }

    static void ZEROING() {
        steps = null;
        stepNum = 0;
        task = false;
        isTutor = false;
        ActivitySinglePlayer.RestartRobotXY = true;
    }

    private void setComplete(boolean complete) {
        SharedPreferences.Editor editor = mTutor.edit();
        editor.putBoolean(APP_PREFERENCES_COMPL, complete);
        editor.apply();
    }
}
