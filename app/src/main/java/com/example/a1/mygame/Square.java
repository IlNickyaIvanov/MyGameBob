package com.example.a1.mygame;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//главный класс клетки, от которого уже наследуются другие
class Square {
    boolean isVISIBLE = true;
    int ID_NUMBER;
    float x, y;
    Activity activity;
    int size;
    ImageView square;
    Square_empty.Food food;
    private Square_empty.Target target;

    Square(final ActivitySinglePlayer main, float x, float y, int size, int ID_NUMBER) {
        this.ID_NUMBER = ID_NUMBER;
        this.x = x;
        this.y = y;
        this.activity = main;
        this.size = size;
        square = new ImageView(main);
        main.addContentView(square, new RelativeLayout.LayoutParams(size, size));
        square.setX(x);
        square.setY(y);
        square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.makeToast(main, main.getString(R.string.cl_key));
            }
        });
    }

    void EAT() {
        food.Eat();
        ID_NUMBER = 0;
    }

    void Restart() {
        food.Restart();
        ID_NUMBER = 3;
    }

    void setTarget() {
        clearTarget();
        target = new Square_empty.Target(activity, x, y, size);
    }

    void clearTarget() {
        if (target != null)
            target.CLEAR();
    }

    void setFood() {
        ID_NUMBER = 3;
        if (food != null) food.Eat();
        food = new Square_empty.Food(activity, x, y, size);
    }

    void INVISIBLE() {
        square.clearAnimation();
        isVISIBLE = false;
        square.setVisibility(View.INVISIBLE);
    }

    void VISIBLE() {
        square.clearAnimation();
        isVISIBLE = true;
        square.setVisibility(View.VISIBLE);
    }

    void blinkyVISIBLE() {
        isVISIBLE = true;
        square.setVisibility(View.VISIBLE);
        Animation blinkyANIMATION = AnimationUtils.loadAnimation(activity, R.anim.lava);
        square.startAnimation(blinkyANIMATION);
    }
}
