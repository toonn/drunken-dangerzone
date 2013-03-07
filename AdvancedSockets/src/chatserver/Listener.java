package chatserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * The Listener will listen for incoming packets and will parse them.
 */
public class Listener implements Runnable {
    /**
     * Create a new listener for the given socket
     * 
     * @param connection
     * @throws IOException
     *             There was an IO problem creating the input stream for this
     *             listener
     */
    public Listener(Connection connection) throws IOException {
        this.connection = connection;
        in = new DataInputStream(connection.getSocket().getInputStream());
    }

    /**
     * Keep listening for incoming messages from the socket of this Listener
     */
    public void run() {
        while (true) {
            try {
                String received = in.readUTF();
                System.out.println("Message received " + received);
                // Parse the received string into a string array
                StringTokenizer tokenizer = new StringTokenizer(received,
                        ProtocolDB.COMMAND_DELIMITER);
                String[] command = new String[tokenizer.countTokens()];
                for (int i = 0; i < command.length; i++) {
                    command[i] = tokenizer.nextToken();
                }

                // Recognize the command to be called on the server
                if (command[0].equals(ProtocolDB.CLIENTCONNECT_COMMAND)) {
                    connection.getChatServer().clientConnect(
                            (ClientConnection) connection, command[1]);
                } else if (command[0]
                        .equals(ProtocolDB.CLIENTDISCONNECT_COMMAND)) {
                    connection.getChatServer().clientDisconnect(
                            (ClientConnection) connection);
                } else if (command[0].equals(ProtocolDB.CLIENTMESSAGE_COMMAND)) {
                    // connection.getChatServer().clientMessage(
                    // (ClientConnection) connection, command[1]);

                    // TODO ? Avoids crashing chatserver when someone sends an
                    // empty
                    // message
                    String message = "";
                    if (command.length > 1)
                        message = command[1];
                    connection.getChatServer().clientMessage(
                            (ClientConnection) connection, message);
                }
                // Connect server
                else if (command[0].equals(ProtocolDB.SERVERCONNECT_COMMAND)) {
                    connection.getChatServer().serverConnect(
                            (ServerConnection) connection);
                }
                // Disconnect server
                else if (command[0].equals(ProtocolDB.SERVERDISCONNECT_COMMAND)) {
                    connection.getChatServer().serverDisconnect(
                            (ServerConnection) connection);
                }
                // Pass a message from a server to your clients
                else if (command[0].equals(ProtocolDB.SERVERMESSAGE_COMMAND)) {
                    connection.getChatServer().serverMessage(command[1],
                            command[2]);
                }
                // Check for a nickname and reply with a vote
                else if (command[0].equals(ProtocolDB.CHECK_NICKNAME_COMMAND)) {
                    connection.getChatServer().checkNickname(
                            (ServerConnection) connection, command[1]);
                }
                // Receive a vote, decide if you can accept a client's nickname
                else if (command[0].equals(ProtocolDB.VOTE_COMMAND)) {
                    connection.getChatServer().vote(command[1]);
                }
                // Forget a nickname so it can be reused later
                else if (command[0].equals(ProtocolDB.RELEASE_NICKNAME_COMMAND)) {
                    connection.getChatServer().releaseNickname(command[1]);
                }

            } catch (IOException e) {
                System.out.println("A connection has been closed");
                return;
            }
        }
    }

    protected void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Connection connection;
    private DataInputStream in;

}
