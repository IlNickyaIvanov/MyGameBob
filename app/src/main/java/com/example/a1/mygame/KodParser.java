package com.example.a1.mygame;

import java.util.ArrayList;

class KodParser {
    private Square square[][];
    int action;
    int x, y;//положение робота

    private boolean pause;

    private boolean kodERROR;//пауза для ошибок

    private boolean loop = false;
    private static int symbolslENGTH;
    private String text;
    int start, stop;
    int ARx[];//массивы пошаговых
    int ARy[];//координат положения робота
    String ComandName[] = new String[100];//команды
    KodParser(int StartX, int StartY, Square square[][],int ComandsLimit) {
        this.square = square;
        ARx = new int[ComandsLimit];
        ARy = new int[ComandsLimit];
        x = StartX;
        y = StartY;
    }


    int kodParser(String text) {
        pause = false;
        kodERROR = false;
        start = 0;
        stop = 0;
        String[] MainLine;
        this.text=text;
        if (text.replaceAll("\n", "").replaceAll(";", "").isEmpty()) {
            ActivitySinglePlayer.AlertDialogMessage = "А где сами команды?";
            pause = true;
            kodERROR = true;
        } else try {
            String[] line;
            try {
                line = text.replaceAll("\n", "").split(";");
                MainLine = text.split(";");
            } catch (Throwable t) {//выделение подстрок по символу ";"
                ActivitySinglePlayer.AlertDialogMessage = "Пожалуйста,проверьте разделение команд по ';'.";
                kodERROR = true;
                return action;
            }
            for (int i = 0; i < line.length; i++) {
                if (kodERROR) break;
                line[i] = line[i].trim();
                if (CHECK_DELIMITOR(line[i], MainLine[i], text)) break;
                int num=1;
                String cOmAnD = line[i];
                if (!cOmAnD.contains("if")&&!cOmAnD.contains("while"))
                    try {
                    if (IS_NUM_EXIST(MainLine[i], line[i]) == 0) break;
                    else num = IS_NUM_EXIST(MainLine[i], line[i]);
                    cOmAnD = cOmAnD.substring(0, line[i].indexOf("(")).trim();
                } catch (Throwable t) {
                    num = 1;
                }
                else {
                    try {
                        cOmAnD = cOmAnD.substring(0, line[i].indexOf("(")).trim();
                    }catch (Throwable t){
                        ActivitySinglePlayer.AlertDialogMessage = "Неправильня форма условия";
                        SELECTOR(MainLine[i],text,symbolslENGTH);
                        kodERROR = true;
                    }
                }
                //проверка на существование КОМАНДЫ ОБНОВЛЯТЬ ПО ДОБАВЛЕНИЮ НОВЫХ КОМАНД!
                if (IS_COMAND_EXIST(cOmAnD, MainLine[i], text)) break;
                if (!pause) i += executor(cOmAnD, num, i, MainLine,text);//исполнитель
                if (!loop) symbolslENGTH += MainLine[i].length() + 1;
                else loop = false;
            }
        } catch (Throwable t) {
            ActivitySinglePlayer.AlertDialogMessage = "Пожалуйста, проверьте правильность написания команд.";
            kodERROR = true;
        }
        symbolslENGTH = 0;
        return action;
    }


