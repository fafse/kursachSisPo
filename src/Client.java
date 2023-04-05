import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    Scanner scanner=new Scanner(System.in, "cp866");
    String address;
    public static String getRules()
    {
        String rules="";
        rules += "===================================================\n";
        rules+="Commands:\n";
        rules+="===================================================\n";
        rules+="1. add\nExample of using:\nadd 5 3\nServer will answer:8\n";
        rules+="2. subtract\nExample of using:\nsubtract 5 3\nServer will answer:2\n";
        rules+="3. multiply\nExample of using:\nmultiply 5 3\nServer will answer:15\n";
        rules+="4. divide\nExample of using:\ndivide 6 3\nServer will answer:2\n";
        rules+="5. MD5\n Example of using:\nMD5 torules\nServer will answer:\n";
        rules+="6. DeshMD5\nExample of using:\nDeshMd5 39D89CD686B43C82A7509A638A4AB6DD 100\nServer will answer:rulesss\nATTENTION:\n" +
                "This command is available for 7 letter message(in decrypted form)\n";
        rules+="7. help\nExample of using:\nhelp\nServer will write this note.\n";
        rules+="===================================================\n";
        return rules;
    }

    public void startClient(int port) throws IOException {
        System.out.print("Enter address of server");
        System.out.println();
        address = scanner.nextLine();
        Socket socket = new Socket(address, port);
        System.out.println("Connection stabilized");
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        System.out.println(getRules());
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
            if(userInput.equals("help"))
            {
                System.out.println(getRules());
            }
            String serverResponse = in.readLine();
            System.out.println("Server answered:\n" + serverResponse);
        }
        System.out.println("Connection destroyed");
        socket.close();
    }
}