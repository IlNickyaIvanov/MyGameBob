package com.example.a1.mygame;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ActivityMain extends Activity {
    private boolean pause;
    private final int miniGameStart=4000;
    private int MaxNGhost=50;
    protected static int screenWidth, screenHeight;
    protected static int liveGhosts, diedGhosts,nGhosts;
    private static int count;
    private static long back_pressed;
    ArrayList<MiniGameGhost> ghost=new ArrayList<>();
    TextView textViewVersion, nameGame;
    ImageView slime;

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

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            int size = screenHeight/2;
            float x = (float)(screenWidth/2-size)/2;
            float y = (float)(screenHeight-size)/2;
            slime= new ImageView(this);
            this.addContentView(slime, new RelativeLayout.LayoutParams(size*2, size*2));
            slime.setX(x-size/2);slime.setY(y-size/2);
            slime.setImageResource(R.drawable.slime);
            Robot robot = new Robot(this,x,y,size,screenHeight,1000,1,0,0,0);
        }


        nameGame = (TextView)findViewById(R.id.name_game);
        textViewVersion = (TextView) findViewById(R.id.textView2);

        nameGame.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ObelixPro.ttf"));
        textViewVersion.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ObelixPro.ttf"));
        MyTimer timer = new MyTimer();
        timer.start();
    }
    @Override
    protected void onPause(){
        super.onPause();
        pause=true;
        nGhosts=0;
        liveGhosts=0;
        diedGhosts=0;
        nGhosts=0;
        ghost.clear();
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
        if (count>=miniGameStart && liveGhosts<MaxNGhost && nGhosts<MaxNGhost) {
            miniGameGhost();}
        for (int i = 0; i < nGhosts; i++) {if (ghost.get(i).live) ghost.get(i).move(); else diedGhosts++;}
    }
    void miniGameGhost (){
        if  (count==miniGameStart){ghost.add(new MiniGameGhost(this));nGhosts++; liveGhosts++;
            Utils.AlertDialog(this,"ВНИМАНИЕ!",
                    "Вас не было слишком долго...\nЩенкам стало скучно и теперь они жаждут ласки!","погладить всех");}
        int random = (int)(Math.random()*100);
        if (random%100==0){ghost.add(new MiniGameGhost(this));nGhosts++; liveGhosts++;}
        if (liveGhosts==0) {
            String seconds;
            if (count/100%10==1)seconds=" секунду  ";
            else if (count/100%10>1 && count/100%10<5) seconds=" секунды ";
            else seconds=" секунд ";
            Utils.AlertDialog(this,"Поздралвяем!",
                    "Вы потратили: "+(count/100-10)+seconds+"своей жизни впустую.","ок");
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
