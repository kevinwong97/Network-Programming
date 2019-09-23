import java.net.*;
import java.io.*;
import java.util.*;

//creation of server class
public class Server
{
    public static void main(String args[]) throws Exception
    {
        ServerSocket soc=new ServerSocket(61231); //new socket has been started
        System.out.println("FTP Server Started on Port Number 61231"); //print the port number of socket
        while(true)
        {
            System.out.println("Waiting for Connection ..."); //print for wait of connection by client
            transferfile t=new transferfile(soc.accept()); //file transfer initiated
            
        }
    }
}

//file transfer class
class transferfile extends Thread
{
	//definition of variables
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    
    transferfile(Socket soc) //file transfer constructor
    {
        try
        {
            ClientSoc=soc;                        
            din=new DataInputStream(ClientSoc.getInputStream()); //new data stream for input of data
            dout=new DataOutputStream(ClientSoc.getOutputStream()); //new data stream for output of data
            System.out.println("FTP Client Connected ..."); //print connection notification
            start(); //start file transfer using socket
            
        }
        catch(Exception ex) //error handling
        {
        }        
    }
	//funtion to send file
    void SendFile() throws Exception
    {        
	//variable definition
        String filename=din.readUTF();
        File f=new File(filename);
		//file check before transfer
        if(!f.exists())
        {
            dout.writeUTF("File Not Found");
            return;
        }
        else
        {
            dout.writeUTF("READY"); //write to file status
            FileInputStream fin=new FileInputStream(f); //new file stream
            int ch;
			//read file and write file stream on data output stream
            do
            {
                ch=fin.read();
                dout.writeUTF(String.valueOf(ch));
            }
            while(ch!=-1);    
            fin.close();    
            dout.writeUTF("File Receive Successfully");  //success message notification                          
        }
    }
    
	//function to receive file
    void ReceiveFile() throws Exception
    {
		//variable definition
        String filename=din.readUTF(); 
		
		//check file name existance
        if(filename.compareTo("File not found")==0)
        {
            return;
        }
		//create new file
        File f=new File(filename);
        String option;
        
        if(f.exists())
        {
            dout.writeUTF("File Already Exists"); //check error for existance
            option=din.readUTF();
        }
        else
        {
            dout.writeUTF("SendFile"); //start send file option
            option="Y";
        }
            
            if(option.compareTo("Y")==0)
				//transfer file by character
            {
                FileOutputStream fout=new FileOutputStream(f);
                int ch;
                String temp;
                do
                {
                    temp=din.readUTF();
                    ch=Integer.parseInt(temp);
                    if(ch!=-1)
                    {
                        fout.write(ch);                    
                    }
                }while(ch!=-1);
                fout.close();
                dout.writeUTF("File Send Successfully"); //success message
            }
            else
            {
                return;
            }
            
    }

// run the program functions in the sequence of their need
    public void run()
    {//check client response and execute commands
        while(true)
        {
            try
            {
            System.out.println("Waiting for Command ...");
            String Command=din.readUTF();
            if(Command.compareTo("GET")==0)
            {
                System.out.println("\tGET Command Received ...");
                SendFile();
                continue;
            }
            else if(Command.compareTo("SEND")==0)
            {
                System.out.println("\tSEND Command Receiced ...");                
                ReceiveFile();
                continue;
            }
            else if(Command.compareTo("DISCONNECT")==0)
            {
                System.out.println("\tDisconnect Command Received ...");
                System.exit(1);
            }
            }
            catch(Exception ex) //error handling
            {
            }
        }
    }
}