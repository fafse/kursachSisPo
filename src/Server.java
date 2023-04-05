import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class Server {

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

    private static double calculate(String command) {
        String[] tokens = command.split(" ");
        double result = 0.0;
        switch (tokens[0]) {
            case "add":
                result = Double.parseDouble(tokens[1]) + Double.parseDouble(tokens[2]);
                break;
            case "subtract":
                result = Double.parseDouble(tokens[1]) - Double.parseDouble(tokens[2]);
                break;
            case "multiply":
                result = Double.parseDouble(tokens[1]) * Double.parseDouble(tokens[2]);
                break;
            case "divide":
                result = Double.parseDouble(tokens[1]) / Double.parseDouble(tokens[2]);
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
                    double result = calculate(inputLine);
                    date = new Date();
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
