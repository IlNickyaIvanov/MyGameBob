package com.example.a1.mygame;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class MiniGameGhost  {
    private float x;
    private float y;
    boolean live;
    private int size, vx, vy;
    private int screenWidth, screenHeight;
    private ImageView image;

    MiniGameGhost(Activity main){
        screenWidth = ActivityMain.screenWidth;
        screenHeight = ActivityMain.screenHeight;
        live = true;
        bornSize();
        bornLocation();
        bornSpeed();
        image = new ImageView(main);
        main.addContentView(image, new RelativeLayout.LayoutParams(size,size));
        image.setX(x);
        image.setY(y);
        image.setImageResource(R.drawable.robot);
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    touch();
                }
                return false;
            }
        });
    }
     private void bornLocation() {
        x = (float) Math.abs((Math.random() * screenWidth - size));
        y = (float) Math.abs((Math.random() * screenWidth - size));
    }
    private void bornSpeed() {
        vx = (int)(Math.random()*10+1);
        vy = (int)(Math.random()*10+1);
    }
    private void bornSize(){
        size = (int)(Math.random()*screenWidth/7+screenWidth/10);
    }
     void move(){
        x+=vx;
        y+=vy;
        if(x>=screenWidth-size || x<0)  vx=-vx;
        if(y>=screenHeight-size || y<0) vy=-vy;
        image.setX(x);
        image.setY(y);
    }
    private void touch(){
        Animation AlphaAnimation = new AlphaAnimation(1,0);
        AlphaAnimation.setDuration(200);
        image.startAnimation(AlphaAnimation);
        live = false;
         ActivityMain.liveGhosts--;
        ActivityMain.diedGhosts++;
        FrameLayout parent = (FrameLayout) image.getParent();
        parent.removeView(image);
    }
}
