package ru.ifmo.droid2016.lineball.board;

import android.graphics.Canvas;
import android.graphics.Paint;

import static ru.ifmo.droid2016.lineball.board.Board.*;

class Wall {
    Point p1, p2;
    Line line;
    int hitPoints;

    Wall(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.line = new Line(p1, p2);
        this.hitPoints = 2;
    }

    void onDraw(Canvas canvas, Paint paint, boolean isGameMaster) {
        if (isGameMaster) {
            canvas.drawLine(
                    p1.x * maxXLocal / maxX, p1.y * maxYLocal / maxY,
                    p2.x * maxXLocal / maxX, p2.y * maxYLocal / maxY,
                    paint);
        } else {
            canvas.drawLine(
                    maxXLocal - p1.x * maxXLocal / maxX, maxYLocal - p1.y * maxYLocal / maxY,
                    maxXLocal - p2.x * maxXLocal / maxX, maxYLocal - p2.y * maxYLocal / maxY,
                    paint);
        }

    }
}
