package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.Timer;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ChatServer {

    /***************
     * CONSTRUCTOR *
     ***************/

    public ChatServer() {
        clientConnections = new Vector<ClientConnection>();
        serverConnections = new Vector<ServerConnection>();
        votedNicknames = new Vector<String>();

        (new Thread(new ServerListenThread(this))).start();
        (new Thread(new ClientListenThread(this))).start();
        (new Thread(new CommandLineListener())).start();
    }

    /******************
     * LISTEN THREADS *
     ******************/

    private class ClientListenThread implements Runnable {
        public ClientListenThread(ChatServer chatserver) {
            this.chatServer = chatserver;
        }

        public void run() {
            try {
                ServerSocket listenSocket = new ServerSocket(
                        ServerConfig.getLocalClientPort());
                System.out.println("Listening for connecting clients on port "
                        + ServerConfig.getLocalClientPort());

                // Start listening for incoming connections from clients
                while (true) {
                    Socket remoteClientSocket = listenSocket.accept();
                    // Create a new ClientConnection for the incoming connection
                    new ClientConnection(remoteClientSocket, chatServer);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private ChatServer chatServer;
    }

    private class ServerListenThread implements Runnable {
        public ServerListenThread(ChatServer chatserver) {
            this.chatServer = chatserver;
        }

        public void run() {
            try {
                ServerSocket listenSocket = new ServerSocket(
                        ServerConfig.getLocalServerPort());
                System.out.println("Listening for connecting servers on port "
                        + ServerConfig.getLocalServerPort());

                // *************************
                // TODO: ADD YOUR CODE HERE
                // *************************

            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private ChatServer chatServer;
    }

    /*******************
     * STOP THE SERVER *
     *******************/

    /**
     * Listen for commandline commands
     */
    private class CommandLineListener implements Runnable {

        public void run() {
            boolean running = true;
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    System.in));

            while (running) {
                System.out.println("Type \"shutdown\" to stop the server.");
                try {
                    String command = br.readLine();

                    if (command.equals("shutdown")) {
                        running = false;
                        stopServer();
                    }
                } catch (IOException e) {
                    System.err.println("There was an IOException");
                }
            }
        }
    }

    /**
     * Stop the server
     */
    public void stopServer() {
        System.out.println("The server is stopping");
        String[] nickName = new String[1];

        for (int i = 0; i < clientConnections.size(); i++) {
            sendToServers(
                    ProtocolDB.RELEASE_NICKNAME_COMMAND,
                    new String[] { ((ClientConnection) clientConnections.get(i))
                            .getNickname() });
        }

        sendToServers(ProtocolDB.SERVERDISCONNECT_COMMAND, new String[0]);
        System.exit(0);
    }

    /********************
     * PROTOCOL METHODS *
     ********************/

    /**
     * Connect a client to the chat network.
     */
    public void clientConnect(ClientConnection clientConnection, String nickname) {
        clientConnection.setNickname(nickname);
        clientConnections.add(clientConnection);
        // If the nickname the client proposes has already been used or voted
        // for, reject the client
        if (votedNicknames.contains(nickname)) {
            System.out
                    .println("The client with nickname "
                            + nickname
                            + " failed to connect to the chat network. Nickname in use.");
            clientConnection.send(ProtocolDB.REJECTED_COMMAND, null);
            // otherwise, start a vote round for the nickname
        } else {
            votedNicknames.add(nickname);

            // *************************
            // TODO: ADD YOUR CODE HERE
            // *************************

            clientConnection.setConnected(true); // YOU HAVE TO CHANGE THIS!
            clientConnection.setNickname(nickname); // YOU HAVE TO CHANGE THIS!
            clientConnection.send(ProtocolDB.ACCEPTED_COMMAND, null); // YOU
                                                                      // HAVE TO
                                                                      // CHANGE
                                                                      // THIS!

        }
    }

    // *************************
    // TODO: ADD YOUR CODE HERE (is for the Timer)
    // *************************

    /**
     * Connect a server to the chat network.
     * 
     * @param connection
     */
    public void serverConnect(ServerConnection serverConnection) {

        // *************************
        // TODO: ADD YOUR CODE HERE
        // *************************

    }

    /**
     * removes a client connection
     * 
     * @param clientConnection
     */
    public void clientDisconnect(ClientConnection clientConnection) {
        String nickname = clientConnection.getNickname();
        System.out.println("Disconnecting client" + nickname);
        // tell the other servers that the nickname has been released
        sendToServers(ProtocolDB.RELEASE_NICKNAME_COMMAND,
                new String[] { nickname });

        // remove the client connection and forget the nickname
        clientConnections.remove(clientConnection);
        votedNicknames.remove(nickname);
    }

    /**
     * removes a server connection
     * 
     * @param serverConnection
     */
    public void serverDisconnect(ServerConnection serverConnection) {

        // *************************
        // TODO: ADD YOUR CODE HERE
        // *************************

    }

    /**
     * Broadcast the message from the given client connection to all other
     * clients and all other servers. The nickname of the sender will be
     * appended to the argument list.
     * 
     * @param clientConnection
     * @param messageText
     */
    public void clientMessage(ClientConnection clientConnection,
            String messageText) {
        String nickname = clientConnection.getNickname();

        // Put arguments of SERVERMESSAGE together
        String[] outArguments = new String[2];
        outArguments[0] = messageText;
        outArguments[1] = nickname;

        // Send to all your other connected clients
        sendToClientsExcept(nickname, ProtocolDB.SERVERMESSAGE_COMMAND,
                outArguments);

        // *************************
        // TODO: ADD YOUR CODE HERE
        // *************************

    }

    /**
     * Broadcast the given message to all clients
     * 
     * @param messageText
     * @param nickname
     */
    public void serverMessage(String messageText, String nickname) {

        // *************************
        // TODO: ADD YOUR CODE HERE
        // *************************

    }

    /**
     * Check if the given nickname hasn't already been registered and vote for
     * it if it's not.
     * 
     * @param serverConnection
     * @param nickname
     */
    public void checkNickname(ServerConnection serverConnection, String nickname) {

        // *************************
        // TODO: ADD YOUR CODE HERE
        // *************************

    }

    /**
     * Receive a vote for the given nickname. If a sufficient number of votes
     * has been gathered, accept the client to the chat network.
     * 
     * @param nickname
     */
    public void vote(String nickname) {

        // *************************
        // TODO: ADD YOUR CODE HERE
        // *************************

    }

    /**
     * Release the given nickname from the list of taken nicknames of this
     * server.
     * 
     * @param nickname
     */
    public void releaseNickname(String nickname) {

        // *************************
        // TODO: ADD YOUR CODE HERE
        // *************************

    }

    /*******************
     * SEND FUNCTIONS *
     *******************/

    /**
     * Send the given command and arguments to all registered servers
     */
    protected void sendToServers(String command, String[] arguments) {
        for (Iterator<ServerConnection> iter = serverConnections.iterator(); iter
                .hasNext();) {
            ServerConnection serverConnection = (ServerConnection) iter.next();
            serverConnection.send(command, arguments);
        }
    }

    /**
     * Send the given command and arguments to all registered clients
     * 
     * @param command
     * @param arguments
     */
    protected void sendToClients(String command, String[] arguments) {
        for (Iterator<ClientConnection> iter = clientConnections.iterator(); iter
                .hasNext();) {
            ClientConnection clientConnection = (ClientConnection) iter.next();
            if (clientConnection.isConnected()) {
                clientConnection.send(command, arguments);
            }
        }
    }

    /**
     * Send the given command and arguments to all registered clients except the
     * one with the given nickname
     * 
     * @param nickname
     * @param command
     * @param arguments
     */
    protected void sendToClientsExcept(String nickname, String command,
            String[] arguments) {
        for (Iterator<ClientConnection> iter = clientConnections.iterator(); iter
                .hasNext();) {
            ClientConnection clientConnection = (ClientConnection) iter.next();
            if (!nickname.equals(clientConnection.getNickname())
                    && clientConnection.isConnected()) {
                clientConnection.send(command, arguments);
            }
        }
    }

    /*************
     * VARIABLES *
     *************/

    private Vector<ClientConnection> clientConnections;
    private Vector<ServerConnection> serverConnections;
    private Vector<String> votedNicknames;
    private Timer timer;

}