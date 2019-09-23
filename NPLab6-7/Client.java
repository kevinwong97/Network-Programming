import java.net.*;
import java.io.*;
import java.util.*;

//client class for program
class Client
{
    public static void main(String args[]) throws Exception
    {
        Socket soc=new Socket("netprog2.csit.rmit.edu.au",61231); //socket start at localhost and port
        transferfileClient t=new transferfileClient(soc); //file transfer client function
        t.displayMenu();
        
    }
}

//class for file trasnfer client side
class transferfileClient
{
	//variable declaration
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;
	
	//start file trsnafer from data streams
    transferfileClient(Socket soc)
    {
        try
        {
			//data stream definitions
            ClientSoc=soc;
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            br=new BufferedReader(new InputStreamReader(System.in));
        }
        catch(Exception ex) //error handling
        {
        }        
    }
	
	//funtion to send file
    void SendFile() throws Exception
    {        
        //variable declaration
        String filename;
        System.out.print("Enter File Name :");
        filename=br.readLine();
            
        File f=new File(filename);
		
		//check and print error on file existance
        if(!f.exists())
        {
            System.out.println("File not Exists...");
            dout.writeUTF("File not found");
            return;
        }
        
        dout.writeUTF(filename); //write file data
        
        String msgFromServer=din.readUTF();
		
		//check server esponse and send file
        if(msgFromServer.compareTo("File Already Exists")==0)
        {
            String Option;
            System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
			//bit by bit file send option
            Option=br.readLine();            
            if(Option=="Y")    
            {
                dout.writeUTF("Y");
            }
            else
            {
                dout.writeUTF("N");
                return;
            }
        }
        
        System.out.println("Sending File ..."); //sending notification
        FileInputStream fin=new FileInputStream(f);
        int ch;
		
		//read file for sending
        do
        {
            ch=fin.read();
            dout.writeUTF(String.valueOf(ch));
        }
        while(ch!=-1);
        fin.close();
        System.out.println(din.readUTF());
        
    }
    
	
	//function to receive file
    void ReceiveFile() throws Exception
    {
		//variable declaration
        String fileName;
        System.out.print("Enter File Name :");
        fileName=br.readLine();
        dout.writeUTF(fileName);
        String msgFromServer=din.readUTF();
        
		//check for file existance
        if(msgFromServer.compareTo("File Not Found")==0)
        {
            System.out.println("File not found on Server ...");
            return;
        }
		
		//check server status and receive file
        else if(msgFromServer.compareTo("READY")==0)
        {
            System.out.println("Receiving File ...");
            File f=new File(fileName);
			
			//check file name status and proceed
            if(f.exists())
            {
                String Option;
                System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
                Option=br.readLine();            
                if(Option=="N")    
                {
                    dout.flush();
                    return;    
                }                
            }
            FileOutputStream fout=new FileOutputStream(f);
            int ch;
            String temp;
			
			//bit by bit file read and receive
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
            System.out.println(din.readUTF());
                
        }
        
        
    }

	//display menu for cleint activity
    public void displayMenu() throws Exception
    {
        while(true)
        {    
            System.out.println("[ MENU ]");
            System.out.println("1. Send File");
            System.out.println("2. Receive File");
            System.out.println("3. Exit");
            System.out.print("\nEnter Choice :");
            int choice;
            choice=Integer.parseInt(br.readLine());
            if(choice==1)
            {
                dout.writeUTF("SEND");
                SendFile();
            }
            else if(choice==2)
            {
                dout.writeUTF("GET");
                ReceiveFile();
            }
            else if(choice==3)
            {
                dout.writeUTF("DISCONNECT");
                System.exit(1);
            }
        }
    }
}