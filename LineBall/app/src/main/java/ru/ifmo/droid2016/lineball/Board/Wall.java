package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Wall {
    Point p1, p2;
    Line l;
    int k = 2;

    Wall(Point p1, Point p2, Line l){
        this.p1 = p1;
        this.p2 = p2;
        this.l = l;
    }

    public void onDraw(Canvas canvas)
    {
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStrokeWidth(10);
        canvas.drawLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, p);
    }
}
