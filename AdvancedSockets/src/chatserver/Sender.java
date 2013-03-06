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
    	out = new DataOutputStream(connection.getSocket().getOutputStream());
    }
    
    /**
     * Send a message
     * @param command The command of this message
     * @param arguments	The arguments of this message
     */
    public void send(String command, String[] arguments) {
    	String message = command;
    	if(arguments != null) {
    		for(int i = 0; i < arguments.length; i++) {
    			message = message + ProtocolDB.COMMAND_DELIMITER + arguments[i];
    		}
    	}

    	try {
    	    System.out.println("writing message "+message);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private DataOutputStream out;
}