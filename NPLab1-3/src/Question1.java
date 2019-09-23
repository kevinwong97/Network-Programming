import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Question1 {

    public static void main(String[] args) {

        System.out.println("Please enter a sequence of characters:");

        try (
                InputStream input = new BufferedInputStream(System.in);
                OutputStream output = new BufferedOutputStream(System.out);
        ) {
            int text = input.read();
            while (text != -1) {
                char character = (char) text;
                if (character == ' ') {
                    character = '_';
                }

                // just stores the input data text in the buffer
                output.write(character);

                // flush() will push the input data from buffer into console.
                output.flush();

                // read next character
                text = input.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
