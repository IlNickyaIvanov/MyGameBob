package com.example.a1.mygame;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class Square_lava extends Square {
    private ImageView lava;

    Square_lava(ActivitySinglePlayer main, float x, float y, int size) {
        super(main, x, y, size, 1);
        square.setImageResource(R.drawable.square_lava);
        lava = new ImageView(main);
        lava.setX(x);
        lava.setY(y);
        lava.setImageResource(R.drawable.lava_lay);
        main.addContentView(lava, new RelativeLayout.LayoutParams(size, size));
        Animation animation = AnimationUtils.loadAnimation(main, R.anim.lava);
        lava.startAnimation(animation);
    }
}
