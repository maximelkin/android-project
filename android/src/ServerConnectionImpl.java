import java.io.IOException;

//all bool - is success
public interface ServerConnectionImpl {

    //verify user for this session
    boolean verify(String password);

    //create new user with this pass, verify is still need
    boolean registration(String password);

    //delete stats of user
    boolean resetUser();

    //return true when game starts
    boolean search() throws IOException, IllegalAccessException;

    //send result of last game, result should be "win" or smth else
    boolean gameOver(String result);

    //if game only
    boolean setWall(String coordinates);

    //get coordinates of wall
    String getWall() throws IOException;

    //reconnect to server, if success, return true
    boolean reconnect();
}