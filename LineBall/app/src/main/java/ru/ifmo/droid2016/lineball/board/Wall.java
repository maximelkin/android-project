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
                    (float) (p1.x * maxXLocal / maxX), (float) (p1.y * maxYLocal / maxY),
                    (float) (p2.x * maxXLocal / maxX), (float) (p2.y * maxYLocal / maxY),
                    paint);
        } else {
            canvas.drawLine(
                    (float) (maxXLocal - p1.x * maxXLocal / maxX), (float) (maxYLocal - p1.y * maxYLocal / maxY),
                    (float) (maxXLocal - p2.x * maxXLocal / maxX), (float) (maxYLocal - p2.y * maxYLocal / maxY),
                    paint);
        }

    }
}
