package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
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

                // if (ServerConfig.getNbOtherServers() >
                // serverConnections.size())

                // Attempt to connect to all known servers
                // TODO Connection attempts could fail, try again? (loop)
                for (int serverIndex = 0; serverIndex < ServerConfig
                        .getNbOtherServers(); serverIndex++) {
                    // Get hostname and port number for the next connection
                    String remoteServerName = ServerConfig
                            .getRemoteServer(serverIndex);
                    int remoteServerPort = ServerConfig
                            .getRemoteServerPort(serverIndex);
                    // Attempt to create a connection with the server
                    Socket newRemoteServerSocket = new Socket(remoteServerName,
                            remoteServerPort);
                    // Create a new ServerConnection for the remote chatserver
                    new ServerConnection(newRemoteServerSocket, chatServer);
                }

                // Start listening for incoming connections from servers
                while (true) {
                    // Listen for connection on "getLocalServerPort"
                    Socket remoteChatServerSocket = listenSocket.accept();
                    // Create a new ServerConnection for the incoming connection
                    new ServerConnection(remoteChatServerSocket, chatServer);
                }

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
            // TODO throw away the connection?
            clientConnections.remove(clientConnection);
        }
        // otherwise, start a vote round for the nickname
        else {
            votedNicknames.add(nickname);
            // Ask for votes
            sendToServers(ProtocolDB.CHECK_NICKNAME_COMMAND,
                    new String[] { nickname });
            // If you don't receive enough votes after 3 seconds, reject the
            // nickname
            new Timer()
                    .schedule(new RejectNicknameTask(clientConnection), 3000);
        }
    }

    // TODO If you remove timer, when do you reject a client?
    private class RejectNicknameTask extends TimerTask {
        public RejectNicknameTask(ClientConnection client) {
            this.clientToRejectOnTimeOut = client;
        }

        @Override
        public void run() {
            // Inform client of rejection
            clientToRejectOnTimeOut.send(ProtocolDB.REJECTED_COMMAND, null);
            // Forget about the client's connection
            clientConnections.remove(clientToRejectOnTimeOut);
            // Forget one instance of the nickname from votedNicknames
            releaseNickname(clientToRejectOnTimeOut.getNickname());
        }

        private ClientConnection clientToRejectOnTimeOut;
    }

    /**
     * Connect a server to the chat network.
     * 
     * @param connection
     */
    public void serverConnect(ServerConnection serverConnection) {
        // Add the connection to our vector of connected servers
        serverConnections.add(serverConnection);

        // TODO Set up connection in the other direction?
        // Notify incoming chatserver with SERVER_CONNECT message
        serverConnection.send(ProtocolDB.SERVERCONNECT_COMMAND, null);
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
        // Remove the server from our vector, so we no longer try to send
        // messages to it
        serverConnections.remove(serverConnection);
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
        // TODO changed to CLIENTMESSAGE instead of SERVERMESSAGE?
        sendToClientsExcept(nickname, ProtocolDB.CLIENTMESSAGE_COMMAND,
                outArguments);

        // Send to all your connected chatservers
        sendToServers(ProtocolDB.SERVERMESSAGE_COMMAND, outArguments);
    }

    /**
     * Broadcast the given message to all clients
     * 
     * @param messageText
     * @param nickname
     */
    public void serverMessage(String messageText, String nickname) {
        // Send incoming chat messages to all connected clients
        sendToClients(ProtocolDB.CLIENTMESSAGE_COMMAND, new String[] {
                messageText, nickname });
    }

    /**
     * Check if the given nickname hasn't already been registered and vote for
     * it if it's not.
     * 
     * @param serverConnection
     * @param nickname
     */
    public void checkNickname(ServerConnection serverConnection, String nickname) {
        // If the nickname isn't in votedNicknames then it is unique
        boolean nicknameUnique = !votedNicknames.contains(nickname);
        // Add the nickname to our vector (for future checks)
        votedNicknames.add(nickname);
        // If the nickname was indeed unique, send a vote back to the server
        // that requested it
        if (nicknameUnique) {
            serverConnection.send(ProtocolDB.VOTE_COMMAND,
                    new String[] { nickname });
        }
    }

    /**
     * Receive a vote for the given nickname. If a sufficient number of votes
     * has been gathered, accept the client to the chat network.
     * 
     * @param nickname
     */
    public void vote(String nickname) {
        boolean clientRejectedOnTimeOut = true;
        // Check if the client with this nickname is already connected, if so
        // then stop (ACCEPTED messages will only be sent once)
        for (ClientConnection client : clientConnections)
            if (nickname.equals(client.getNickname()) && client.isConnected())
                return;
            else if (nickname.equals(client.getNickname()))
                // If a client with nickname still exists it has not been
                // rejected because of a timeout yet
                clientRejectedOnTimeOut = false;

        // If no connected client has the name nickname, then the client must
        // have been rejected
        if (clientRejectedOnTimeOut)
            return;

        // If this is the first vote for this nickname create an entry in
        // nicknameVotes, otherwise increment it
        if (nicknameVotes.get(nickname) == null)
            nicknameVotes.put(nickname, 1);
        else
            nicknameVotes.put(nickname, nicknameVotes.get(nickname) + 1);

        // If this nickname has enough votes, accept it's connection
        // (integer + 1) / 2, gives ceiling(integer/2) for java integer division
        // why ceiling(int/2)? -> ceil(nbOfConnections/2) is the smallest number
        // of votes needed for a majority; ceil(nbOfConnections/2) + 1(your own
        // vote) > totalNbOfPossibleVotes/2
        if (nicknameVotes.get(nickname) > (serverConnections.size() + 1) / 2) {
            // Remove the vote count for this nickname incase it is requested
            // again in the future (so the count will start from zero)
            nicknameVotes.remove(nickname);
            // Connect the right client and send an ACCEPTED message
            for (ClientConnection client : clientConnections)
                if (nickname.equals(client.getNickname())) {
                    client.setConnected(true);
                    client.send(ProtocolDB.ACCEPTED_COMMAND, null);
                }
        }
    }

    private Map<String, Integer> nicknameVotes = new HashMap<String, Integer>();

    /**
     * Release the given nickname from the list of taken nicknames of this
     * server.
     * 
     * @param nickname
     */
    public void releaseNickname(String nickname) {
        // Forget one instance of the nickname from the votedNicknames vector
        votedNicknames.remove(nickname);
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
}