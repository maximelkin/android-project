package ru.ifmo.droid2016.lineball.Board;

public class Line {
    double A, B, C;
    private static final double eps = 1e-9;

    Line(Point p1, Point p2) {
        getCoeff(p1, p2);
    }

    Line(double A, double B, double C) {
        this.A = A;
        this.B = B;
        this.C = C;
    }

    private void getCoeff(Point p1, Point p2) {
        this.A = p2.y - p1.y;
        this.B = p1.x - p2.x;
        this.C = -(A * p1.x + B * p1.y);
    }

    public boolean intersect(Line l) {
        return (Math.abs(A * l.B - l.A * B) < eps);
    }

    public double dist(Point p) {
        return (A * p.x + B * p.y + C) / Math.sqrt(A * A + B * B);
    }
}
