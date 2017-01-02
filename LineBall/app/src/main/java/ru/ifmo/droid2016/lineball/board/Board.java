package ru.ifmo.droid2016.lineball.board;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
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

    public Board(int maxX, int maxY, int color) {
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
    }


    public Who check() {

        if (b1.outOfBoard(maxX, maxY)) {
            Log.e("CHECK:", "we out of board");
            return Who.RIVAL;
        }

        if (b2.outOfBoard(maxX, maxY)) {
            Log.e("CHECK:", "rival out of board");
            return Who.THIS_USER;
        }

        for (Wall wall : walls1) {
            boolean rotate1 = b1.collision(wall, false);
            boolean rotate2 = b2.collision(wall, false);

            if (rotate1 && rotate2) {
                Log.e("CHECK:", "balls hit blue wall");
                b1.rotate(wall);
                b2.rotate(wall);
                b1.v -= dv;
                b2.v += dv;
                walls1.remove(wall);
                break;
            }

            if (rotate1) {
                Log.e("CHECK:", "blue hits blue wall");
                b1.rotate(wall);
                b1.v -= dv;
                if (--wall.k == 0) {
                    walls1.remove(wall);
                }
                break;
            }

            if (rotate2) {
                Log.e("CHECK:", "red hits blue wall");
                b2.rotate(wall);
                b2.v += dv;
                walls1.remove(wall);
                break;
            }
        }

        for (Wall wall : walls2) {
            boolean rotate1 = b1.collision(wall, false);
            boolean rotate2 = b2.collision(wall, false);

            if (rotate1 && rotate2) {
                Log.e("CHECK:", "balls hit red wall");
                b1.rotate(wall);
                b2.rotate(wall);
                b1.v += dv;
                b2.v -= dv;
                walls1.remove(wall);
                break;
            }

            if (rotate1) {
                Log.e("CHECK:", "blue hits red wall");
                b1.rotate(wall);
                b1.v += dv;
                walls2.remove(wall);
                break;
            }

            if (rotate2) {
                Log.e("CHECK:", "red hits red wall");
                b2.rotate(wall);
                b2.v -= dv;
                if (--wall.k == 0) {
                    walls2.remove(wall);
                }
                break;
            }
        }


        if (b1.v < 1)
            return Who.RIVAL;

        if (b2.v < 1)
            return Who.THIS_USER;

        if (b1.collision(b2)) {
            if (b1.v > b2.v) {
                Log.e("CHECK:", "we are faster");
                return Who.THIS_USER;
            } else if (b2.v > b1.v) {
                Log.e("CHECK:", "we are slower");
                return Who.RIVAL;
            } else if (Math.abs(b1.v - b2.v) < eps) {
                Log.e("CHECK:", "need to reverse");
                Point t = b1.dir;
                b1.dir = b2.dir;
                b2.dir = t;
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
        for (int i = 0; i < s.length; ++i) {
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
                w.reverse();
                walls2.add(w);
                break;
        }
    }

    public void drawBoard(Canvas canvas) {
        b1.onDraw(canvas);
        for (Wall wall : walls1) {
            wall.onDraw(canvas, thisUserPaint);
        }
        b2.onDraw(canvas);
        for (Wall wall : walls2) {
            wall.onDraw(canvas, rivalPaint);
        }
    }
}