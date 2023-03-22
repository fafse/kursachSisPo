import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    String name;
    Scanner scanner=new Scanner(System.in, "cp866");
    String address;
    OutputStreamWriter writer;


    public void startClient(int port) throws IOException {
        System.out.print("Enter address of server");
        System.out.println();
        address = scanner.nextLine();

        ChatListener chatListener = null;
        try (Socket socket = new Socket(address, port)) {


            writer = new OutputStreamWriter(socket.getOutputStream(), "utf-8");

            chatListener = new ChatListener(socket);
            Thread thread = new Thread(chatListener);

            thread.start();

            System.out.print("Enter nickname");
            System.out.println();
            this.name = scanner.nextLine();
            writer.write(this.name + "\n");
            writer.flush();

            String message;

            System.out.println("To leave print 'quit'");
            while (true) {
                message = scanner.nextLine();
                if (message.toLowerCase().equals("quit")) {
                    writer.write(message + "\n");
                    writer.flush();
                    chatListener.isWork = false;
                    break;
                } else {
                    writer.write(this.name +">:"+message + "\n");
                    writer.flush();
                }

            }
        } catch (IOException e) {
            System.out.println("Connection error");
            this.startClient(port);

        } finally {
            writer.close();
            scanner.close();

        }

    }

    class ChatListener implements Runnable {

        boolean isWork = true;

        Socket socket;

        public ChatListener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Scanner scanner = new Scanner(socket.getInputStream(), "utf-8")) {

                while (true) {

                    if(isWork) {
                        try {
                            System.out.println(scanner.nextLine());
                        } catch (NoSuchElementException e){
                            System.out.println("Bye");
                            break;
                        }

                    } else {
                        scanner.close();
                        socket.close();
                        break;
                    }

                }

            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }
}