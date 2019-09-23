
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String args[]) throws Exception {
        String filename;

        // scanner for Entering server address then close the scanner
        System.out.println("Enter File Name: ");
        Scanner sc = new Scanner(System.in);
        filename = sc.nextLine();
        sc.close();

        // iterate each line
        while (true) {
            //create server socket on port 61231
            ServerSocket ss = new ServerSocket(61231);
            System.out.println("Waiting for request");

            // accept socket connection between client and server
            Socket s = ss.accept();
            System.out.println("Connected With " + s.getInetAddress().toString());

            // read number of bytes from the input stream
            DataInputStream din = new DataInputStream(s.getInputStream());

            // return the bytes from input stream and written to output stream
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            try {
                String str = "";

                // read str from data input stream
                str = din.readUTF();
                System.out.println("Send....Ok");

                if (!str.equals("stop")) {

                    System.out.println("Sending File: " + filename);

                    // write data to designated filename
                    dout.writeUTF(filename);

                    // flush the output stream
                    dout.flush();

                    File f = new File(filename);
                    FileInputStream fin = new FileInputStream(f);

                    // size of file in bytes
                    long sz = (int) f.length();

                    // max buffer size
                    byte b[] = new byte[1024];

                    int read;

                    // write sz to output stream
                    dout.writeUTF(Long.toString(sz));

                    // flush the output stream
                    dout.flush();

                    System.out.println("Size: " + sz);
                    System.out.println("Buf size: " + ss.getReceiveBufferSize());

                    // read file and write into output stream then flush
                    while ((read = fin.read(b)) != -1) {
                        dout.write(b, 0, read);
                        dout.flush();
                    }

                    // close file input stream
                    fin.close();

                    System.out.println("..ok");
                    dout.flush();
                }
                dout.writeUTF("stop");
                System.out.println("Send Complete");
                dout.flush();

              // catch exception
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("An error occured");
            }
            // close socket and streams
            din.close();
            s.close();
            ss.close();
        }
    }
}  