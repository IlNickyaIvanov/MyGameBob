package com.example.a1.mygame;


public class Square_lava extends Square {
    Square_lava(MyGame main, float x, float y,int size){
        super(main,x,y,size,1);
        square.setImageResource(R.drawable.square_lava);
    }
}
