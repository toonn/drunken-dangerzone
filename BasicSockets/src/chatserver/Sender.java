package chatserver;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The Sender will take care of all actions required for sending messages.
 */
public class Sender {
    /**
     * Create a new Sender that has an empty nickname-socket mapping
     */
    public Sender(Connection connection) throws IOException {
        this.connection = connection;
        out = new DataOutputStream(this.connection.getSocket()
                .getOutputStream());
    }

    /**
     * Send a message
     * 
     * @param command
     *            The command of this message
     * @param arguments
     *            The arguments of this message
     */
    public void send(String command, String[] arguments) {
        String commandString = command;
        if (arguments != null)
            for (String arg : arguments)
                commandString += arg;

        try {
            out.writeUTF(commandString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Connection connection;
    private DataOutputStream out;
}