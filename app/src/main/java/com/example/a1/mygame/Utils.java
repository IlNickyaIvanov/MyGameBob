package com.example.a1.mygame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//полезные и универсальные методы
class Utils {
    private static boolean ADPositBut;
    private static boolean ADVisible;
    static void makeToast(Activity main, String text){
        Toast.makeText(main,text,Toast.LENGTH_SHORT).show();
    }
    public static void AlertDialog(Activity main, String title, String Message, String TextButton ){
        ActivitySinglePlayer.AlertDialogMessage=null;

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle(title)
                .setMessage(Message)
                .setCancelable(false)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton(TextButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ADVisible=false;
                            }
                        });

        AlertDialog alert = builder.create();
        if(!( main).isFinishing())
        {
            alert.show();
            ADVisible=true;
        }
        else {
            AlertDialog(ActivitySinglePlayer.singleplayer,title,Message,TextButton);
            Tutorial.activity=ActivitySinglePlayer.singleplayer;
        }
    }
    static AlertDialog.Builder TwoButtonAllertDialog(final Activity main,
                                      String title, String message,
                                      String TextLeftButton, String TextRightButton, final int level){
        AlertDialog.Builder ad = new AlertDialog.Builder(main);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setNegativeButton(TextLeftButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intent = new Intent(main, ActivityLevelMenu.class);
                main.startActivity(intent);
                main.finish();
                ADPositBut=false;
                ADVisible=false;
            }
        });
        ad.setPositiveButton(TextRightButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
               if (level!=0) {
                   Intent intent = new Intent(main, ActivityLevelMenu.class);
                   intent.putExtra("level_num", level + 1);
                   main.startActivity(intent);
                   main.finish();
               }
                else {
                   dialog.cancel();
               }
                Tutorial.ZEROING();
                ADPositBut=true;
                ADVisible=false;
            }
        });
        ad.setCancelable(false);
        if(!( main).isFinishing())
        {
            ad.show();
            ADVisible=true;
        }
        else {
            TwoButtonAllertDialog(ActivitySinglePlayer.singleplayer,title,message,TextLeftButton,TextRightButton,level);
            Tutorial.activity=ActivitySinglePlayer.singleplayer;
        }
        return ad;
    }
    static void EditAlert(String textMes, String edMes, final Context context, final Activity activity){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.promt, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
        if(edMes!=null)userInput.setText(edMes);
        final TextView message = (TextView)promptsView.findViewById(R.id.tv);
        if(textMes!=null)message.setText(textMes);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //Вводим текст и отображаем в строке ввода на основном экране:
                                ActivityLevelEditor.name = userInput.getText().toString();
                                ActivityLevelEditor.upload();
                                ActivityLevelEditor.db.close();
                                // переход к главной activity
                                Intent intent = new Intent(activity, ActivityLevelMenu.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        })
                .setNegativeButton("Abort",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        //Создаем AlertDialog:
        AlertDialog alertDialog = mDialogBuilder.create();

        //и отображаем его:
        alertDialog.show();

    }
    static boolean isADVisible() {
        return ADVisible;
    }
    static boolean isADPositBut() {
        return ADPositBut;
    }
    static void setADPositBut(boolean ADPositBut) {
        Utils.ADPositBut = ADPositBut;
    }
}
