package ru.ifmo.droid2016.lineball.Board;

public class Wall {
    Point p1, p2;
    double A, B, C;

    Wall(Point p1, Point p2){
        this.p1 = p1;
        this.p2 = p2;
        getCoeff(p1, p2);
    }

    private void getCoeff(Point p1, Point p2) {
        this.A = p2.y - p1.y;
        this.B = p1.x - p2.x;
        C = -(A * p1.x + B * p1.y);
    }

}
