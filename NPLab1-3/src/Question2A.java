import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.io.IOException;



public class Question2A {

    public static void main(String[] args) {

        File outputFile = new File("console_output.txt");
        File checksumFile = new File("checksum_output.txt");

        System.out.println(

                "Please enter a sequence of characters. Press 'x' in new line and Enter key to end");

        try (

                InputStream input = new BufferedInputStream(System.in);

                // checking each line. first ByteArrayOutputStream is reset.
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

                // store entire input.
                ByteArrayOutputStream byteOutput2 = new ByteArrayOutputStream();

                // write to 2nd ByteArrayOutputStream and calculate checksum.
                CheckedOutputStream checkedOutputStream = new CheckedOutputStream(byteOutput2, new CRC32());

                // write to files 'console_output.txt' and 'checksum_output.txt'
                OutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(outputFile));
                OutputStream checksumFileOutput = new BufferedOutputStream(new FileOutputStream(checksumFile));
        ) {

            // do until 'x' is found
            do {
                // iterate each line
                int text = input.read();
                while (text != -1) {
                    if ((char) text == '\n') {
                        break;
                    }
                    byteOutput.write(text);
                    text = input.read();
                }

                // checks for uppercase and lowercase 'x'
                String line = byteOutput.toString();
                if (line.trim().toLowerCase().equals("x")) {
                    break;
                }

                // current line is added to the end
                checkedOutputStream.write(line.getBytes());

                // reset to break if statement and stops return of lines
                byteOutput.reset();

            } while (true);


            // write to console_output.txt & checksum_output.txt
            fileOutput.write(byteOutput2.toString().getBytes());

            // get the checksum, then write to checksum_output.txt
            long checksum = checkedOutputStream.getChecksum().getValue();
            String checksumStr = Long.toString(checksum);
            checksumFileOutput.write(checksumStr.getBytes());

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}
