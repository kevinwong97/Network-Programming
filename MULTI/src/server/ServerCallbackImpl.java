package server;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.Socket;

/**
 * uses bfConsole and loggers to print and log message events. 
 * This way we could identify what events have happened.
 * each method will have a another method underneath for logging
 * @author Kevin W
 *
 */
public class ServerCallbackImpl implements ServerCallback {
	
	private Logger logger;

	public ServerCallbackImpl(Logger logger) {
		this.logger = logger;
	}
	
	
	/**
	 * server initiated
	 */
	@Override
	public void scbServerInitiated(MultiPlayerServer server, int portNum) {
		SERVER_INITIATED("Server initiated on port Number " + portNum);
	}
	private void SERVER_INITIATED(String message) {
		System.out.println("SERVER STARTED - " + message);
		logger.log(Level.INFO, message);
	}
	

	/**
	 * client connected
	 */
	@Override
	public void scbClientConnected(MultiPlayerServer server, Socket socket) {
		CLIENT_CONNECTION_ESTABLISHED("Client " + socket.getInetAddress().toString() + " connected.");
	}
	private void CLIENT_CONNECTION_ESTABLISHED(String message) {
		System.out.println("CLIENT CONNECTED - " + message);
		logger.log(Level.INFO, message);
	}

	
	/**
	 * server responded
	 */
	@Override
	public void scbSendResponse(ServerProcess process, Response response) {
		SERVER_RESPONSE("Server: " + response.getMessage());
	}
	private void SERVER_RESPONSE(String message) {
		System.out.println("SERVER RESPONDED - " + message);
		logger.log(Level.INFO, message);
	}

	
	/**
	 * client replied
	 */
	@Override
	public void scbClientReply(ServerProcess process, String clientReply) {
		String address = process.getSocket().getInetAddress().toString();
		CLIENT_REPLY(String.format("%s: %s\n", address, clientReply));
	}
	private void CLIENT_REPLY(String message) {
		System.out.println("CLIENT REPLIED - " + message);
		logger.log(Level.INFO, message);
	}
	
	
	
	/**
	 * client disconnected
	 */
	@Override
	public void scbClientDisconnected(MultiPlayerServer server, Socket socket, ServerProcess process) {
		String address = socket.getInetAddress().toString();
		CLIENT_DISCONNECT(String.format("CLIENT DISCONNECTED: %s disconnected.", address));
	}
	private void CLIENT_DISCONNECT(String message) {
		System.out.println("CLIENT DISCONNECTED - " + message);
		logger.log(Level.INFO, message);
	}

	
	/**
	 * exception thrown
	 */
	@Override
	public void scbException(ServerProcess process, Exception e) {
		EXCEPTION_E(e);
	}
	public void EXCEPTION_E(Exception e) {
		System.err.println("EXCEPTION: " + e.getMessage());
		e.printStackTrace();
		logger.log(Level.SEVERE, e.getMessage(), e);
	}
	
}
