package org.ac.bootcamp;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class Server {
    private final int PORT_NUMBER = 8080;
    private final Vector<ClientHandler> playersList = new Vector<>();
    private ClientHandler clientHandler;

    public Server() {

    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);

            while (playersList.size() < 2) {
                Socket clientSocket = serverSocket.accept();
                Prompt prompt = new Prompt(clientSocket.getInputStream(), new PrintStream(clientSocket.getOutputStream()));
                StringInputScanner question1 = new StringInputScanner();
                question1.setMessage("\nType your name player: ");
                String username = prompt.getUserInput(question1);
                clientHandler = new ClientHandler(clientSocket);
                clientHandler.name = username;

                playersList.addElement(clientHandler);
                System.out.println("Player " + playersList.size() + " connected");
            }

            for (ClientHandler ch : playersList) {
                System.out.println("thread created");
                Thread thread = new Thread(ch);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Class Client Handler
     */
    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintStream printStream;
        private String name;
        private final BufferedReader in;
        public static final String TEXT_RESET = "\u001B[0m";
        public static final String TEXT_RED = "\u001B[31m";

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                printStream = new PrintStream(clientSocket.getOutputStream());
                printStream.write("\n".getBytes(StandardCharsets.UTF_8));
                printStream.write("░██╗░░░░░░░██╗███████╗░██████╗████████╗███████╗██████╗░███╗░░██╗\n".getBytes(StandardCharsets.UTF_8));
                printStream.write("░██║░░██╗░░██║██╔════╝██╔════╝╚══██╔══╝██╔════╝██╔══██╗████╗░██║\n".getBytes(StandardCharsets.UTF_8));
                printStream.write("░╚██╗████╗██╔╝█████╗░░╚█████╗░░░░██║░░░█████╗░░██████╔╝██╔██╗██║\n".getBytes(StandardCharsets.UTF_8));
                printStream.write("░░████╔═████║░██╔══╝░░░╚═══██╗░░░██║░░░██╔══╝░░██╔══██╗██║╚████║\n".getBytes(StandardCharsets.UTF_8));
                printStream.write("░░╚██╔╝░╚██╔╝░███████╗██████╔╝░░░██║░░░███████╗██║░░██║██║░╚███║\n".getBytes(StandardCharsets.UTF_8));
                printStream.write("░░░╚═╝░░░╚═╝░░╚══════╝╚═════╝░░░░╚═╝░░░╚══════╝╚═╝░░╚═╝╚═╝░░╚══╝\n".getBytes(StandardCharsets.UTF_8));
                printStream.write("\nWelcome to the Western!\nThis are the game rules:\n--> The game has only 2 players.\n--> The first person to write the word (shoot) win.\n".getBytes(StandardCharsets.UTF_8));

            } catch (IOException e) {
                e.printStackTrace();
            }

            Thread.currentThread().setName(name);

            if (playersList.size() == 2) {
                try {
                    tellEveryone("Well done " + Thread.currentThread().getName() + ", prepare yourselves for this.");
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer();
            }
            fight();
        }

        public void timer() {
            try {
                tellEveryone("5");
                Thread.sleep(1000);
                tellEveryone("4");
                Thread.sleep(1000);
                tellEveryone("3");
                Thread.sleep(1000);
                tellEveryone("Ready...");
                Thread.sleep(1000);
                tellEveryone("1");
                Thread.sleep(1000);
                tellEveryone(TEXT_RED + "FIRE!!!\n" + TEXT_RESET);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void dontTell(String message, ClientHandler c) {
            for (ClientHandler clientHandler : playersList) {
                try {
                    PrintWriter writer = new PrintWriter(clientHandler.clientSocket.getOutputStream(), true);
                    if (!clientHandler.equals(c)) {
                        writer.println(message);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void tellEveryone(String message) {
            for (ClientHandler clientHandler : playersList) {
                try {
                    if (this.equals(clientHandler)) {
                        continue;
                    }
                    PrintWriter writer = new PrintWriter(clientHandler.clientSocket.getOutputStream(), true);
                    writer.println(message);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void fight() {
            try {

                long startTime = System.currentTimeMillis();

                String line = in.readLine();

                if (line.equals("shoot")) {

                    long stopTime = System.currentTimeMillis();
                    long reactionTime = stopTime - startTime;

                    dontTell(Thread.currentThread().getName() + " won the gunfire! He is the king of the Western!\n", this);
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    writer.println("\nCongratulation! You were the faster one!");
                    writer.println("You took: " + reactionTime + " ms to fire!\n");
                    System.out.println("End of the program");
                    System.exit(1);

                } else {
                    tellEveryone(Thread.currentThread().getName() + " you shoot like my grandma!\n");
                    System.out.println("End of the program");
                    System.exit(1);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}