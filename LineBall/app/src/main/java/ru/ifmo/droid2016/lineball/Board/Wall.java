package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Wall {
    Point p1, p2;
    Line l;
    int k;

    Wall(Point p1, Point p2, Line l) {
        this.p1 = p1;
        this.p2 = p2;
        this.l = l;
        this.k = 2;
    }

    Wall(Wall wall) {
        this.p1 = wall.p1;
        this.p2 = wall.p2;
        this.l = wall.l;
        this.k = wall.k;
    }

    public void onDraw(Canvas canvas, Paint p) {
        //TODO convert coordinates
        canvas.drawLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, p);
    }
}