    private int executor(String cOmAnD, int num, int Element, String MainLine[],String text) {
        int LOOP_JUMP = 0;
        loop = false;
        for (int g = 0; g < num; g++) {
            switch (cOmAnD) {
                case "up":
                    if (y != 0 && !IS_LAVA(-1, 0)) y--;
                    else if (!pause) {
                        pause = true;
                        if (IS_LAVA(-1,0))
                            ActivitySinglePlayer.AlertDialogMessage = "Вверху лава!!!.";
                        else ActivitySinglePlayer.AlertDialogMessage = "Вверху клеток нет.";
                    }
                    break;
                case "down":
                    if (y != square.length - 1 && !IS_LAVA(1, 0)) y++;
                    else if (!pause) {
                        pause = true;
                        if (IS_LAVA(1,0))
                            ActivitySinglePlayer.AlertDialogMessage = "Внизу лава!!!.";
                        ActivitySinglePlayer.AlertDialogMessage = "Внизу клеток нет.";
                    }
                    break;
                case "right":
                    if (x != square[0].length - 1 && !IS_LAVA(0, 1)) x++;
                    else if (!pause) {
                        pause = true;
                        if (IS_LAVA(0,1))
                            ActivitySinglePlayer.AlertDialogMessage = "Справа лава!!!.";
                        ActivitySinglePlayer.AlertDialogMessage = "Справа клеток нет.";
                    }
                    break;
                case "left":
                    if (x != 0 && !IS_LAVA(0, -1)) x--;
                    else if (!pause) {
                        pause = true;
                        if (IS_LAVA(0,-1))
                            ActivitySinglePlayer.AlertDialogMessage = "Слева лава!!!.";
                        ActivitySinglePlayer.AlertDialogMessage = "Слева клеток нет.";
                    }
                    break;
                case "repeat":
                    LOOP_JUMP = idetifyBODY(MainLine, Element,text);
                    if (!kodERROR) {
                        if(g==0)symbolslENGTH +=
                                MainLine[Element].substring(0, MainLine[Element].indexOf("{") + 1).length();
                        LOOP(Element, MainLine,text);
                    }
                    loop = true;
                    break;
                case "if":
                    LOOP_JUMP = IEidetifyBODY( MainLine, Element,text);
                    if (!kodERROR) {
                        if(g==0)symbolslENGTH +=
                                MainLine[Element].substring(0, MainLine[Element].indexOf("{") + 1).length();
                        DISTRIBUTOR();
                    }
                    loop = true;
                    break;
                case "while":
                    LOOP_JUMP = IEidetifyBODY( MainLine, Element,text);
                    String condition=MainLine[Element].substring(MainLine[Element].indexOf("(")+1,MainLine[Element].indexOf(")"));
                   while (CONDITION_CHECKING(condition)){
                       if (!kodERROR) {
                           if(g==0)symbolslENGTH +=
                                   MainLine[Element].substring(0, MainLine[Element].indexOf("{") + 1).length();
                           if (!pause)
                               LOOP(Element, MainLine,text);
                           else break;
                       }
                       else
                           break;
                   }
                    loop = true;
                    break;
                default:
                    break;
            }
            if (loop) continue;
            if (pause) {
                SELECTOR(MainLine[Element], text, symbolslENGTH);
                break;
            }
            if (action==ARx.length-1){
                ActivitySinglePlayer.AlertDialogMessage = ("Привышен лимит команд!");
                kodERROR = true;
            }
            ARx[action] = x;
            ARy[action] = y;
            ComandName[action] = cOmAnD;
            action++;
        }
        return LOOP_JUMP;
    }


    //-----------------------------------------------------------------------------------------------------------------------------------
    //проверки
    private boolean CONDITION_CHECKING(String text){
        boolean result=false;
        text=text.trim();
        int dX=0,dY=0;//направление проверки
        String dir_text;
        try {
            dir_text=text.substring(0,text.indexOf("_"));
            switch (dir_text){
                case ("up"):dY=-1;dX=0;
                    break;
                case ("down"):dY=1;dX=0;
                    break;
                case ("right"):dY=0;dX=1;
                    break;
                case ("left"):dY=0;dX=-1;
            }
            if(text.substring(text.indexOf("_")+1).equals("wall"))
                result=IS_LAVA(dY,dX);
        }
        catch (Throwable t){
            ActivitySinglePlayer.AlertDialogMessage = "Неправильная форма условия:\n" + text;
            kodERROR = true;
        }
        return result;
    }
    private boolean CHECK_DELIMITOR(String line, String OriginLine, String text) {
        if (line.equals("")) {
            ActivitySinglePlayer.AlertDialogMessage = "Лишний знак ';'";
            SELECTOR(OriginLine, text, symbolslENGTH);
            kodERROR = true;
            return true;
        }
        //добавить if wile
        else if (!line.contains("repeat")&&!line.contains("if")&&!line.contains("while")
                &&line.contains("(")&&line.contains(")")&&!line.endsWith(")")){
            ActivitySinglePlayer.AlertDialogMessage = "После:\n"+line.substring(0,line.indexOf(")")+1)+"\nОжидается знак ';'.";
            SELECTOR(OriginLine, text, symbolslENGTH);
           stop = symbolslENGTH+OriginLine.indexOf(")")+1;
            kodERROR=true;
           return true;
        }
        else return false;
    }

