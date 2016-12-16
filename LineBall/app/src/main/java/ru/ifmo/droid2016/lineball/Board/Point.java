package ru.ifmo.droid2016.lineball.Board;

public class Point {
    double x, y;

    Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    double lenght(){
        return Math.sqrt(x * x + y * y);
    }

    Point sum (Point p){
        return new Point(this.x + p.x, this.y + p.y);
    }

    Point sub(Point p){
        return new Point(this.x - p.x, this.y - p.y);
    }

    double cp(Point p){
        return this.x * p.y - this.y * p.x;
    }

    double sp(Point p){
        return this.x * p.x + this.y * p.y;
    }
}
