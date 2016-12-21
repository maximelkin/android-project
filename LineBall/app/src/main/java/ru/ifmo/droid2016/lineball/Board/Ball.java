package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Ball {

    Point pos;
    Point dir;
    double r = 30;
    double v = 1;
    double eps = 1e-9;

    Ball(Point pos, Point dir) {
        this.pos = pos;
        this.dir = dir;
    }

    Ball(Ball b) {
        this.pos = b.pos;
        this.dir = b.dir;
        this.r = b.r;
        this.v = b.v;
    }

    boolean collision(Ball ball) {
        Ball b1 = new Ball(this);
        Ball b2 = new Ball(ball);
        Point dist = b2.pos.sub(b1.pos);
        if (dist.length() < 2 * r) {
            return true;
        } else {
            return false;
        }
    }

    boolean collision(Wall wall) {
        Ball b1 = new Ball(this);
        Wall w1 = new Wall(wall);

        Point m = (new Point(b1.pos)).sub(w1.p2);
        Point p = (new Point(b1.pos)).sub(w1.p1);
        Point q = new Point(w1.p2.sub(w1.p1));
        Point qr = new Point(q.mul(-1));
        double d = w1.l.dist(b1.pos);

        if (m.sp(qr) * p.sp(q) >= 0) {
            Log.e("COLLISION", "distance to line");
            return (d <= r);
        } else {
            Log.e("COLLISION", "distance to points");
            return (m.length() <= r || p.length() <= r);
        }
    }

    public void rotate(Wall w) {
        //TODO Just do it OK
        Point n = new Point(-w.l.B, w.l.A);
        double angle = 2 * Math.atan2(dir.cp(n), dir.sp(n));
        double x1 = dir.x * Math.cos(angle) - dir.y * Math.sin(angle);
        double y1 = dir.y * Math.cos(angle) + dir.x * Math.sin(angle);
        dir = new Point(x1, y1);
        dir = dir.mul(1 / dir.length());
    }

    public boolean outOfBoard(double mX, double mY) {
        return (pos.x < 0 || pos.x > mX || pos.y < 0 || pos.y > mY);
    }

    public void onDraw(Canvas canvas, Paint p) {
        //TODO convert coordinates
        canvas.drawCircle((float) pos.x, (float) pos.y, (float) r, p);
    }

    public void move() {
        pos = pos.sum(dir.mul(v));
    }
}
