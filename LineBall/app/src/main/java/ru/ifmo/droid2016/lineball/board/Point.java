package ru.ifmo.droid2016.lineball.board;

import android.graphics.PointF;
import android.support.annotation.NonNull;

class Point extends PointF {

    static Creator<Point> CREATOR;

    Point(float x, float y){
        super(x, y);
    }

    void add(Point p) {
        x += p.x;
        y += p.y;
    }

    @NonNull
    static Point sum(Point point1, Point point2) {
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

    Point mul(float coefficient) {
        x *= coefficient;
        y *= coefficient;
        return this;
    }

    @NonNull
    static Point multiply(Point point, float k) {
        return new Point(k * point.x, k * point.y);
    }

    float scalarProduct(Point p) {
        return x * p.x + y * p.y;
    }

    Point normalize() {
        mul(1 / length());
        return this;
    }
}
