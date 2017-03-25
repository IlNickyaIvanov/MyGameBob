package com.example.a1.mygame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class Utils {
     static int level;
    private static  boolean AlertDialogVisible;
    public static void makeToast(Activity main,String text){
        Toast.makeText(main,text,Toast.LENGTH_LONG).show();
    }
    public static void AlertDialog(Activity main, String title, String Message, String TextButton ){
        ActivitySinglePlayer.AlertDialogMessage=null;

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
        ad.setNegativeButton(TextLeftButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intent = new Intent(main, ActivityLevelMenu.class);
                main.startActivity(intent);
            }
        });
        ad.setPositiveButton(TextRightButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intent = new Intent(main, ActivityLevelMenu.class);
               if(level<2) intent.putExtra("level_num", level+1);
                else intent.putExtra("level_num",0);
                main.startActivity(intent);
                main.finish();
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
