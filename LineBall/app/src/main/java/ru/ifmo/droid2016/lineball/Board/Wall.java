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
        canvas.drawLine((float) (p1.x * Board.maxXLocal / Board.maxX), (float) (p1.y * Board.maxYLocal / Board.maxY),
                (float) (p2.x * Board.maxXLocal / Board.maxX), (float) (p2.y * Board.maxYLocal / Board.maxY), p);
    }

    public void toAbsoluteCoord() {
    }

    public void reverse() {
        Point maxCoord = new Point(Board.maxX, Board.maxY);
        p1 = maxCoord.sub(p1);
        p2 = maxCoord.sub(p2);
        l = new Line(p1, p2);
    }
}
