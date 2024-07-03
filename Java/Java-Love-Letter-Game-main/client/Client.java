package org.example.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client 
{
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void setUsername() {
        try {
            // Send username to the server
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Listen for server response about the username
            String serverResponse = bufferedReader.readLine();
            System.out.println(serverResponse);  // Print server's username acceptance message
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();

                if ("bye".equalsIgnoreCase(messageToSend.trim())) {
                    // Send a goodbye message to the server to disconnect
                    bufferedWriter.write("bye");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    closeEverything(socket, bufferedWriter, bufferedReader);
                    break; // Exit the loop
                } else {
                    // Send any other message including commands
                    bufferedWriter.write(messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    String msgFromGroupChat = bufferedReader.readLine();
                    if (msgFromGroupChat != null) {
                        System.out.println(msgFromGroupChat); // Print message received from the server
                    }
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username for the game: ");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 1212); 
            Client client = new Client(socket, username);
            client.setUsername();
            client.listenForMessage();  
            client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
