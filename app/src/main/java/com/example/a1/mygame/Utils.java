package com.example.a1.mygame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Utils {
     static int level;
    private static  boolean AlertDialogVisible;
    public static void makeToast(Activity main,String text){
        Toast.makeText(main,text,Toast.LENGTH_LONG).show();
    }
    public static void AlertDialog(Activity main, String title, String Message, String TextButton ){
        MyGame.AlertDialogMessage=null;

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        AlertDialogShow(builder,title,Message,TextButton);
    }
    private static void AlertDialogShow(AlertDialog.Builder builder,String title, String Message, String TextButton){
        AlertDialogVisible = true;
        builder.setTitle(title)
                .setMessage(Message)
                .setCancelable(false)
                .setNegativeButton(TextButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                AlertDialogVisible = false;
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    static boolean isAlertDialogVisible() {
        return AlertDialogVisible;
    }
    public static void TwoButtonAllertDialog (final Activity main,
                                              String title, String message,
                                              String TextLeftButton, String TextRightButton, final int level ){
        AlertDialog.Builder ad = new AlertDialog.Builder(main);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(TextLeftButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intent = new Intent(main, LevelMenu.class);
                main.startActivity(intent);
            }
        });
        ad.setNegativeButton(TextRightButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
              LevelMenu lvl_menu=new LevelMenu();
               if(level<2)lvl_menu.SELECT_LEVEL(level+1,main);
                else {Intent intent = new Intent(main, LevelMenu.class);
                   AlertDialog(main,"Уровень", "Следующий уровень находится в разработке.","В главное меню");
                main.startActivity(intent);
               }
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(main, "Вы ничего не выбрали",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.show();
    }
}
