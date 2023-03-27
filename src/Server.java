import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public void startServer(int port) throws IOException {
        // Создаем серверный сокет и привязываем его к порту
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Сервер запущен");

        // Бесконечный цикл ожидания клиентов
        while (true) {
            // Ожидаем подключения клиента
            Socket clientSocket = serverSocket.accept();
            System.out.println("Новый клиент подключился: " + clientSocket.getInetAddress().getHostAddress());

            // Создаем новый поток для обработки клиента
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
                System.out.println("Неподдерживаемая команда: " + tokens[0]);
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
                    System.out.println("Клиент отправил сообщение: " + inputLine);
                    if (inputLine.equals("exit")) {
                        break;
                    }
                    double result = calculate(inputLine);
                    out.println(result);
                }
                System.out.println("Клиент отключился");
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}