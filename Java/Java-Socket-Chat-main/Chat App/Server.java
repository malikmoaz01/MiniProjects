import java.io.*;
import java.net.*;

public class Server {
// Declaring a private ServerSocket variable named server to handle server-side socket connections.
private ServerSocket server;

// Declaring a private Socket variable named socket to handle individual client requests.
private Socket socket; // For Client Request

// Now do work in Files
// For Reading
// Declaring a BufferedReader variable named br to read input from the client.
private BufferedReader br;

// For Writing 
// Declaring a PrintWriter variable named pw to write output to the client.
private PrintWriter out;

// Constructor method for the Server class.
public Server() {
    try {
        
        // Creating a new ServerSocket object and binding it to port 6666.
        server = new ServerSocket(6666);
        
        // Printing a message indicating that the server is waiting for connections.
        System.out.println("Connection is establishing !!!\nWaiting ");
        
        // Accepting a connection from a client and storing the Socket object representing the client's connection.
        socket = server.accept(); // For connection accept from Client
        
        // Creating a BufferedReader to read input from the client's input stream.
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Creating a PrintWriter to write output to the client's output stream.
        out = new PrintWriter(socket.getOutputStream(), true);

        // Calling the Reading() method to handle reading from the client.
        Reading();

        // Calling the Writing() method to handle writing to the client.
        Writing(); 

    } catch (Exception e) {

        // Catching any exceptions that might occur during server setup and printing the stack trace.
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
            
                // Printing the message indicating what the client said, concatenated with the line read.
                System.out.println("Client :" + line);
            
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

        System.out.println("Server is running");
        new Server(); // We are doing in Constructor So we have to make object
    }
}
