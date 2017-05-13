package com.example.a1.mygame;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Square{
    int ID_NUMBER;
    float x,y;
    Activity activity;
    int size;
    ImageView square;
    Square_empty.Food food;
    Square_empty.Target target;
    Square (final ActivitySinglePlayer main, float x, float y, int size, int ID_NUMBER){
        this.ID_NUMBER=ID_NUMBER;
        this.x=x; this.y=y;
        this.activity = main;
        this.size = size;
        square = new ImageView(main);
        main.addContentView(square,new RelativeLayout.LayoutParams(size,size));
        square.setX(x);
        square.setY(y);
        square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        Utils.makeToast(main,"'НАЗАД' - для закрытия клавиатуры.");
            }
        });
    }
    void setTarget(){
        target = new Square_empty.Target(activity,x,y,size);
    }
    void clearTarget(){
        if (target!=null)
            target.CLEAR();
    }
    void setFood(){
        food = new Square_empty.Food(activity,x,y,size);
    }
}
