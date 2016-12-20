package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

import java.util.ArrayList;

//TODO all
public class Board {
    double dv = 1, maxX = 1024, maxY = 1024;
    ArrayList<Wall> walls1, walls2;
    private Ball b1, b2;


    public Who check() {
        if (b1.outOfBoard(maxX, maxY)) {
            return Who.RIVAL;
        }

        if (b2.outOfBoard(maxX, maxY)) {
            return Who.THIS_USER;
        }

        if (b1.collision(b2)) {
            if (b1.v > b2.v)
                return Who.THIS_USER;
            else if (b2.v > b1.v)
                return Who.RIVAL;
            else if (b1.v == b2.v) {
                b1.dir.mul(-1);
                b2.dir.mul(-1);
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
        return null;
    }

    public void setWall(String coord, @NonNull Who from) {
        //TODO
    }

    public void drawBoard(Canvas canvas) {
        b1.onDraw(canvas);
        b2.onDraw(canvas);
        for (Wall wall : walls1) {
            wall.onDraw(canvas);
        }
        for (Wall wall : walls2) {
            wall.onDraw(canvas);
        }
    }
}
