package server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerFileHandler {

    /**
	 * Create a logger object. The logger is handled via a file. 
	 * if log file does not exist it creates a new onee
	 */
	public static <T> Logger createLogger(String fileName, Class<T> className) {
		Logger logger = Logger.getLogger(className.getName());
		logger.setUseParentHandlers(false);
		
		
		FileHandler fileHandler = null;
		try {
			// logging via files. if true then add to existing log file
			fileHandler = new FileHandler(fileName, true);
			fileHandler.setLevel(Level.INFO);
			fileHandler.setFormatter(new SimpleFormatter()); 
			
			logger.addHandler(fileHandler);
			return logger;
		} 
		catch (SecurityException e) {
			logger.log(Level.SEVERE, "Logger stopped working. ", e.getMessage());
		}
		catch (IOException e) {
			logger.log(Level.SEVERE, "Logger stopped working. ", e.getMessage());
		}
		
		return logger;
	}
	
}
