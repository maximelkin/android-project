import java.io.IOException;

public interface ServerConnectionImpl {
    boolean verify(String password) throws IOException;

    boolean registration(String password) throws IOException;

    //delete stats of user
    boolean resetUser() throws IOException;

    //get in line
    String search() throws Exception;

    //send result of last game
    boolean gameOver(String result) throws IOException;

}