package ru.ifmo.droid2016.lineball.Board;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;

//TODO all
public class Board {
    private final LayoutInflater layoutInflater;
    private Wall[] walls;
    private Ball b1, b2;


    public void setWall(String coord, @NonNull Who from) {

    }

    //should return who won or null if game not ended
    @Nullable
    public Who redraw() {
        return null;
    }

    public Board(LayoutInflater inflater) {
        layoutInflater = inflater;
    }
}
