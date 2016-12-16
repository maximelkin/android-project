package ru.ifmo.droid2016.lineball.Board;

public class Ball {

    Point position;
    Point direction;

    boolean collision(Ball ball){
        return false;
    }

    boolean collision(Wall wall){
        return false;
    }
}
