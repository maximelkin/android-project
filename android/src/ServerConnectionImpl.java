import java.io.IOException;

//all bool - is success
public interface ServerConnectionImpl {
    boolean verify(String password);

    boolean registration(String password);

    //delete stats of user
    boolean resetUser();

    //get in line
    String search() throws IOException, IllegalAccessException;

    //send result of last game result = "win" or "loose"
    boolean gameOver(String result);

    //if game only
    boolean setWall(String coordinates);
}