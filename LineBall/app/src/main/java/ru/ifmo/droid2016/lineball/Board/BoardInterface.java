package ru.ifmo.droid2016.lineball.Board;

/**
 * Created by maxeljkin on 23.11.16.
 */
public interface BoardInterface {
    void setWall(String coord, MoveFrom from);//and player identification
    void redraw();
}
