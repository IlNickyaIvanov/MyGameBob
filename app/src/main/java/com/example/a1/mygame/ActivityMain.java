package com.example.a1.mygame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

//это класс - основная актиность на ней есть разные кнопки, Боб и по истечению некоторого времени начинается мини игра "щенки"
public class ActivityMain extends Activity {
    private boolean pause;
    private int miniGameStart = 1000;//время в миллсек*10 до старта мини игры
    private final int MaxNGhost = 50;
    protected static int screenWidth, screenHeight;
    protected static int liveGhosts, diedGhosts, nGhosts;//количество мертвых, живых и всех щенков
    private static int count;//счетчик
    private static long back_pressed;
    ArrayList<MiniGameGhost> ghost = new ArrayList<>();
    TextView textViewVersion, nameGame;
    ImageView slime;
    LinearLayout upLayout;
    RelativeLayout downLayout;
    Robot robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        upLayout = (LinearLayout) findViewById(R.id.up_layout);
        downLayout = (RelativeLayout) findViewById(R.id.down_layout);
        //определение оринтации и создание Боба
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int size = screenHeight / 2;
            float x = (float) (screenWidth / 2 - size) / 2;
            float y = (float) (screenHeight - size) / 2;
            slime = new ImageView(this);
            this.addContentView(slime, new RelativeLayout.LayoutParams(size * 2, size * 2));
            slime.setX(x - size / 2);
            slime.setY(y - size / 2);
            slime.setImageResource(R.drawable.slime);
            robot = new Robot(this, x, y, size, 1000, 1, 0, 0, 0);
        }
        //получение данных о старте мини игры из Шаредов
        SharedPreferences mSettings;
        try {
            mSettings = getSharedPreferences(ActivitySettings.APP_PREFERENCES, Context.MODE_PRIVATE);
            if (mSettings.contains(ActivitySettings.APP_PREFERENCES_MINIGAME))
                miniGameStart = mSettings.getInt(ActivitySettings.APP_PREFERENCES_MINIGAME, 1000) * 100;
        } catch (Throwable ignore) {
        }

        nameGame = (TextView) findViewById(R.id.name_game);
        textViewVersion = (TextView) findViewById(R.id.textView2);

        nameGame.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ObelixPro.ttf"));
        textViewVersion.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ObelixPro.ttf"));
        MyTimer timer = new MyTimer();
        timer.start();
    }

    @Override
    protected void onPause() {
        //обнуление по паузе
        super.onPause();
        pause = true;
        nGhosts = 0;
        liveGhosts = 0;
        diedGhosts = 0;
        nGhosts = 0;
        ghost.clear();
    }

    @Override
    protected void onResume() {
        super.onPause();
        count = 0;
        pause = false;
    }


    public void onClickPlay(View view) {
        if (liveGhosts <= 0) {
            Intent intent = new Intent(ActivityMain.this, ActivityLevelMenu.class);
            startActivity(intent);
        }

    }

    public void OnClickHowToPlay(View view) {
        if (liveGhosts <= 0) {
            Intent intent = new Intent(ActivityMain.this, ActivityHowToPlay.class);
            startActivity(intent);
        }
    }

    public void onClickSettings(View view) {
        if (liveGhosts <= 0) {
            Intent intent = new Intent(ActivityMain.this, ActivitySettings.class);
            startActivity(intent);
        }
    }

    void update() {
        count++;//счетчик по достижению определенного значения запускается мини игра
        if (liveGhosts == 0) {
            textViewVersion.setText(getString(R.string.version));
        }
        if (count >= miniGameStart && liveGhosts < MaxNGhost && nGhosts < MaxNGhost) {
            miniGameGhost();
        }
        for (int i = 0; i < nGhosts; i++) {
            if (ghost.get(i).live) ghost.get(i).move();
            else diedGhosts++;
        }
    }

    //метод, отвечающий за контроль мини игры
    void miniGameGhost() {
        if (count == miniGameStart) {
            ghost.add(new MiniGameGhost(this));
            nGhosts++;
            liveGhosts++;
            Utils.AlertDialog(this, getString(R.string.attention),
                    getString(R.string.puppies), getString(R.string.pat));
            if (robot != null)
                robot.stopAnim();
            upLayout.setBackgroundColor(getResources().getColor(R.color.bg_color));
            downLayout.setBackgroundColor(getResources().getColor(R.color.bg_color));
        }
        //по рандому создаются новые щенки
        int random = (int) (Math.random() * 100);
        if (random % 100 == 0) {
            ghost.add(new MiniGameGhost(this));
            nGhosts++;
            liveGhosts++;
        }
        if (liveGhosts == 0) {
            String seconds;
            if (count / 100 % 10 == 1) seconds = getString(R.string.second);
            else if (count / 100 % 10 > 1 && count / 100 % 10 < 5)
                seconds = getString(R.string.seconds);
            else seconds = getString(R.string.sec);
            Utils.AlertDialog(this, getString(R.string.congratulation),
                    getString(R.string.spend) + (count / 100 - miniGameStart / 100) + seconds + getString(R.string.life), getString(R.string.ok));
            count = 0;
            if (robot != null)
                robot.restartAnim();
            upLayout.setBackgroundColor(getResources().getColor(R.color.clear));
            downLayout.setBackgroundColor(getResources().getColor(R.color.clear));
        } else if (liveGhosts > 0)
            textViewVersion.setText(getString(R.string.left_pet) + liveGhosts);
    }

    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, 10);
        }

        public void onTick(long millisUntilFinished) {
            if (!pause) update();
        }

        public void onFinish() {
        }

    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Utils.makeToast(this, getString(R.string.exit));
        back_pressed = System.currentTimeMillis();
    }
}
