package ru.ifmo.droid2016.lineball.Board;

public class Point {
    double x, y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    double length() {
        return Math.sqrt(x * x + y * y);
    }

    Point sum(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    Point mul(double k) {
        return new Point(k * x, k * y);
    }

    double crossProduct(Point p) {
        return x * p.y - y * p.x;
    }

    double scalarProduct(Point p) {
        return x * p.x + y * p.y;
    }

    Point normalize() {
        return mul(1 / length());
    }

    Point getPerpendicularVector(){
        return new Point(-y, x);
    }
}
