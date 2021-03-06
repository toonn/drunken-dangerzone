package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {

    /***************
     * CONSTRUCTOR *
     ***************/

    public ChatServer() {
        clientConnections = new Vector<ClientConnection>();

        (new Thread(new ClientListenThread(this))).start();
    }

    /******************
     * LISTEN THREADS *
     ******************/

    private class ClientListenThread implements Runnable {
        public ClientListenThread(ChatServer chatserver) {
            this.chatServer = chatserver;
            try {
                this.serverSocket = new ServerSocket(
                        ServerConfig.getLocalClientPort());
            } catch (IOException ioExcept) {
                ioExcept.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                Socket connectionSocket;
                try {
                    connectionSocket = serverSocket.accept();

                    new ClientConnection(connectionSocket, chatServer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private ChatServer chatServer;
        private ServerSocket serverSocket;
    }

    /********************
     * PROTOCOL METHODS *
     ********************/

    /**
     * Connect a client to the chat network.
     * 
     * @param connection
     * @param nickname
     */
    public void clientConnect(ClientConnection clientConnection, String nickname) {
        boolean unique = true;
        for (ClientConnection client : clientConnections)
            if (!unique)
                break;
            else
                unique = (!nickname.equals(client.getNickname()));

        if (unique) {
            clientConnection.setNickname(nickname);
            clientConnection.setConnected(true);
            clientConnections.add(clientConnection);
            clientConnection.getSender()
                    .send(ProtocolDB.ACCEPTED_COMMAND, null);
        } else {
            clientConnection.send(ProtocolDB.REJECTED_COMMAND, null);
        }
    }

    /**
     * removes a client connection
     * 
     * @param clientConnection
     */
    public void clientDisconnect(ClientConnection clientConnection) {
        clientConnection.setConnected(false);
        clientConnections.remove(clientConnection);
    }

    /**
     * Broadcast the message from the given client connection to all other
     * clients.
     * 
     * @param clientConnection
     * @param messageText
     */
    public void clientMessage(ClientConnection clientConnection,
            String messageText) {
        String nickname = clientConnection.getNickname();
        String[] message = { messageText, nickname };
        for (ClientConnection receiver : clientConnections)
            if (!nickname.equals(receiver.getNickname()))
                receiver.send(ProtocolDB.SERVERMESSAGE_COMMAND, message);
    }

    /*************
     * VARIABLES *
     *************/

    private Vector<ClientConnection> clientConnections;
}