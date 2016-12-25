package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

//TODO all
public class Board {
    //maxX/maxY = 16/9
    public static double dv = 0.2, maxX = 1024, maxY = 576, maxXLocal, maxYLocal, eps = 1e-9;
    private ArrayList<Wall> walls1 = new ArrayList<>(), walls2 = new ArrayList<>();
    private Ball b1 = new Ball(new Point(30, 30), new Point(1 / Math.sqrt(2), 1 / Math.sqrt(2))),
            b2 = new Ball(new Point(maxX - 30, maxY - 30), new Point(-1 / Math.sqrt(2), -1 / Math.sqrt(2)));

    public Board(int maxX, int maxY) {
        maxXLocal = maxX;
        maxYLocal = maxY;
    }


    public Who check() {

        //TODO do all it the correct order
        if (Math.abs(b1.v) < eps)
            return Who.RIVAL;

        if (Math.abs(b2.v) < eps)
            return Who.THIS_USER;

        if (b1.outOfBoard(maxX, maxY)) {
            Log.e("CHECK:", "we out of board");
            return Who.RIVAL;
        }

        if (b2.outOfBoard(maxX, maxY)) {
            Log.e("CHECK:", "rival out of board");
            return Who.THIS_USER;
        }

        for (Wall wall : walls1) {
            boolean rotate1 = b1.collision(wall);
            boolean rotate2 = b2.collision(wall);

            if (rotate1 && rotate2) {
                Log.e("CHECK:", "balls hit blue wall");
                b1.rotate(wall);
                b2.rotate(wall);
                b1.v = Math.max(0, b1.v - dv);
                b2.v += dv;
                walls1.remove(wall);
                break;
            }

            if (rotate1) {
                Log.e("CHECK:", "blue hits blue wall");
                b1.rotate(wall);
                b1.v = Math.max(0, b1.v - dv);
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
            boolean rotate1 = b1.collision(wall);
            boolean rotate2 = b2.collision(wall);

            if (rotate1 && rotate2) {
                Log.e("CHECK:", "balls hit red wall");
                b1.rotate(wall);
                b2.rotate(wall);
                b1.v += dv;
                b2.v = Math.max(0, b2.v - dv);
                ;
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
                b2.v = Math.max(0, b1.v - dv);
                if (--wall.k == 0) {
                    walls2.remove(wall);
                }
                break;
            }
        }

        if (b1.collision(b2)) {
            if (b1.v > b2.v) {
                Log.e("CHECK:", "we are faster");
                return Who.THIS_USER;
            } else if (b2.v > b1.v) {
                Log.e("CHECK:", "we are slower");
                return Who.RIVAL;
            } else if (Math.abs(b1.v - b2.v) < eps) {
                Log.e("CHECK:", "need to reverse");
                b1.dir = b1.dir.mul(-1);
                b2.dir = b2.dir.mul(-1);
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

        Point p1 = (new Point(a[0], a[1])).mul(maxXLocal / maxX), p2 = (new Point(a[2], a[3])).mul(maxYLocal / maxY);
        Wall w = new Wall(p1, p2, new Line(p1, p2));

        if (b1.collision(w) || b2.collision(w))
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
        Paint p = new Paint();
        p.setStrokeWidth(10);
        p.setColor(Color.BLUE);

        b1.onDraw(canvas, p);
        for (Wall wall : walls1) {
            wall.onDraw(canvas, p);
        }

        p.setColor(Color.RED);
        b2.onDraw(canvas, p);
        for (Wall wall : walls2) {
            wall.onDraw(canvas, p);
        }
    }
}
