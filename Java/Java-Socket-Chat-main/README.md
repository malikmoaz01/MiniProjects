# Client-Server Communication System

This repository contains a simple implementation of a client-server communication system using Java Socket programming. The system allows communication between a client and a server over a network.

# Concept That used in There :
1. GUI
2. Streams
3. Multithreading
4. Socket Programming 

# I add Comments on each line & functionality for understanding. 

## Introduction

This project demonstrates how to establish a socket connection between a client and a server using Java. It enables bidirectional communication between the two, where the client can send messages to the server and vice versa.

## Features

- Establishes a client-server connection over a network.
- Allows clients to send messages to the server and vice versa.
- Implemented using multithreading to handle multiple client connections simultaneously.

## Requirements

To run this project, you need:

- Java Development Kit (JDK) installed on your system.
- Basic understanding of Java Socket programming concepts.

## Installation

1. Clone the repository to your local machine using the following command:

   git clone https://github.com/your-username/Java-Socket-Chat.git

2. Navigate to the project directory:


   cd Java-Socket-Chat

3. Compile the Java files:

   javac *.java

## Usage

1. Start the server by running the following command:

   java Server

2. Once the server is running, start the client by running:

   java Client

3. You should see messages indicating the client and server are connected.

4. Start typing messages on the client console. Press Enter to send a message. Messages will be displayed on both client and server consoles.

5. To exit, type `exit` on the client console. This will terminate the client-server connection.
