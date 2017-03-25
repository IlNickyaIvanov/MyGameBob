package com.example.a1.mygame;

class KodParser {
    private Square square[][];
    int action;
    int x, y;//положение робота

    private boolean pause;

    private boolean kodERROR;//пауза для ошибок

    private boolean loop = false;
    private static int symbolslENGTH;
    int start, stop;
    int ARx[] = new int[100];//массивы пошаговых
    int ARy[] = new int[100];//координат положения робота
    String ComandName[] = new String[100];//команды
    KodParser(int StartX, int StartY, Square square[][]) {
        this.square = square;
        x = StartX;
        y = StartY;
    }

    int kodParser(String text) {
        pause = false;
        kodERROR = false;
        start = 0;
        stop = 0;
        if (text.replaceAll("\n", "").replaceAll(";", "").isEmpty()) {
            ActivitySinglePlayer.AlertDialogMessage = "А где сами команды?";
            pause = true;
            kodERROR = true;
        } else try {
            String[] line;
            String[] MainLine;
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
                int num;
                String cOmAnD = line[i];
                try {
                    if (IS_NUM_EXIST(MainLine[i], line[i]) == 0) break;
                    else num = IS_NUM_EXIST(MainLine[i], line[i]);
                    cOmAnD = cOmAnD.substring(0, line[i].indexOf("(")).trim();
                } catch (Throwable t) {
                    num = 1;
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
                        ActivitySinglePlayer.AlertDialogMessage = "Вверху клеток нет.";
                    }
                    break;
                case "down":
                    if (y != square.length - 1 && !IS_LAVA(1, 0)) y++;
                    else if (!pause) {
                        pause = true;
                        ActivitySinglePlayer.AlertDialogMessage = "Внизу клеток нет.";
                    }
                    break;
                case "right":
                    if (x != square[0].length - 1 && !IS_LAVA(0, 1)) x++;
                    else if (!pause) {
                        pause = true;
                        ActivitySinglePlayer.AlertDialogMessage = "Справа клеток нет.";
                    }
                    break;
                case "left":
                    if (x != 0 && !IS_LAVA(0, -1)) x--;
                    else if (!pause) {
                        pause = true;
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
                default:
                    break;
            }
            if (loop) continue;
            if (pause) {
                SELECTOR(MainLine[Element], text, symbolslENGTH);
                break;
            }
            ARx[action] = x;
            ARy[action] = y;
            ComandName[action] = cOmAnD;
            action++;
        }
        return LOOP_JUMP;
    }

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
        for (int g = 1; g < MainLine.length - LoopElement; g++)
            line[g] = MainLine[LoopElement + g];
        //System.arraycopy(MainLine,LoopElement+1,line,1,MainLine.length-LoopElement);
        //выдает ошибку
        for (int i = 0; i < line.length; i++) {
            if (line[i].contains("{")) enLOOP_EXIST = true;
            if (line[i].contains("}") && !enLOOP_EXIST) {
                return i;
            } else if (line[i].contains("}")) enLOOP_EXIST = false;
        }
        ActivitySinglePlayer.AlertDialogMessage = ("Провущен ЗАКРЫВАЮЩИЙ тег!");
        SELECTOR(MainLine[LoopElement], text, symbolslENGTH);
        kodERROR = true;

        return -1;
    }

    private boolean CHECK_DELIMITOR(String line, String OriginLine, String text) {
        if (line.equals("")) {
            ActivitySinglePlayer.AlertDialogMessage = "Лишний знак ';'";
            SELECTOR(OriginLine, text, symbolslENGTH);
            kodERROR = true;
            return true;
        }
        else if (!line.contains("repeat")&&line.contains("(")&&line.contains(")")&&!line.endsWith(")")){
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
                && !cOmAnD.equals("left") && !cOmAnD.equals("right") && !cOmAnD.equals("repeat")) {
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

    private boolean IS_LAVA(int y, int x) {
        switch (x) {
            case (-1):
                x = this.x - 1;
                break;
            case (1):
                x = this.x + 1;
                break;
            default:
                x = this.x;
                break;
        }
        switch (y) {
            case (-1):
                y = this.y - 1;
                break;
            case (1):
                y = this.y + 1;
                break;
            default:
                y = this.y;
                break;
        }
        if (square[y][x].ID_NUMBER == 1) {
            pause = true;
            ActivitySinglePlayer.AlertDialogMessage = "Впереди ЛАВА!";
        }
        return (pause);
    }

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

    public void setKodERROR(boolean kodERROR) {
        this.kodERROR = kodERROR;
    }
    boolean isKodERROR() {
        return kodERROR;
    }
    public boolean isPause() {
        return pause;
    }
}

