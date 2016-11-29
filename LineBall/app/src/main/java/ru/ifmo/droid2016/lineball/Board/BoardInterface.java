package ru.ifmo.droid2016.lineball.Board;

public interface BoardInterface {
    void setWall(String coord, MoveFrom from);//and player identification

    void redraw();
}
