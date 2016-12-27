package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Wall {
    Point p1, p2;
    Line l;
    int k;

    Wall(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.l = new Line(p1, p2);
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

    public void reverse() {
        Point maxCoord = new Point(Board.maxX, Board.maxY);
        p1.sub(maxCoord).mul(-1);
        p2.sub(maxCoord).mul(-1);
        l = new Line(p1, p2);
    }
}
