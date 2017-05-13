package com.example.a1.mygame;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class Robot {
    float x, y;
    int sqX, sqY;
    int size;
    private int onTickMove;
    private int screenY;
    private int BUSY;
    boolean pause=true;
    float corY,corX;

    private ImageView body, eye;
    private AnimationDrawable anim_body, anim_eye;
    TranslateAnimation translateAnimation,MoveMySelf;

    ImageView image1,image2,image3;
    Animation alphaAnimation1,alphaAnimation2,alphaAnimation3;

    Robot(Activity main, float x, float y, int size, int screenY,int onTick,int bodyType,int BUSYtype,float corX,float corY) {
        this.corX=corX;
        this.corY = corY;
        body = new ImageView(main);
        eye = new ImageView(main);

        image1= new ImageView(main);
        image2= new ImageView(main);
        image3= new ImageView(main);

        this.x = x;
        this.y = y;
        this.size = size;
        this.screenY = screenY;
        this.onTickMove = onTick;
        this.BUSY=BUSYtype;

        main.addContentView(body, new RelativeLayout.LayoutParams(size, size));
        main.addContentView(eye, new RelativeLayout.LayoutParams(size, size));

        main.addContentView(image1,new RelativeLayout.LayoutParams(size,size));
        main.addContentView(image2,new RelativeLayout.LayoutParams(size,size));
        main.addContentView(image3,new RelativeLayout.LayoutParams(size,size));

        alphaAnimation1 = AnimationUtils.loadAnimation(main,R.anim.alpha);
        alphaAnimation2 = AnimationUtils.loadAnimation(main,R.anim.alpha);
        alphaAnimation3 = AnimationUtils.loadAnimation(main,R.anim.alpha);

        alphaAnimation2.setStartOffset(100);
        alphaAnimation3.setStartOffset(200);

        CreateAnim(bodyType);
    }

    private void CreateAnim(int bodyType) {
        body.setX(x);body.setY(y);
        eye.setX(x);eye.setY(y);

        image1.setX(x);image1.setY(y);
        image2.setX(x);image2.setY(y);
        image3.setX(y);image3.setY(y);

        if (bodyType==1)
        body.setBackgroundResource(R.drawable.body_anim1);
        else
            body.setBackgroundResource(R.drawable.body_anim2);
        eye.setBackgroundResource(R.drawable.eye_anim);

        image1.setImageResource(R.drawable.search_anim1);
        image2.setImageResource(R.drawable.search_anim2);
        image3.setImageResource(R.drawable.search_anim3);

        anim_body = (AnimationDrawable) body.getBackground();
        anim_eye = (AnimationDrawable) eye.getBackground();

        anim_body.start();
        anim_eye.start();

        image1.setVisibility(View.INVISIBLE);
        image2.setVisibility(View.INVISIBLE);
        image3.setVisibility(View.INVISIBLE);
    }

    void RobotMove(float y, float x, int sqY, int sqX,boolean isBUSY) {
        if (!pause){
            body.setX(this.x);
            body.setY(this.y);
            eye.setX(this.x);
            eye.setY(this.y);
            pause=true;
        }
        int n=0;
        if (isBUSY) n=BUSY;
        translateAnimation = new TranslateAnimation(this.x-corX, x-corX, this.y - corY, y -corY+n);
        translateAnimation.setDuration(onTickMove);
        translateAnimation.setFillAfter(true);

        body.startAnimation(translateAnimation);
        eye.startAnimation(translateAnimation);

        image1.setX(x);image1.setY(y);
        image2.setX(x);image2.setY(y);
        image3.setX(x);image3.setY(y);

        translateAnimation.hasEnded();
        this.x = x;
        this.y = y;
        this.sqX = sqX;
        this.sqY = sqY;
    }
    void MoveMySelf(boolean isBUSY){
        int n=0;
        if (isBUSY) n=BUSY;
        MoveMySelf = new TranslateAnimation(this.x-corX, this.x-corX, this.y - corY, this.y -corY+n);
        MoveMySelf.setDuration(onTickMove);
        MoveMySelf.setFillAfter(true);
        body.startAnimation(MoveMySelf);
        eye.startAnimation(MoveMySelf);
        MoveMySelf.hasEnded();
    }

    void SearchAnim (int AnimID){
        switch (AnimID){
            case(1):
                image1.setRotation(0);
                image2.setRotation(0);
                image3.setRotation(0);
                break;
            case(2):
                image1.setRotation(180);
                image2.setRotation(180);
                image3.setRotation(180);
                break;
            case(3):
                image1.setRotation(270);
                image2.setRotation(270);
                image3.setRotation(270);
                break;
            case(4):
                image1.setRotation(90);
                image2.setRotation(90);
                image3.setRotation(90);
                break;
        }
        image1.startAnimation(alphaAnimation1);
        image2.startAnimation(alphaAnimation2);
        image3.startAnimation(alphaAnimation3);
    }

}
