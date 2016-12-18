package ru.ifmo.droid2016.lineball.Board;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;

import java.util.ArrayList;

//TODO all
public class Board {
    private final LayoutInflater layoutInflater;
    private ArrayList<Wall> walls1, walls2;
    private Ball b1, b2;

    public Who check() {
        if(b1.outOfBoard()){
            return Who.RIVAL;
        }

        if(b2.outOfBoard()){
            return Who.THIS_USER;
        }

        if (b1.collision(b2)) {
            if (b1.v > b2.v)
                return Who.THIS_USER;
            else if (b2.v > b1.v)
                return Who.RIVAL;
            else if (b1.v == b2.v){
                b1.dir.mul(-1);
                b2.dir.mul(-1);
                return null;
            }
        }

        Who who = null;
        for (Wall wall : walls1) {
            if(b1.collision(wall)) {
                b1.rotate(wall);
                break;
            }
        }

        for (Wall wall : walls2) {
            if(b2.collision(wall)) {
                b2.rotate(wall);
                break;
            }
        }
        return null;
    }

    public void setWall(String coord, @NonNull Who from) {

    }

    //should return who won or null if game not ended
    @Nullable
    public Who redraw() {
        return check();
    }

    public Board(LayoutInflater inflater) {
        layoutInflater = inflater;
    }
}
