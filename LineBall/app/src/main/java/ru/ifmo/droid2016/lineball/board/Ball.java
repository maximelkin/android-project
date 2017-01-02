package ru.ifmo.droid2016.lineball.board;

import android.graphics.Canvas;
import android.graphics.Paint;

import static ru.ifmo.droid2016.lineball.board.Point.*;

public class Ball {

    private final Paint paint;
    private Point pos;
    Point dir;
    private double r;
    double v;

    Ball(Point pos, Point dir, Paint paint) {
        this.pos = pos;
        this.dir = dir;
        this.r = 30;
        this.v = 8;
        this.paint = paint;
    }

    boolean collision(Ball ball) {
        return sub(ball.pos, pos).length() < 2 * r;
    }

    boolean collision(Wall wall, boolean settingWall) {

        Point m = sub(pos, wall.p2);
        Point p = sub(pos, wall.p1);
        Point q = sub(wall.p2, wall.p1);
        double d_ball = wall.l.dist(pos);
        boolean intersect;

        if (m.scalarProduct(q) * p.scalarProduct(q) <= 0) {
            intersect = (Math.abs(d_ball) <= r);
        } else {
            intersect = (m.length() <= r || p.length() <= r);
        }
        if (!intersect)
            return false;

        if (settingWall) {
            return true;
        }
        Point nextPos = sum(pos, dir);

        if (wall.l.contain(pos) || wall.l.contain(nextPos)) {
            dir.mul(-1);
            return false;
        }

        double d_nextPos = wall.l.dist(nextPos);

        return (Math.abs(d_nextPos) < Math.abs(d_ball));
    }

    void rotate(Wall w) {

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

    void onDraw(Canvas canvas) {
        canvas.drawCircle((float) (pos.x * Board.maxXLocal / Board.maxX),
                (float) (pos.y * Board.maxYLocal / Board.maxY), (float) (r * Board.maxYLocal / Board.maxY), paint);
    }

    void move() {
        pos.add(multiply(dir, v));
    }
}
