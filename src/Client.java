import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    Scanner scanner=new Scanner(System.in, "cp866");
    String address;

    public void startClient(int port) throws IOException {
        System.out.print("Enter address of server");
        System.out.println();
        address = scanner.nextLine();
        Socket socket = new Socket("192.168.1.102", 12345);
        System.out.println("Соединение установлено");
//s
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        while (true) {
            System.out.print("> ");
            userInput = stdIn.readLine();
            if (userInput == null) {
                break;
            }
            out.println(userInput);
            if (userInput.equals("exit")) {
                break;
            }
            String serverResponse = in.readLine();
            System.out.println("Сервер ответил: " + serverResponse);
        }
        System.out.println("Соединение разорвано");
        socket.close();
    }
}