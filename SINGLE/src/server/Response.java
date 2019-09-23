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


	private static final long serialVersionUID = 1235123545348156465L;

	public static final int PRINTMESSAGE = 0;
	public static final int READLINE = 1;
	public static final int QUIT = 2;
	
	public String message;
	
	public int type = PRINTMESSAGE;
	
	public Response(String message) {
		this(message, PRINTMESSAGE);
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
		return new Response(message, PRINTMESSAGE);
	}
	
	public static Response readLine(String message) {
		return new Response(message, READLINE);
	}
	
	public static Response quit(String message) {
		return new Response(message, QUIT);
	}
}
