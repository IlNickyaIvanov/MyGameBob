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

public class MainActivity extends Activity {
    private boolean pause;
    private final int miniGameStart=1000;
    private int MaxNGhost=50;
    protected static int screenWidth, screenHeight;
    protected static int liveGhosts, diedGhosts,nGhosts;
    private static int count;
    MiniGameGhost ghost[] = new MiniGameGhost[200];
    TextView textViewVersion,textViewWelcome;
    Robot robot;

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
        textViewWelcome = (TextView)findViewById(R.id.textView);
        textViewVersion = (TextView) findViewById(R.id.textView2);
        textViewWelcome.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/ObelixPro.ttf"));
        textViewVersion.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/ObelixPro.ttf"));
        MyTimer timer = new MyTimer();
        timer.start();
        robot = new Robot(this,screenWidth/4,screenHeight/2,screenWidth/2,screenHeight,screenWidth);
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
        Intent intent = new Intent (MainActivity.this,LevelMenu.class);
        startActivity(intent);}

    }
    public void OnClickHowToPlay(View view) {
        if (liveGhosts<=0) {
            Intent intent = new Intent(MainActivity.this, HowToPlay.class);
            startActivity(intent);
        }
    }
    public void onClickSettings(View view) {
        if (liveGhosts<=0){
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);}
    }
    public void OnClickSecret(View view) {
        if(liveGhosts==0)count=999;
    }

    public void onClickImage(View view) {
        if (liveGhosts<=0){
        if (robot.anim_body.isRunning()){ robot.anim_body.stop();
            robot.anim_eye.stop();}
        else {
            robot.anim_body.start();
            robot.anim_eye.start();
        }}
    }



    void update(){
        count++;//счетчик
        if (liveGhosts==0) {textViewVersion.setText(R.string.version);textViewWelcome.setText(R.string.welcome);}
        if (count>=miniGameStart && liveGhosts<MaxNGhost && nGhosts<ghost.length) {miniGameGhost();}
        for (int i = 0; i < nGhosts; i++) {if (ghost[i].live) ghost[i].move(); else diedGhosts++;}
    }
    void miniGameGhost (){
        textViewWelcome.setText(R.string.attack);
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
}