    private boolean IS_COMAND_EXIST(String cOmAnD, String OriginLine, String text) {
        if (!cOmAnD.equals("up") && !cOmAnD.equals("down")
                && !cOmAnD.equals("left") && !cOmAnD.equals("right")
                && !cOmAnD.equals("repeat")  && !cOmAnD.equals("if")
                && !cOmAnD.equals("while")) {
            ActivitySinglePlayer.AlertDialogMessage = "Неизвестная команда:\n" + cOmAnD;
            SELECTOR(OriginLine, text, symbolslENGTH);
            kodERROR = true;
            return true;
        } else return false;
    }

    private int IS_NUM_EXIST(String OriginLine, String line) {
        int num;
        try {
            num = Integer.valueOf(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
            return num;
        } catch (NumberFormatException e) {
            ActivitySinglePlayer.AlertDialogMessage = "Нет такого числа.";
            if (OriginLine.contains("("))
                start = symbolslENGTH + OriginLine.indexOf("(");
            else start = symbolslENGTH + line.length() - 1;
            if (OriginLine.contains(")"))
                stop = symbolslENGTH + OriginLine.indexOf(")") + 1;
            else stop = symbolslENGTH + OriginLine.length() + 1;
            kodERROR = true;
            return 0;
        }
    }

    private boolean IS_LAVA(int dY, int dX) {
        boolean is_LAVA=false;
        switch (dX) {
            case (-1):
                dX = this.x - 1;
                break;
            case (1):
                dX = this.x + 1;
                break;
            default:
                dX = this.x;
                break;
        }
        switch (dY) {
            case (-1):
                dY = this.y - 1;
                break;
            case (1):
                dY = this.y + 1;
                break;
            default:
                dY = this.y;
                break;
        }
        if (dY<0 || dY>square.length-1){
            is_LAVA=true;
        }
        else if (dX<0 || dX>square[dY].length-1){
            is_LAVA=true;
        }
        else if (square[dY][dX].ID_NUMBER == 1) {
            is_LAVA = true;
        }
        return (is_LAVA);
    }


    //-----------------------------------------------------------------------------------------------------------------------------------
    //ЦИКЛ
    private void LOOP(int LoopElement, String MainLine[],String text) {
        int index = MainLine[LoopElement].replaceAll(" ", "").indexOf(")");
        String test = MainLine[LoopElement].replaceAll(" ", "").substring(index, index + 2);
        if (!test.equals("){")) {
            ActivitySinglePlayer.AlertDialogMessage = ("Пропущен ОТКРЫВАЮЩИЙ тег!");
            SELECTOR(MainLine[LoopElement], text, symbolslENGTH);
            kodERROR = true;
        }

        String LoopComands = "";
        int length = idetifyBODY(MainLine, LoopElement,text);
        LoopComands += MainLine[LoopElement].substring(MainLine[LoopElement].indexOf("{") + 1, MainLine[LoopElement].length()) + ";";
        for (int g = 1; g < length; g++) {
            LoopComands += MainLine[LoopElement + g] + ";";
        }
        if (!kodERROR) kodParser(LoopComands);
    }

    private int idetifyBODY(String MainLine[], int LoopElement,String text) {
        boolean enLOOP_EXIST = false;
        String line[] = new String[MainLine.length - LoopElement];
        line[0] = MainLine[LoopElement].substring(MainLine[LoopElement].indexOf("{") + 1, MainLine[LoopElement].length());
        System.arraycopy(MainLine, LoopElement + 1, line, 1, MainLine.length - LoopElement - 1);
        //выдает ошибку
        for (int i = 0; i < line.length; i++) {
            if (line[i].contains("}") && !enLOOP_EXIST) return i;
            if (line[i].contains("{")) enLOOP_EXIST = true;
            else if (line[i].contains("}")) enLOOP_EXIST = false;
        }
        ActivitySinglePlayer.AlertDialogMessage = ("Провущен ЗАКРЫВАЮЩИЙ тег!");
        SELECTOR(MainLine[LoopElement], text, symbolslENGTH);
        kodERROR = true;

        return -1;
    }
    //-----------------------------------------------------------------------------------------------------------------------------------
    //IF-ELSE
    private ArrayList<Integer> elsenum=new ArrayList<>();//номера елементов основного массива, содержащих IF и/или ELSE
    private String IEMainLine[];

    //определяет "прыжок" для основного парсера команд и создает метки елементов с IF/ELSE
    private int IEidetifyBODY(String MainLine[],int LoopElement,String text){
        //---------------------------
        elsenum.clear();
        int elnum = 0;
        IEMainLine=MainLine;
        //обнуление
        elsenum.add(LoopElement);
        elnum++;
        elsenum.add(idetifyBODY(MainLine,elsenum.get(elnum -1),text) +elsenum.get(elnum -1));
        while (!MainLine[elsenum.get(elnum)].replaceAll("\n","").equals("}")){
            if (IS_ELSE_OKAY(MainLine[elsenum.get(elnum)])){
                elnum++;
                elsenum.add(idetifyBODY(MainLine,elsenum.get(elnum -1),text)+elsenum.get(elnum -1));
            }
            else {
                ActivitySinglePlayer.AlertDialogMessage = "ОЖИДАЕТСЯ ELSE или ;\n"+MainLine[elsenum.get(elnum)];
                SELECTOR(MainLine[LoopElement], this.text, symbolslENGTH);
                kodERROR = true;
                return -1;
            }
        }
        return elsenum.get(elnum)-LoopElement;
    }


    private void DISTRIBUTOR(){
        for (int i=0;i<elsenum.size();i++){
            String line=IEMainLine[elsenum.get(i)];
            if (IS_IF_OKAY(line)){
                try{
                    String text = line.substring(line.indexOf("(")+1,line.indexOf(")"));
                    if(CONDITION_CHECKING(text)){
                        String IF_ELSE_COMANDS=line.substring(line.indexOf("{")+1)+";";
                        for (int g=elsenum.get(i)+1;g<elsenum.get(i+1);g++)
                            IF_ELSE_COMANDS += IEMainLine[g] + ";";
                        kodParser(IF_ELSE_COMANDS);
                        break;
                    }
                }catch (Throwable t){
                    ActivitySinglePlayer.AlertDialogMessage = "непраильная форма условия";
                    break;
                }
            }
            else if (i!=elsenum.size()-1){
                String IF_ELSE_COMANDS=line.substring(line.indexOf("{")+1)+";";
                for (int g=elsenum.get(i)+1;g<elsenum.get(i+1);g++)
                    IF_ELSE_COMANDS += IEMainLine[g] + ";";
                kodParser(IF_ELSE_COMANDS);
                break;
            }
        }
    }

    private  boolean IS_ELSE_OKAY(String line){
        boolean result=false;
        if (line.contains("if")){
            if (line.substring(line.indexOf("}")+1,line.indexOf("i")).trim().equals("else"))
                result = true;
            else {
                ActivitySinglePlayer.AlertDialogMessage = "непраильная форма условия"+line;
                System.out.println("неизвестная команда");
            }
        }
        else {
            if (line.substring(line.indexOf("}")+1,line.indexOf("{")).trim().equals("else"))
                result = true;
            else {
                ActivitySinglePlayer.AlertDialogMessage = "непраильная форма условия"+line;
                System.out.println("неизвестная команда");
            }
        }
        return result;
    }
    private  boolean IS_IF_OKAY(String line){
        boolean result=false;
        if (line.contains("(")) {
            if (line.contains("else")) {
                if (line.substring(line.indexOf("else") + 4, line.indexOf("(")).trim().equals("if"))
                    result = true;
            }
            else if (line.substring(0, line.indexOf("(")).trim().equals("if"))
                result = true;
        }
        return result;
    }

    //-------------------------------------------------------------------------------------------------------







    private void SELECTOR(String OriginLine, String text, int symbolslENGTH) {
        if (OriginLine.equals("")) start = symbolslENGTH;
        else if (OriginLine.substring(0, 1).equals("\n"))
            start = symbolslENGTH + 1;
        else start = symbolslENGTH;
        if (symbolslENGTH + OriginLine.length() + 1 > text.length())
            stop = symbolslENGTH + OriginLine.length();
        else stop = symbolslENGTH + OriginLine.length() + 1;
    }

     void setAction(int action) {
        this.action = action;
    }

     void setKodERROR(boolean kodERROR) {
        this.kodERROR = kodERROR;
    }
    boolean isKodERROR() {
        return kodERROR;
    }
     boolean isPause() {
        return pause;
    }
}

