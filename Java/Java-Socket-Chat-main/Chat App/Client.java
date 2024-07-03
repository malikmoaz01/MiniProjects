import java.io.*;
import java.net.*;

public class Client {

    // Declaring a private Socket variable named socket to handle the client-side socket connection.
    private Socket socket;

    // Declaring a private BufferedReader variable named br to read input from the server.
    private BufferedReader br;

    // Declaring a private PrintWriter variable named out to write output to the server.
    private PrintWriter out;

    // Constructor method for the Client class.
    public Client() {
        try {
            // Creating a new Socket object and connecting it to the server at localhost on port 6666.
            socket = new Socket("127.0.0.1", 6666);

            // Printing a message indicating that the client has connected to the server.
            System.out.println("Connected to server.");

            // Creating a BufferedReader to read input from the server's input stream.
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Creating a PrintWriter to write output to the server's output stream.
            out = new PrintWriter(socket.getOutputStream(), true);

            // Calling the Reading() method to handle reading from the server.
            Reading();

            // Calling the Writing() method to handle writing to the server.
            Writing();

        } catch (Exception e) {
            // Catching any exceptions that might occur during client setup and printing the stack trace.
            e.printStackTrace();
        }
    }

    // Declaring a method named Reading which does not take any parameters and does not return anything.
    public void Reading() {
        // Defining a Runnable instance using lambda expression to execute the reading task in a separate thread.
        Runnable r1 = () -> {
            try {
                // Printing a message indicating that reading is being performed.
                System.out.println("Reading is performing ");
                // Reading a line of text from the BufferedReader associated with the socket's input stream and storing it in the line variable.
                String line = br.readLine();

                // Looping while the read line is not null, indicating that there's more content to read.
                while (line != null) {
                    // Printing the message indicating what the server said, concatenated with the line read.
                    System.out.println("Server: " + line);

                    // Reading the next line of text from the BufferedReader associated with the socket's input stream and updating the line variable.
                    line = br.readLine();
                }

                // Printing a message indicating that reading is terminated when no more content is available to read.
                System.out.println("Terminated");

            } catch (Exception e) {
                // Catching any exceptions that might occur during the execution of the reading task and printing the stack trace.
                e.printStackTrace();
            }
        };

        // Creating a new thread (read_t1) with the Runnable r1.
        Thread read_t1 = new Thread(r1);

        // Starting the read_t1 thread to execute the reading task concurrently.
        read_t1.start();
    }

    // Declaring a method named Writing which does not take any parameters and does not return anything.
// Modify the Writing method in Client class
public void Writing() {

    System.out.println("Writing is performing ");

    // Defining a Runnable instance using lambda expression to execute the writing task in a separate thread.
    Runnable r2 = () -> {
        try {
            // Creating a BufferedReader instance to read input from the console (System.in).
            BufferedReader b1 = new BufferedReader(new InputStreamReader(System.in));
            
            String content;

            // Continue writing until "exit" command is entered
            while (!(content = b1.readLine()).equalsIgnoreCase("exit")) {
                // Sending the content to the PrintWriter instance (out) associated with the socket's output stream.
                out.println(content);
                // Flushing the PrintWriter to ensure the content is sent immediately without buffering.
                out.flush();
            }

        } catch (Exception e) {
            // Catching any exceptions that might occur during the execution of the writing task and printing the stack trace.
            e.printStackTrace();
        }
    };

    // Creating a new thread (writerThread) with the Runnable r2.
    Thread writerThread = new Thread(r2);

    // Starting the writerThread to execute the writing task concurrently.
    writerThread.start();
}


    public static void main(String[] args) {
        System.out.println("Client is running");
        new Client();
    }
}
