package com.example.a1.mygame;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//мини игра
class MiniGameGhost {
    private float x;
    private float y;
    boolean live;
    private int size, vx, vy;
    private int screenWidth, screenHeight;
    private ImageView image;

    MiniGameGhost(Activity main) {
        Display display = main.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        live = true;
        bornSize();
        bornLocation();
        bornSpeed();
        image = new ImageView(main);
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        image.setX(x);
        image.setY(y);
        image.setImageResource(R.drawable.robot);
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    touch();
                }
                return false;
            }
        });
    }

    private void bornLocation() {
        x = (float) Math.abs((Math.random() * screenWidth - size));
        y = (float) Math.abs((Math.random() * screenHeight - size));
    }

    private void bornSpeed() {
        vx = (int) (Math.random() * 10 + 1);
        vy = (int) (Math.random() * 10 + 1);
    }

    private void bornSize() {
        if (screenWidth > screenHeight)
            size = (int) (Math.random() * screenHeight / 7 + screenHeight / 10);
        else
            size = (int) (Math.random() * screenWidth / 7 + screenWidth / 10);
    }

    void move() {
        x += vx;
        y += vy;
        if (x >= screenWidth - size || x < 0) vx = -vx;
        if (y >= screenHeight - size || y < 0) vy = -vy;
        image.setX(x);
        image.setY(y);
    }

    private void touch() {
        Animation AlphaAnimation = new AlphaAnimation(1, 0);
        AlphaAnimation.setDuration(200);
        image.startAnimation(AlphaAnimation);
        live = false;
        ActivityMain.liveGhosts--;
        ActivityMain.diedGhosts++;
        FrameLayout parent = (FrameLayout) image.getParent();
        parent.removeView(image);
    }
}
