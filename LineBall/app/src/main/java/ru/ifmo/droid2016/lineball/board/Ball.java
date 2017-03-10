package ru.ifmo.droid2016.lineball.board;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RectShape;

import static ru.ifmo.droid2016.lineball.board.Board.*;
import static ru.ifmo.droid2016.lineball.board.Point.*;

class Ball {

    private final Paint paint;
    private Point pos;
    Point direction;
    private static final float radius = 30;
    private static final float defaultSpeed = 8;
    float speed;

    Ball(Point pos, Point direction, Paint paint) {
        this.pos = pos;
        this.direction = direction;
        this.speed = defaultSpeed;
        this.paint = paint;
    }

    boolean collisionWithBall(Ball ball) {
        return sub(ball.pos, pos).length() <= 2 * radius;
    }

    boolean collisionWithWall(Wall wall, boolean settingWall) {

        Point m = sub(pos, wall.p2);
        Point p = sub(pos, wall.p1);
        Point q = sub(wall.p2, wall.p1);
        float distanceToWall = wall.line.distance(pos);
        boolean isIntersect;

        if (m.scalarProduct(q) * p.scalarProduct(q) <= 0) {
            //simple case
            isIntersect = (Math.abs(distanceToWall) <= radius);
        } else {
            //ball impact with one of the end of wall
            isIntersect = (m.length() <= radius || p.length() <= radius);
        }
        if (!isIntersect)
            return false;

        if (settingWall) {
            return true;
        }
        Point nextPos = sum(pos, direction);

        if (wall.line.contain(pos) || wall.line.contain(nextPos)) {
            direction.mul(-1);
            return false;
        }

        float d_nextPos = wall.line.distance(nextPos);

        return d_nextPos < distanceToWall;
    }

    //wtf some kind of magic
    void rotate(Wall wall) {

        Point p = sum(wall.p1, direction);
        //n - normal vector for wall line
        Point wallNormalVector = wall.line.getNormalVector();
        //d - distance between line and (wall.p1 + direction)
        float d = wall.line.distance(p);
        wallNormalVector.normalize().mul(2 * d);

        if (wall.line.contain(p)) {
            p.add(wallNormalVector);
        } else {
            p.sub(wallNormalVector);
        }
        direction = sub(p, wall.p1).normalize();
    }

    boolean outOfBoard() {
        return !borders.contains(pos.x, pos.y);
    }

    void onDraw(Canvas canvas, boolean isGameMaster) {
        if (isGameMaster) {
            canvas.drawCircle(
                    pos.x * maxXLocal / maxX,
                    pos.y * maxYLocal / maxY,
                    radius * maxYLocal / maxY,
                    paint);
        } else {
            canvas.drawCircle(
                    maxXLocal - pos.x * maxXLocal / maxX,
                    maxYLocal - pos.y * maxYLocal / maxY,
                    radius * maxYLocal / maxY,
                    paint);
        }
    }

    void move() {
        pos.add(multiply(direction, speed));
    }
}
