package ru.ifmo.droid2016.lineball.board;

import static ru.ifmo.droid2016.lineball.board.Board.eps;

class Line {
    private double a, b, c;

    Line(Point p1, Point p2) {
        this.a = p2.y - p1.y;
        this.b = p1.x - p2.x;
        this.c = -(a * p1.x + b * p1.y);
    }

    Point getNormalVector(){
        return new Point(a, b);
    }

    boolean contain(Point p) {
        return (Math.abs(a * p.x + b * p.y + c) < eps);
    }

    double distance(Point p) {
        return Math.abs(a * p.x + b * p.y + c) / Math.sqrt(a * a + b * b);
    }

}
