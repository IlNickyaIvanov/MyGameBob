package com.example.a1.mygame;


import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

 class Square_empty extends Square {
     Activity activity;
     float x,y;
     int size;
    Square_empty(ActivitySinglePlayer main, float x, float y, int size,boolean isFood){
        super(main,x,y,size,(isFood)?3:0);
        square.setBackgroundResource(R.drawable.square_empty);
        this.activity=main;
        this.x = x;
        this.y = y;
        this.size = size;
        if (isFood)
            food = new Food(main,x,y,size);
    }
     static class Target{
         ImageView target;
         Activity main;
         Target (Activity main, float x,float y, int size){
             this.main = main;
             target=new ImageView(main);
             target.setX(x); target.setY(y);
             target.setImageResource(R.drawable.target);
             main.addContentView(target,new RelativeLayout.LayoutParams(size,size));
         }
         void CLEAR(){
             target.setVisibility(View.INVISIBLE);
         }
     }

    static class Food{
        ImageView food;
        private int foodType;
        Activity main;
       private boolean isEaten;
        Food(Activity main,float x, float y, int size){
            this.main = main;
            food=new ImageView(main);
            food.setX(x); food.setY(y);
            foodType=(int)(Math.random()*6);
            switch (foodType){
                case (1):food.setImageResource(R.drawable.big_cake);
                    break;
                case (2):food.setImageResource(R.drawable.cake);
                    break;
                case (3):food.setImageResource(R.drawable.choc_cookie);
                    break;
                case (4):food.setImageResource(R.drawable.cookie);
                    break;
                case (5):food.setImageResource(R.drawable.donut);
                    break;
                default:food.setImageResource(R.drawable.cookie);
                    break;
            }
            main.addContentView(food,new RelativeLayout.LayoutParams(size,size));
        }
        void Eat(){
            isEaten=true;
            Animation animation;
            animation = AnimationUtils.loadAnimation(main,R.anim.food);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    food.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            food.startAnimation(animation);
        }
        void Restart(){
            food.setVisibility(View.VISIBLE);
            isEaten=false;
        }
        boolean isEaten(){
            return isEaten;
        }
    }
}
