package server;

import java.io.Serializable;

/**
 * server sends instance of this class to client, 
 * client will read and do functions based on object 
 * 
 * @author Kevin
 *
 */
 public class Response implements Serializable {

	private static final long serialVersionUID = 4651351238532156465L;
	                                             
	public static final int PRINT_MESSAGE = 0;
	public static final int READ_LINE = 1;
	public static final int QUIT = 2;

	public String message;
	public int type = PRINT_MESSAGE;
	
	public Response(String message) {
		this(message, PRINT_MESSAGE);
	}
	
	public Response(String message, int type) {
		this.message = message;
		this.type = type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getType() {
		return type;
	}
	
	public static Response message(String message) {
		return new Response(message, PRINT_MESSAGE);
	}
	
	public static Response readLine(String message) {
		return new Response(message, READ_LINE);
	}
	
	public static Response quit(String message) {
		return new Response(message, QUIT);
	}
}
