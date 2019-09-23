import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Question2B {

    public static void main(String[] args) {

        File localFile = new File("console_output.txt");

        try (
                InputStream fileInput = new BufferedInputStream(new FileInputStream(localFile));
                OutputStream output = new BufferedOutputStream(System.out);
                CheckedOutputStream checkedOutputStream = new CheckedOutputStream(output, new CRC32());
        ) {

            // checkedOutputStream to print the text from file to console and calculate checksum
            int text = fileInput.read();
            while (text != -1) {
                // print contents of console_output.txt into console
                checkedOutputStream.write(text);
                text = fileInput.read();
            }

            // print checksum
            Long checksum = checkedOutputStream.getChecksum().getValue();
            System.out.println("Checksum: " + Long.toString(checksum));


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
