package com.example.a1.mygame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class ActivityMain extends Activity {
    private boolean pause;
    private final int miniGameStart=10000;
    private int MaxNGhost=50;
    protected static int screenWidth, screenHeight;
    protected static int liveGhosts, diedGhosts,nGhosts;
    private static int count;
    private static long back_pressed;
    MiniGameGhost ghost[] = new MiniGameGhost[200];
    TextView textViewVersion, nameGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth=metrics.widthPixels;
        screenHeight=metrics.heightPixels;

        nameGame = (TextView)findViewById(R.id.name_game);
        textViewVersion = (TextView) findViewById(R.id.textView2);

        nameGame.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/piecesfi.ttf"));
        textViewVersion.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ObelixPro.ttf"));
        MyTimer timer = new MyTimer();
        timer.start();
    }
    @Override
    protected void onPause(){
        super.onPause();
        pause=true;
    }
    @Override
    protected void onResume(){
        super.onPause();
        count=0;
        pause=false;
    }


    public void onClickPlay(View view) {
        if (liveGhosts<=0){
        Intent intent = new Intent (ActivityMain.this,ActivityLevelMenu.class);
        startActivity(intent);}

    }
    public void OnClickHowToPlay(View view) {
        if (liveGhosts<=0) {
            Intent intent = new Intent(ActivityMain.this, ActivityHowToPlay.class);
            startActivity(intent);
        }
    }
    public void onClickSettings(View view) {
        if (liveGhosts<=0){
        Intent intent = new Intent(ActivityMain.this, ActivitySettings.class);
        startActivity(intent);}
    }

    void update(){
        count++;//счетчик
        if (liveGhosts==0) {textViewVersion.setText(R.string.version);}
        if (count>=miniGameStart && liveGhosts<MaxNGhost && nGhosts<ghost.length) {miniGameGhost();}
        for (int i = 0; i < nGhosts; i++) {if (ghost[i].live) ghost[i].move(); else diedGhosts++;}
    }
    void miniGameGhost (){
        if  (count==miniGameStart){ghost[nGhosts]=new MiniGameGhost(this);nGhosts++; liveGhosts++;
            Utils.AlertDialog(this,"ВНИМАНИЕ!",
                    "Вас не было слишком долго...\nЩенкам стало скучно и теперь они жаждут ласки!","погладить всех");}
        int random = (int)(Math.random()*100);
        if (random%100==0){ghost[nGhosts]=new MiniGameGhost(this);nGhosts++; liveGhosts++;}
        if (liveGhosts==0) {
            String seconds;
            if (count/100%10==1)seconds=" секунду  ";
            else if (count/100%10>1 && count/100%10<5) seconds=" секунды ";
            else seconds=" секунд ";
            Utils.AlertDialog(this,"Поздралвяем!",
                    "Вы потратили: "+(count/100-10)+seconds+"своей жизни в пустую.","ок");
            count = 0;}
        else if (liveGhosts>0)textViewVersion.setText("осталось погладить>> "+liveGhosts);
    }

    class MyTimer extends CountDownTimer {
        MyTimer(){
            super(Integer.MAX_VALUE, 10);
        }
        public void onTick(long millisUntilFinished) {if(!pause)update();}
        public void onFinish(){
        }

    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Utils.makeToast(this,"Нажми еще раз для выхода.");
        back_pressed = System.currentTimeMillis();
    }
}
