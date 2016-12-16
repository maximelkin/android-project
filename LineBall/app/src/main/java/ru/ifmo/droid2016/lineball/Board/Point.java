package ru.ifmo.droid2016.lineball.Board;

public class Point {
    double x, y;

    Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    double length(){
        return Math.sqrt(x * x + y * y);
    }

    Point sum (Point p){
        return new Point(x + p.x, y + p.y);
    }

    Point sub(Point p){
        return new Point(x - p.x, y - p.y);
    }

    double cp(Point p){
        return x * p.y - y * p.x;
    }

    double sp(Point p){
        return x * p.x + y * p.y;
    }
}
