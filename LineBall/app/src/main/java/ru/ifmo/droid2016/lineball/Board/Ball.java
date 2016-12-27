package ru.ifmo.droid2016.lineball.Board;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import static java.lang.StrictMath.abs;

public class Ball {

    Point pos;
    Point dir;
    double r, v;
    double eps = 1e-9;

    Ball(Point pos, Point dir) {
        this.pos = pos;
        this.dir = dir;
        this.r = 30;
        this.v = 5;
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

        Point m = b1.pos.sub(w1.p2);
        Point p = b1.pos.sub(w1.p1);
        Point q = w1.p2.sub(w1.p1);
        Point qrev = new Point(q.mul(-1));
        double d_ball = w1.l.dist(b1.pos);
        double d_nextPos = w1.l.dist(nextPos);
        boolean intersect = false;

        if (m.scalarProduct(qrev) * p.scalarProduct(q) >= 0) {
            intersect |= (Math.abs(d_ball) <= r);
        } else {
            intersect |= (m.length() <= r || p.length() <= r);
        }
        if (!intersect)
            return false;

        if(w1.l.contain(pos) && w1.l.contain(nextPos)){
            dir = dir.mul(-1);
            return false;
        }

        return (Math.abs(d_nextPos) < Math.abs(d_ball));
    }

    public void rotate(Wall w) {
        //TODO Just do it OK

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
        dir = dir.mul(1 / dir.length());
    }


    /*
    public void rotate(Wall wall) {
        //collision with p1
        Point p1p2 = wall.p2.sub(wall.p1);
        if (p1p2.scalarProduct(pos.sub(wall.p1)) < 0) {
            Log.e("Rotate", "impact with p1");
            rotateDueToPoint(wall.p1);
            return;
        }
        //collision with p2
        Point p2p1 = wall.p1.sub(wall.p2);
        if (p2p1.scalarProduct(pos.sub(wall.p2)) < 0) {
            Log.e("Rotate", "impact with p2");
            rotateDueToPoint(wall.p2);
            return;
        }
        //usual impact with wall
        //wallNormal - normal vector for wall.l with length = 1
        Log.e("Rotate", "impact with wall");
        Point wallNormal = new Point(wall.l.A, wall.l.B).normalize();
        dir = dir.sum(wallNormal.mul(-dir.scalarProduct(wallNormal) * 2)).normalize();
    }

    public void rotateDueToPoint(Point point) {
        //lets "lineBetween" - line between point and pos
        Point normalVectorLineBetween = pos.sub(point).getPerpendicularVector().normalize();
        dir = dir.sum(normalVectorLineBetween.mul(dir.scalarProduct(normalVectorLineBetween) * 2)).normalize();
    }

    */

    public boolean outOfBoard(double mX, double mY) {
        return (pos.x < -r || pos.x > mX + r || pos.y < -r || pos.y > mY + r);
    }

    public void onDraw(Canvas canvas, Paint p) {
        //TODO convert coordinates
        canvas.drawCircle((float) (pos.x * Board.maxXLocal / Board.maxX), (float) (pos.y * Board.maxYLocal / Board.maxY), (float) (r * Board.maxYLocal / Board.maxY), p);
    }

    public void move() {
        pos = pos.sum(dir.mul(v));
    }
}
