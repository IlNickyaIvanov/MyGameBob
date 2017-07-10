package com.example.a1.mygame;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//это классс клетки из редактора уровней
 class EditSquare {
    private ImageView image;
    static Target target;
    static Start start;
    float x, y;
    private int sqX, sqY;
    private int IconType;

    EditSquare(Activity main, final float x, final float y, final int size, int IconType, final int ListLine, final int ListCol) {
        this.x = x;
        this.y = y;
        this.sqY = ListLine;
        this.sqX = ListCol;
        image = new ImageView(main);
        image.setX(x); // координаты
        image.setY(y);
        image.setScaleType(ImageView.ScaleType.FIT_XY); // подгоняет под пропорции Layout, иначе будут сохранены исходные пропорции изображения
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        image.setOnClickListener(new View.OnClickListener() {  // вешаем слушателя на клик
            public void onClick(View v) {
                if (ActivityLevelEditor.setEnd) target.SetNewTarget(ListCol, ListLine);
                else if (ActivityLevelEditor.setStart) start.SetNewStart(ListCol, ListLine);
                else {
                    if (target.sqX == sqX && target.sqY == sqY) {
                        ActivityLevelEditor.etEndx.setText("");
                        ActivityLevelEditor.etEndy.setText("");
                        target.clearTarget();
                    }
                    if (start.sqX == sqX && start.sqY == sqY) {
                        ActivityLevelEditor.etStartx.setText("");
                        ActivityLevelEditor.etStarty.setText("");
                        start.clearStart();
                    }
                    setIcon(ActivityLevelEditor.IconType);
                    ActivityLevelEditor.edMap.get(ListLine).set(ListCol, ActivityLevelEditor.IconType);
                }
            }
        });
        setIcon(IconType);
        if (ListLine == ActivityLevelEditor.edMap.size() - 1 && ListCol == ActivityLevelEditor.edMap.get(0).size() - 1) {
            target = new Target(main, size);
            start = new Start(main, size);
        }
    }

    private void setIcon(int IconType) {
        this.IconType = IconType;

        switch (IconType) {
            case (0):
                image.setImageResource(R.drawable.square_empty);
                break;
            case (1):
                image.setImageResource(R.drawable.square_lava);
                break;
            case (2):
                image.setImageResource(R.drawable.kislota);
                break;
            case (3):
                image.setBackgroundResource(R.drawable.square_empty);
                image.setImageResource(R.drawable.
                        cookie);
                break;
        }
    }

    private int getIconType() {
        return IconType;
    }

    void DELETE() {
        FrameLayout parent = (FrameLayout) image.getParent();
        parent.removeView(image);
    }

    //класс старт - это начальное положение Боба
     class Start {
        ImageView start;
        int sqX, sqY;

        Start(Activity main, int size) {
            start = new ImageView(main);
            if (!ActivityLevelEditor.etStartx.getText().toString().equals("")) {
                this.sqX = Integer.parseInt(ActivityLevelEditor.etStartx.getText().toString());
                this.sqY = Integer.parseInt(ActivityLevelEditor.etStarty.getText().toString());
            } else {
                this.sqX = 0;
                this.sqY = 0;
                ActivityLevelEditor.etStartx.setText("");
                ActivityLevelEditor.etStarty.setText("");
                start.setVisibility(View.INVISIBLE);
            }
            float setX = 0;
            float setY = 0;
            for (int i = 0; i < ActivityLevelEditor.Squares.size(); i++) {
                EditSquare square = ActivityLevelEditor.Squares.get(i);
                if (square.sqX == sqX && square.sqY == sqY) {
                    start.setX(square.x);
                    setX = square.x;
                    start.setY(square.y);
                    setY = square.y;
                }
            }
            if (setX == 0 && setY == 0) {
                start.setX(0);
                start.setY(0);
                ActivityLevelEditor.etStartx.setText("");
                ActivityLevelEditor.etStarty.setText("");
                start.setVisibility(View.INVISIBLE);
            }

            start.setImageResource(R.drawable.eye6);
            start.setBackgroundResource(R.drawable.body5);
            main.addContentView(start, new RelativeLayout.LayoutParams(size, size));
        }

        void SetNewStart(int sqx, int sqy) {
            ActivityLevelEditor.setStart = false;
            ActivityLevelEditor.layoutStart.clearAnimation();
            EditSquare square = ActivityLevelEditor.Squares.get(sqy * ActivityLevelEditor.edMap.get(sqy).size() + sqx);
            if (square.getIconType() == 0) {
                this.sqX = sqx;
                this.sqY = sqy;
                float x = square.x;
                float y = square.y;
                start.setX(x);
                start.setY(y);
                start.setVisibility(View.VISIBLE);
                ActivityLevelEditor.etStartx.setText(sqx + "");
                ActivityLevelEditor.etStarty.setText(sqy + "");
            }
        }

        void clearStart() {
            start.setVisibility(View.INVISIBLE);
        }
    }

    //цель - это класс с конечным положением
     class Target {
        ImageView target;
        int sqX, sqY;

        Target(Activity main, int size) {
            target = new ImageView(main);
            if (!ActivityLevelEditor.etEndx.getText().toString().equals("")) {
                this.sqX = Integer.parseInt(ActivityLevelEditor.etEndx.getText().toString());
                this.sqY = Integer.parseInt(ActivityLevelEditor.etEndy.getText().toString());
            } else {
                this.sqX = 0;
                this.sqY = 0;
                ActivityLevelEditor.etEndx.setText("");
                ActivityLevelEditor.etEndy.setText("");
                target.setVisibility(View.INVISIBLE);
            }
            float setX = 0;
            float setY = 0;
            for (int i = 0; i < ActivityLevelEditor.Squares.size(); i++) {
                EditSquare square = ActivityLevelEditor.Squares.get(i);
                if (square.sqX == sqX && square.sqY == sqY) {
                    target.setX(square.x);
                    setX = square.x;
                    target.setY(square.y);
                    setY = square.y;
                }
            }
            if (setX == 0 && setY == 0) {
                target.setX(0);
                target.setY(0);
                ActivityLevelEditor.etEndx.setText("");
                ActivityLevelEditor.etEndy.setText("");
                target.setVisibility(View.INVISIBLE);
            }
            target.setImageResource(R.drawable.target);
            main.addContentView(target, new RelativeLayout.LayoutParams(size, size));
        }

        void SetNewTarget(int sqx, int sqy) {
            ActivityLevelEditor.setEnd = false;
            ActivityLevelEditor.layoutEnd.clearAnimation();
            this.sqX = sqx;
            this.sqY = sqy;
            float x = ActivityLevelEditor.Squares.get(sqy * ActivityLevelEditor.edMap.get(sqy).size() + sqx).x;
            float y = ActivityLevelEditor.Squares.get(sqy * ActivityLevelEditor.edMap.get(sqy).size() + sqx).y;
            target.setX(x);
            target.setY(y);
            target.setVisibility(View.VISIBLE);
            ActivityLevelEditor.etEndx.setText(sqx + "");
            ActivityLevelEditor.etEndy.setText(sqy + "");
        }

        void clearTarget() {
            target.setVisibility(View.INVISIBLE);
        }
    }
}
