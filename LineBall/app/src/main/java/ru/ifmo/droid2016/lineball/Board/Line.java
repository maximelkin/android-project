package ru.ifmo.droid2016.lineball.Board;

public class Line {
    double A, B, C;
    private static final double eps = 1e-9;

    Line(Point p1, Point p2) {
        getCoeff(p1, p2);
    }

    Line(double A, double B, double C){
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

    public Point intersectPoint(Line l) {
        double det = A * l.B - l.A * B;
        double detx = C * l.B - l.C * B;
        double dety = A * l.C - l.A * C;
        return new Point(-detx / det, -dety / det);
    }

    public boolean eq(Line l) {
        double det = Math.abs(A * l.B - l.A * B);
        double detx = Math.abs(C * l.B - l.C * B);
        double dety = Math.abs(A * l.C - l.A * C);
        return (det < eps && detx < eps && dety < eps);
    }
}
