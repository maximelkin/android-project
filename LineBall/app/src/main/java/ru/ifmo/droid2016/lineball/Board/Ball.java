package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.graphics.Paint;

import static ru.ifmo.droid2016.lineball.Board.Point.*;

public class Ball {

    private Point pos;
    Point dir;
    private double r;
    double v;

    Ball(Point pos, Point dir) {
        this.pos = pos;
        this.dir = dir;
        this.r = 30;
        this.v = 8;
    }

    private Ball(Ball b) {
        this.pos = b.pos;
        this.dir = b.dir;
        this.r = b.r;
        this.v = b.v;
    }

    boolean collision(Ball ball) {
        Ball b1 = new Ball(this);
        Ball b2 = new Ball(ball);
        Point dist = sub(b2.pos, b1.pos);
        return dist.length() < 2 * r;
    }

    boolean collision(Wall wall, boolean settingWall) {
        Ball b1 = new Ball(this);
        Wall w1 = new Wall(wall);

        Point nextPos = sum(b1.pos, b1.dir);

        Point m = sub(b1.pos, w1.p2);
        Point p = sub(b1.pos, w1.p1);
        Point q = sub(w1.p2, w1.p1);
        Point qrev = multiply(q, -1);
        double d_ball = w1.l.dist(b1.pos);
        double d_nextPos = w1.l.dist(nextPos);
        boolean intersect;

        if (m.scalarProduct(qrev) * p.scalarProduct(q) >= 0) {
            intersect = (Math.abs(d_ball) <= r);
        } else {
            intersect = (m.length() <= r || p.length() <= r);
        }
        if (!intersect)
            return false;

        if (settingWall) {
            return true;
        }

        if (w1.l.contain(pos) && w1.l.contain(nextPos)) {
            dir.mul(-1);
            return false;
        }

        return (Math.abs(d_nextPos) < Math.abs(d_ball));
    }

    void rotate(Wall w) {
        //TODO Just do it OK

        Point p = sum(w.p1, dir);
        Point n = new Point(w.l.A, w.l.B);
        double d = w.l.dist(p);
        n.mul(d / n.length());
        p.add(n);

        if (w.l.contain(p)) {
            p.add(n);
        } else {
            n.mul(-3);
            p.add(n);
        }
        dir = sub(p, w.p1).mul(1 / dir.length());
    }

    boolean outOfBoard(double mX, double mY) {
        return (pos.x < -r || pos.x > mX + r || pos.y < -r || pos.y > mY + r);
    }

    void onDraw(Canvas canvas, Paint p) {
        canvas.drawCircle((float) (pos.x * Board.maxXLocal / Board.maxX),
                (float) (pos.y * Board.maxYLocal / Board.maxY), (float) (r * Board.maxYLocal / Board.maxY), p);
    }

    void move() {
        pos.add(multiply(dir, v));
    }
}
