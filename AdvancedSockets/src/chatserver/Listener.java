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
     * @param connection
     * @throws IOException
     * 		There was an IO problem creating the input stream for this listener
     */
    public Listener(Connection connection) throws IOException{
    	this.connection = connection;
    	in = new DataInputStream(connection.getSocket().getInputStream()); 
    }
    
    /**
     * Keep listening for incoming messages from the socket of this Listener
     */
    public void run() {  
        while(true) {
            try {
                String received = in.readUTF();
                System.out.println("Message received "+ received);
                //Parse the received string into a string array
                StringTokenizer tokenizer = new StringTokenizer(received, ProtocolDB.COMMAND_DELIMITER);
                String[] command = new String[tokenizer.countTokens()];
                for(int i=0; i<command.length; i++) {
                    command[i] = tokenizer.nextToken();
                }
              
                
                //Recognize the command to be called on the server
                if(command[0].equals(ProtocolDB.CLIENTCONNECT_COMMAND)) {
                    connection.getChatServer().clientConnect((ClientConnection) connection, command[1]);
                } else if(command[0].equals(ProtocolDB.CLIENTDISCONNECT_COMMAND)) {
                	connection.getChatServer().clientDisconnect((ClientConnection) connection);
                } else if(command[0].equals(ProtocolDB.CLIENTMESSAGE_COMMAND)) {
                	connection.getChatServer().clientMessage((ClientConnection) connection, command[1]);
                }
                
            	//*************************
            	//TODO: ADD YOUR CODE HERE
            	//*************************
                
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

