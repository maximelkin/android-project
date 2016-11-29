package ru.ifmo.droid2016.lineball.Socket;

import java.io.IOException;

//all bool - is success
interface ServerConnectionImpl {

    //verify user for this session
    boolean verify(String password);

    //create new user with this pass, verify is still need
    boolean registration(String password);

    //delete stats of user
    boolean resetUser();

    //return true when game starts
    boolean search() throws IOException, IllegalAccessException;

    //send result of last game, result should be "win" or something else
    boolean gameOver(String result);

    //if game only
    void setWall(String coordinates) throws IOException;

    //get coordinates of wall
    String getWall() throws IOException;

    long getTimeDelta() throws IOException;
}