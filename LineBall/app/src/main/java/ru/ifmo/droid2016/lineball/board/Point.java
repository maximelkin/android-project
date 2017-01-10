package ru.ifmo.droid2016.lineball.board;

import android.support.annotation.NonNull;

class Point {
    double x, y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double length() {
        return Math.sqrt(x * x + y * y);
    }

    void add(Point p) {
        x += p.x;
        y += p.y;
    }

    @NonNull
    static Point sum(Point point1, Point point2){
        return new Point(point1.x + point2.x, point1.y + point2.y);
    }

    Point sub(Point p) {
        x -= p.x;
        y -= p.y;
        return this;
    }

    @NonNull
    static Point sub(Point point1, Point point2) {
        return new Point(point1.x - point2.x, point1.y - point2.y);
    }

    Point mul(double coefficient) {
        x *= coefficient;
        y *= coefficient;
        return this;
    }

    @NonNull
    static Point multiply(Point point, double k) {
        return new Point(k * point.x, k * point.y);
    }

    double scalarProduct(Point p) {
        return x * p.x + y * p.y;
    }

}
