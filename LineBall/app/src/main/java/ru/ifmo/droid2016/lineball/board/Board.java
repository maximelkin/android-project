package ru.ifmo.droid2016.lineball.board;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class Board {
    //maxX/maxY = 9/16
    public static double dv = 0.5, maxX = 576, maxY = 1024, maxXLocal, maxYLocal, eps = 1e-2;
    private final Paint thisUserPaint;
    private final Paint rivalPaint;
    private List<Wall> walls1 = new LinkedList<>(), walls2 = new LinkedList<>();
    private Ball b1,
            b2;
    private final boolean isGameMaster;

    public Board(int maxX, int maxY, int color, boolean isGameMaster) {
        maxXLocal = maxX;
        maxYLocal = maxY;
        thisUserPaint = new Paint();
        thisUserPaint.setStrokeWidth(10);
        thisUserPaint.setAntiAlias(true);

        rivalPaint = new Paint();
        rivalPaint.setStrokeWidth(10);
        rivalPaint.setAntiAlias(true);

        thisUserPaint.setColor((color == 0) ? Color.BLUE : Color.RED);
        rivalPaint.setColor((color == 0) ? Color.RED : Color.BLUE);
        b1 = new Ball(new Point(30, 30), new Point(1 / Math.sqrt(2), 1 / Math.sqrt(2)),
                thisUserPaint);
        b2 = new Ball(new Point(Board.maxX - 30, Board.maxY - 30), new Point(-1 / Math.sqrt(2), -1 / Math.sqrt(2)),
                rivalPaint);
        if (isGameMaster) {
            Ball temp = b1;
            b1 = b2;
            b2 = temp;
        }
        this.isGameMaster = isGameMaster;
    }


    @Nullable
    public Who check() {

        if (b1.outOfBoard()) {
            Log.e("CHECK:", "we out of board");
            return Who.RIVAL;
        }

        if (b2.outOfBoard()) {
            Log.e("CHECK:", "rival out of board");
            return Who.THIS_USER;
        }

        for (Wall wall : walls1) {
            boolean rotate1 = b1.collision(wall, false);
            boolean rotate2 = b2.collision(wall, false);

            if (rotate1) {
                Log.e("CHECK:", "blue hits blue wall");
                b1.rotate(wall);
                b1.speed -= dv;
                wall.hitPoints--;
                break;
            }

            if (rotate2) {
                Log.e("CHECK:", "red hits blue wall");
                b2.rotate(wall);
                b2.speed += dv;
                wall.hitPoints -= 2;
                break;
            }

            if (wall.hitPoints <= 0)
                walls1.remove(wall);
        }

        for (Wall wall : walls2) {
            boolean rotate1 = b1.collision(wall, false);
            boolean rotate2 = b2.collision(wall, false);

            if (rotate1) {
                Log.e("CHECK:", "blue hits red wall");
                b1.rotate(wall);
                b1.speed += dv;
                wall.hitPoints -= 2;
                break;
            }

            if (rotate2) {
                Log.e("CHECK:", "red hits red wall");
                b2.rotate(wall);
                b2.speed -= dv;
                wall.hitPoints--;
                break;
            }
            if (wall.hitPoints <= 0)
                walls2.remove(wall);
        }


        if (b1.speed < 4)
            return Who.RIVAL;

        if (b2.speed < 4)
            return Who.THIS_USER;

        if (b1.collision(b2)) {
            if (b1.speed > b2.speed + eps) {
                Log.e("CHECK:", "we are faster");
                return Who.THIS_USER;

            } else if (b2.speed > b1.speed + eps) {
                Log.e("CHECK:", "we are slower");
                return Who.RIVAL;

            } else {
                Log.e("CHECK:", "need to reverse");
                //swap directions
                Point temp = b1.direction;
                b1.direction = b2.direction;
                b2.direction = temp;
                b1.move();
                b2.move();
                return null;
            }
        }

        b1.move();
        b2.move();
        return null;
    }

    public void setWall(String coord, @NonNull Who from) {
        String[] s = coord.split(" ");
        double[] a = new double[s.length];
        for (int i = 0; i < s.length; i++) {
            a[i] = Double.parseDouble(s[i]);
        }

        Point p1 = new Point(a[0], a[1]),
                p2 = new Point(a[2], a[3]);
        Wall w = new Wall(p1, p2);

        if (b1.collision(w, true) || b2.collision(w, true))
            return;

        Log.e("Board:", "wall added");

        switch (from) {
            case THIS_USER:
                walls1.add(w);
                break;
            case RIVAL:
                //w.reverse();
                walls2.add(w);
                break;
        }
    }

    public void drawBoard(Canvas canvas) {
        b1.onDraw(canvas, isGameMaster);
        for (Wall wall : walls1) {
            wall.onDraw(canvas, thisUserPaint, isGameMaster);
        }

        b2.onDraw(canvas, isGameMaster);
        for (Wall wall : walls2) {
            wall.onDraw(canvas, rivalPaint, isGameMaster);
        }
    }
}
