package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

//TODO all
public class Board {
    double dv = 1, maxX = 1024, maxY = 1024;
    ArrayList<Wall> walls1 = new ArrayList<>(), walls2 = new ArrayList<>();
    private Ball b1 = new Ball(new Point(30, 30), new Point(1 / Math.sqrt(2), 1 / Math.sqrt(2))),
            b2 = new Ball(new Point(300, 300), new Point(- 1 / Math.sqrt(2), - 1 / Math.sqrt(2)));


    public Who check() {
        if (b1.outOfBoard(maxX, maxY)) {
            Log.e("CHECK:", "we out of board");
            return Who.RIVAL;
        }

        if (b2.outOfBoard(maxX, maxY)) {
            Log.e("CHECK:", "rival out of board");
            return Who.THIS_USER;
        }

        if (b1.collision(b2)) {
            if (b1.v > b2.v) {
                Log.e("CHECK:", "we are faster");
                return Who.THIS_USER;
            } else if (b2.v > b1.v) {
                Log.e("CHECK:", "we are slower");
                return Who.RIVAL;
            } else if (b1.v == b2.v) {
                Log.e("CHECK:", "need to reverse");
                b1.dir = b1.dir.mul(-1);
                b2.dir = b2.dir.mul(-1);
                b1.move();
                b2.move();
                return null;
            }
        }
        for (Wall wall : walls1) {
            if (b1.collision(wall)) {
                b1.rotate(wall);
                b1.v -= dv;
                if (--wall.k == 0) {
                    walls1.remove(wall);
                }
                break;
            }
        }

        for (Wall wall : walls2) {
            if (b1.collision(wall)) {
                b1.rotate(wall);
                b1.v += dv;
                walls2.remove(wall);
                break;
            }
        }

        for (Wall wall : walls1) {
            if (b2.collision(wall)) {
                b2.rotate(wall);
                b2.v += dv;
                walls1.remove(wall);
                break;
            }
        }

        for (Wall wall : walls2) {
            if (b2.collision(wall)) {
                b2.rotate(wall);
                b2.v -= dv;
                if (--wall.k == 0) {
                    walls2.remove(wall);
                }
                break;
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

        Point p1 = new Point(a[0], a[1]), p2 = new Point(a[2], a[3]);
        Wall w = new Wall(p1, p2, new Line(p1, p2));

        switch (from) {
            case THIS_USER:
                walls1.add(w);
                break;
            case RIVAL:
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
