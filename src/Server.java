import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class Server {
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
            threads[i] = new Thread(new MD5Hasher((long)numPassword*(i)/numThreads,(long)numPassword*(i+1)/numThreads,hex_password));
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
        return MD5Hasher.getFoundPassword();
    }

    public void startServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }

    private static String calculate(String command) {
        String[] tokens = command.split(" ");
        String result = "";
        switch (tokens[0]) {
            case "add":
                result = String.valueOf((Double.parseDouble(tokens[1]) + Double.parseDouble(tokens[2])));
                break;
            case "subtract":
                result = String.valueOf(Double.parseDouble(tokens[1]) - Double.parseDouble(tokens[2]));
                break;
            case "multiply":
                result = String.valueOf(Double.parseDouble(tokens[1]) * Double.parseDouble(tokens[2]));
                break;
            case "divide":
                result = String.valueOf(Double.parseDouble(tokens[1]) / Double.parseDouble(tokens[2]));
                break;
            case "DeshMD5":
                if(tokens.length ==3)
                result= findMD5(tokens[1], Integer.parseInt(tokens[2]));
                else
                    result=findMD5(tokens[1], 100);
                MD5Hasher.makeDefault();
                break;
            case "MD5":
                result= hashPassword(tokens[1]);
                break;
            default:
                System.out.println("Unavailable command: " + tokens[0]);
                break;
        }
        return result;
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    Date date = new Date();

                    System.out.println(date.toString() +"\nClient sent message: " + inputLine);
                    if (inputLine.equals("exit")) {
                        break;
                    }
                    String result = calculate(inputLine);
                    date = new Date();
                    if(result.length()!=0)
                        out.println(date.toString()+":"+result);
                }
                System.out.println("Client disconnected");
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
