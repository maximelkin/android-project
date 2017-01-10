package ru.ifmo.droid2016.lineball.board;

import android.graphics.Canvas;
import android.graphics.Paint;

import static ru.ifmo.droid2016.lineball.board.Board.*;
import static ru.ifmo.droid2016.lineball.board.Point.*;

class Ball {

    private final Paint paint;
    private Point pos;
    Point direction;
    private static final double radius = 30;
    double speed;

    Ball(Point pos, Point direction, Paint paint) {
        this.pos = pos;
        this.direction = direction;
        this.speed = 8;
        this.paint = paint;
    }

    boolean collision(Ball ball) {
        return sub(ball.pos, pos).length() < 2 * radius;
    }

    boolean collision(Wall wall, boolean settingWall) {

        Point m = sub(pos, wall.p2);
        Point p = sub(pos, wall.p1);
        Point q = sub(wall.p2, wall.p1);
        //d_ball = distance between wall-line and ball
        double d_ball = wall.line.distance(pos);
        boolean intersect;

        if (m.scalarProduct(q) * p.scalarProduct(q) <= 0) {
            //simple case
            intersect = (Math.abs(d_ball) <= radius);
        } else {
            //ball impact with end of wall
            intersect = (m.length() <= radius || p.length() <= radius);
        }
        if (!intersect)
            return false;

        if (settingWall) {
            return true;
        }
        Point nextPos = sum(pos, direction);

        if (wall.line.contain(pos) || wall.line.contain(nextPos)) {
            direction.mul(-1);
            return false;
        }

        double d_nextPos = wall.line.distance(nextPos);

        return (Math.abs(d_nextPos) < Math.abs(d_ball));
    }

    void rotate(Wall w) {

        Point p = sum(w.p1, direction);
        //n - normal vector for wall line
        Point n = new Point(w.line.A, w.line.B);
        //d - distance between line and (p1 + direction)
        double d = w.line.distance(p);
        n.mul(2 * d / n.length());

        if (w.line.contain(p)) {
            p.add(n);
        } else {
            p.sub(n);
        }
        direction = sub(p, w.p1);
        direction.mul(1 / direction.length());
    }

    boolean outOfBoard() {
        return (pos.x < -radius || pos.x > maxX + radius || pos.y < -radius || pos.y > maxY + radius);
    }

    void onDraw(Canvas canvas) {
        canvas.drawCircle((float) (pos.x * maxXLocal / maxX), (float) (pos.y * maxYLocal / maxY),
                (float) (radius * maxYLocal / maxY), paint);
    }

    void move() {
        pos.add(multiply(direction, speed));
    }
}
