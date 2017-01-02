package ru.ifmo.droid2016.lineball.board;

class Line {
    double A, B, C;
    private static final double eps = 1e-9;

    Line(Point p1, Point p2) {
        this.A = p2.y - p1.y;
        this.B = p1.x - p2.x;
        this.C = -(A * p1.x + B * p1.y);
    }

    boolean contain(Point p) {
        return (Math.abs(A * p.x + B * p.y + C) < eps);
    }

    double dist(Point p) {
        return (A * p.x + B * p.y + C) / Math.sqrt(A * A + B * B);
    }

}
