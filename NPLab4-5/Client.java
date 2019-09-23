
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    public static void main(String args[]) throws Exception {
        String address = "";

        // scanner for Entering server address
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Server Address: ");
        address = sc.nextLine();

        // create the socket on port 61231
        Socket s = new Socket(address, 61231);

        // read number of bytes from the input stream
        DataInputStream din = new DataInputStream(s.getInputStream());

        // return the bytes from input stream and written to output stream
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        // reads text from input stream, characters are then buffered.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Type 'start' to begin file send...");
        String str = "", filename = "";
        try {
            // iterate each line
            while (!str.equals("start"))
                str = br.readLine();

            // write str to output stream
            dout.writeUTF(str);

            // flush the output stream
            dout.flush();

            // reads str in file
            filename = din.readUTF();
            System.out.println("Receving file: " + filename);
            filename = "client" + filename;
            System.out.println("Saving as file: " + filename);

            // filename content converted to long decimals
            long sz = Long.parseLong(din.readUTF());
            System.out.println("File Size: " + sz + " Bytes");

            // max byte array of 1024 bytes size
            byte b[] = new byte[1024];
            System.out.println("Receving file..");
            FileOutputStream fos = new FileOutputStream(new File(filename), true);
            long bytesRead;

            // read and write into file output stream
            do {
                bytesRead = din.read(b, 0, b.length);
                fos.write(b, 0, b.length);
            } while (!(bytesRead < 1024));

            // close socket and stream
            System.out.println("Completed");
            fos.close();
            dout.close();
            s.close();

          // catch exception
        } catch (EOFException e) {
            e.printStackTrace();
            System.out.println("An error occured");
        }
    }
}
