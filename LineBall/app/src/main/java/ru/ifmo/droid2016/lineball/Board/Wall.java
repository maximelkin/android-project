package ru.ifmo.droid2016.lineball.Board;

public class Wall {
    Point p1, p2;
    Line l;
    int k = 2;

    Wall(Point p1, Point p2, Line l){
        this.p1 = p1;
        this.p2 = p2;
        this.l = l;
    }
}
