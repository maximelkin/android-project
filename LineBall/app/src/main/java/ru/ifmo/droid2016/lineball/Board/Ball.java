package ru.ifmo.droid2016.lineball.Board;

public class Ball {

    Point pos;
    Point dir;
    int r;
    double v = 5;
    double eps = 1e-9;

    boolean collision(Ball ball){
        Ball b1 = this;
        Ball b2 = ball;
        b2.pos.sub(b1.pos);
        b1.pos = new Point(0, 0);
        Line line = new Line(-2 * b2.pos.x, -2 * b2.pos.y, b2.pos.x * b2.pos.x + b2.pos.y * b2.pos.y + b1.r * b1.r - b2.r * b2.r);
        return checkIntersect(b1, line);
    }

    boolean checkIntersect(Ball b, Line l){
        double x0 = -l.A * l.C / (l.A * l.A + l.B * l.B), y0 = -l.B * l.C / (l.A * l.A + l.B * l.B);
        if (l.C * l.C > b.r * b.r * (l.A * l.A + l.B * l.B) + eps){
            return false;
        } else {
            return true;
        }
    }

    boolean collision(Wall wall){
        Ball b1 = this;
        b1.pos = new Point(0, 0);
        Line line = new Line(wall.p1.sub(b1.pos), wall.p2.sub(b1.pos));
        return checkIntersect(b1, line);
    }

    public void rotate(Wall w) {
        Point n = new Point(-w.l.B, w.l.A);
        double angle = 2 * Math.atan2(dir.cp(n), dir.sp(n));
        double x1 = dir.x * Math.cos(angle) - dir.y * Math.sin(angle);
        double y1 = dir.y * Math.cos(angle) + dir.x * Math.sin(angle);
        dir = new Point(x1, y1);
    }

    public boolean outOfBoard(double mX, double mY) {
        if(pos.x < 0 || pos.x > mX || pos.y < 0 || pos.y > mY)
            return false;
        else
            return true;
    }
}
