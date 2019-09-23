package server;

import java.net.Socket;

/**
 * events that are called by main classes
 * @author Kevin W
 *
 */
public interface ServerCallback {
	
	/**
	 * server initiate on port 
	 */
	void scbServerInitiated(SinglePlayerServer server, int portNum);
	
	/**
	 * client connects
	 */
	void scbClientConnected(SinglePlayerServer server, Socket socket);
	
	/**
	 * sends a response of object type response
	 */
	void scbSendResponse(ServerProcess process, Response response);
	
	/**
	 * server gets reply from client
	 */
	void scbClientReply(ServerProcess process, String clientReply);
	
	/**
	 * server disconnect
	 */
	void scbClientDisconnected(SinglePlayerServer server, Socket socket, ServerProcess process);
	
	/**
	 * exception
	 */
	void scbException(ServerProcess process, Exception e);
}