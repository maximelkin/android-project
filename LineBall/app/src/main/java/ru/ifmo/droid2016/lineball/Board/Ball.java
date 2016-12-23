package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.util.Log;

public class Ball {

    Point pos;
    Point dir;
    double r;
    double v;
    double eps = 1e-9;

    Ball(Point pos, Point dir) {
        this.pos = pos;
        this.dir = dir;
        this.r = 30;
        this.v = 2;
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

        Point nextPos = new Point(b1.pos.sum(b1.dir));

        double s1 = w1.p1.sub(nextPos).cp(w1.p2.sub(w1.p1));
        double s2 = w1.p2.sub(nextPos).cp(b1.pos.sub(w1.p2));
        double s3 = b1.pos.sub(nextPos).cp(w1.p1.sub(b1.pos));

        if (s1 * s2 < 0 || s1 * s3 < 0 || s2 * s3 < 0)
            return false;
        
        Point m = (new Point(b1.pos)).sub(w1.p2);
        Point p = (new Point(b1.pos)).sub(w1.p1);
        Point q = new Point(w1.p2.sub(w1.p1));
        Point qrev = new Point(q.mul(-1));
        double d = w1.l.dist(b1.pos);

        if (m.sp(qrev) * p.sp(q) >= 0) {
            return (Math.abs(d) <= r);
        } else {
            return (m.length() <= r || p.length() <= r);
        }
    }

    public void rotate(Wall w) {
        rotateDirection(w);
        /*//TODO Just do it OK

        Point p = new Point(w.p1.sum(dir));
        Point n = new Point(w.l.A, w.l.B);
        double d = w.l.dist(p);
        n = n.mul(d / n.length());
        p = p.sum(n);

        if (w.l.contain(p)) {
            p = p.sum(n);
        } else {
            n = n.mul(-3);
            p = p.sum(n);
        }
        dir = p.sub(w.p1);
        dir = dir.mul(1 / dir.length());*/
    }

    public void rotateDirection(Wall wall) {
        Point wallNormal = new Point(wall.l.A, wall.l.B).normalize();
        dir = dir.sum(wallNormal.mul(-dir.sp(wallNormal) * 2)).normalize();
    }

    public boolean outOfBoard(double mX, double mY) {
        return (pos.x < -r || pos.x > mX + r || pos.y < -r || pos.y > mY + r);
    }

    public void onDraw(Canvas canvas, Paint p) {
        //TODO convert coordinates
        canvas.drawCircle((float) pos.x, (float) pos.y, (float) r, p);
    }

    public void move() {
        pos = pos.sum(dir.mul(v));
    }
}
