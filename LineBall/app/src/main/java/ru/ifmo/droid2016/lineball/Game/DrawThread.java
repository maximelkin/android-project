package ru.ifmo.droid2016.lineball.Game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

import ru.ifmo.droid2016.lineball.Board.Board;

public class DrawThread extends Thread{

    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;
    private Board board;

    public DrawThread(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public void setRunning(boolean run){
        runFlag = run;
    }

    @Override
    public void run(){
        Canvas canvas;
        while (runFlag){
            canvas = null;
            try{
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    canvas.drawColor(Color.WHITE);
                    board.drawBoard();
                }
            } finally {
                if(canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
