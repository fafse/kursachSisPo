import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Server {
    int count;
    public BlockingQueue<ClientHandler> clientArrayList = new LinkedBlockingQueue<ClientHandler>();
    private List<String> commandList = new LinkedList<>();

    private void initializeCommandList()
    {
        commandList.clear();
        //commandList.add()
    }

    public void startServer(int port) throws IOException {
        ServerSocket serverSocket = null;
        try {
            initializeCommandList();
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");

            do {
                Socket newSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(newSocket);
                Thread thread = new Thread(client);
                thread.start();
                clientArrayList.add(client);

            } while (!clientArrayList.isEmpty());


        } catch (IOException e) {
            System.out.println("Launch error");
        }finally {
            serverSocket.close();
        }

    }

    class ClientHandler implements Runnable {

        Socket socket;
        Writer writer;
        String name;

        public ClientHandler(Socket socket) {
            this.socket = socket;

        }

        @Override
        public void run() {

            try (InputStream inputStream = socket.getInputStream()) {

                Scanner scanner = new Scanner(inputStream, "utf-8");
                String message;
                this.name = scanner.nextLine();
                message = this.name+" connected";
                sendMessage(message);

                while (socket.isConnected()) {
                    message = scanner.nextLine();
                    if (message.equals("quit")) {
                        System.out.println(this.name + " disconnected");
                        message = this.name+ " disconnected.";
                        sendMessage(message);
                        clientArrayList.remove(this);
                        break;
                    }

                    System.out.println(message);
                    sendMessage(message);

                }

            } catch (IOException e) {
                System.out.println(Thread.currentThread().toString() + " not initialized");
            }

        }

        private void sendMessage(String message) throws IOException {
            for (ClientHandler handler : clientArrayList) {

                if (handler.equals(this)) continue;

                if (handler.socket.isConnected()) {
                    Writer writer = new OutputStreamWriter(handler.socket.getOutputStream(), "utf-8");
                    writer.write(message + "\n");
                    writer.flush();
                } else {
                    System.out.println("Client " + handler.name + " is unavailable");
                }

            }
        }
    }
}
