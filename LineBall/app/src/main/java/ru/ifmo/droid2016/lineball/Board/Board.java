package ru.ifmo.droid2016.lineball.Board;

import android.view.LayoutInflater;

import java.util.ArrayList;

//TODO all
public class Board {
    private final LayoutInflater layoutInflater;
    private ArrayList<Wall> walls1, walls2;
    private Ball b1, b2;


    public void setWall(String coord, MoveFrom from) {

    }

    public void redraw() {

    }

    public Board(LayoutInflater inflater) {
        layoutInflater = inflater;
    }
}
