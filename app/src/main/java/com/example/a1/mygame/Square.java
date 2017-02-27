package com.example.a1.mygame;

import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Square{
    int ID_NUMBER;
    float x,y;
    ImageView square;
    Square (MyGame main, float x, float y,int size,int ID_NUMBER){
        this.ID_NUMBER=ID_NUMBER;
        this.x=x; this.y=y;
        square = new ImageView(main);
        main.addContentView(square,new RelativeLayout.LayoutParams(size,size));
        square.setX(x);
        square.setY(y);
    }
}
