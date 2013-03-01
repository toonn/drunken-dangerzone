package chatserver;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * The Listener will listen for incomming packets and will parse them.
 */
public class Listener implements Runnable {
    /**
     * Create a new listener for the given socket
     * 
     * @param server
     * @throws IOException
     *             There was an IO problem creating the input stream for this
     *             listener
     */
    public Listener(Connection connection) throws IOException {
        this.connection = connection;
        in = new DataInputStream(connection.getSocket().getInputStream());
    }

    /**
     * Keep listening for incoming messages from the socket of this Listener and
     * parse the messages.
     */
    @Override
    public void run() {
        while (true) {
            String clientInput = "";
            try {
                clientInput = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] inputSplit = clientInput
                    .split(ProtocolDB.COMMAND_DELIMITER);
            if (ProtocolDB.CLIENTCONNECT_COMMAND.equals(inputSplit[0])) {
                connection.getChatServer().clientConnect(
                        (ClientConnection) connection, inputSplit[1]);
            } else if (ProtocolDB.CLIENTDISCONNECT_COMMAND
                    .equals(inputSplit[0])) {
                connection.getChatServer().clientDisconnect(
                        (ClientConnection) connection);
            } else if (ProtocolDB.CLIENTMESSAGE_COMMAND.equals(inputSplit[0])) {
                connection.getChatServer().clientMessage(
                        (ClientConnection) connection, inputSplit[1]);
            }
        }
    }

    private Connection connection;
    private DataInputStream in;

}
