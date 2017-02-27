package com.example.a1.mygame;


public class Square_empty extends Square {
    Square_empty(MyGame main, float x, float y,int size){
        super(main,x,y,size,0);
        square.setImageResource(R.drawable.square_empty);
    }
}
