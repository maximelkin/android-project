package ru.ifmo.droid2016.lineball;

//whats codes gets each class
public class MessageCodes {
    //socket codes
    public final static int MSG_VERIFY_USER = 200;
    public final static int MSG_REGISTRATION = 201;
    public final static int MSG_SEARCH = 202;
    public final static int MSG_SEND_WALL_TO_RIVAL = 203;
    public final static int MSG_GET_WALL_FROM_RIVAL = 204;
    //Game codes
    public final static int MSG_GAME_END = 300;
    public final static int MSG_SET_WALL_FROM_RIVAL = 301;
    //GameActivity codes
    public final static int MSG_SOCKET_READY = 400;
    public final static int MSG_USER_VERIFIED = 401;
    public final static int MSG_VERIFYING_ERROR = 402;
    public final static int MSG_START_GAME = 403;
    //extra codes
    public final static int MSG_GET_TOP = 500;
    public final static int MSG_DELETE_USER = 501;
    //common
    public final static int MSG_ERROR = 600;
}
