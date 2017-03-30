package com.example.a1.mygame;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class Robot {
    float x, y;
    int sqX, sqY;
    int size;
    private int onTick;
    private int screenY;
    private int BUSY;
    private ImageView body, eye;
    private AnimationDrawable anim_body, anim_eye;
    TranslateAnimation translateAnimation,MoveMySelf;

    Robot(Activity main, float x, float y, int size, int screenY,int onTick,int bodyType,int BUSYtype) {
        body = new ImageView(main);
        eye = new ImageView(main);
        this.x = x;
        this.y = y;
        this.size = size;
        this.screenY = screenY;
        this.onTick=onTick;
        this.BUSY=BUSYtype;
        main.addContentView(body, new RelativeLayout.LayoutParams(size, size));
        main.addContentView(eye, new RelativeLayout.LayoutParams(size, size));
        CreateAnim(bodyType);
    }

    private void CreateAnim(int bodyType) {
        body.setX(x);
        eye.setX(x);
        body.setY(y);
        eye.setY(y);
        if (bodyType==1)
        body.setBackgroundResource(R.drawable.body_anim1);
        else
            body.setBackgroundResource(R.drawable.body_anim2);
        eye.setBackgroundResource(R.drawable.eye_anim);
        anim_body = (AnimationDrawable) body.getBackground();
        anim_eye = (AnimationDrawable) eye.getBackground();
        anim_body.start();
        anim_eye.start();
    }

    void RobotMove(float y, float x, int sqY, int sqX,boolean isBUSY) {
        int n=0;
        if (isBUSY) n=BUSY;
        translateAnimation = new TranslateAnimation(this.x, x, this.y - screenY / 17, y - screenY / 17+n);
        translateAnimation.setDuration(onTick);
        translateAnimation.setFillAfter(true);
        body.startAnimation(translateAnimation);
        eye.startAnimation(translateAnimation);
        translateAnimation.hasEnded();
        this.x = x;
        this.y = y;
        this.sqX = sqX;
        this.sqY = sqY;
    }
    void MoveMySelf(boolean isBUSY){
        int n=0;
        if (isBUSY) n=BUSY;
        MoveMySelf = new TranslateAnimation(this.x, this.x, this.y - screenY / 17+n, this.y - screenY / 17+n);
        MoveMySelf.setDuration(onTick);
        MoveMySelf.setFillAfter(true);
        body.startAnimation(MoveMySelf);
        eye.startAnimation(MoveMySelf);
        MoveMySelf.hasEnded();
    }
}
