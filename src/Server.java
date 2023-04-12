package src;

import java.io.*;
        import java.net.ServerSocket;
        import java.net.Socket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
        import java.util.concurrent.BlockingQueue;
        import java.util.concurrent.LinkedBlockingQueue;


public class Server {
    int count;
    public BlockingQueue<ClientHandler> clientArrayList = new LinkedBlockingQueue<ClientHandler>();
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    public static String[] elements = {"qwertyuiopasdfghjklzxcvbnm"};
    private static String toHexString(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(HEX_DIGITS[(b & 0xff) >> 4]);
            hex.append(HEX_DIGITS[b & 0x0f]);
        }
        return hex.toString();
    }

    static String hashPassword(String password) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(password.getBytes());
        byte[] bytes = digest.digest();
        return toHexString(bytes);
    }

    public static String findMD5(String hex_password, int numThreads)
    {
        if(numThreads<0||numThreads>100)
            numThreads=100;
        int numPassword= (int) Math.pow(26,7);
        Thread[] threads = new Thread[numThreads];
        long t0 = System.nanoTime();
        for(int i = 0;i<numThreads;i++)
        {
            threads[i] = new Thread(new src.MD5Hasher((long)numPassword*(i)/numThreads,(long)numPassword*(i+1)/numThreads,hex_password));
            threads[i].start();
        }
        for(int i = 0;i<numThreads;i++)
        {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long t = System.nanoTime()-t0;
        System.out.println(t/1e9 +" Seconds required to solve this problem");
        return src.MD5Hasher.getFoundPassword();
    }
    public void StartServer(int port) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");

            do {
                System.out.println("I wait connection");
                Socket newSocket = serverSocket.accept();
                System.out.println("New tried connect");
                ClientHandler client = new ClientHandler(newSocket);
                Thread thread = new Thread(client);
                thread.start();
                System.out.println("connected");
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
                System.out.println("I try read name");
                this.name = scanner.nextLine();
                System.out.println("I read name");
                message = this.name+" connected";
                sendMessage(message);
                System.out.println("Got name");
                while (socket.isConnected()) {
                    System.out.println("I read message");
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
                    System.out.println(this.name+">:\n"+message);
                    writer.write(message + "\n");
                    writer.flush();
                } else {
                    System.out.println("Client " + handler.name + " unavailable");
                }

            }
        }
    }
}