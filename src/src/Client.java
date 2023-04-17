package src;

/*import java.io.BufferedReader;
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
        //System.out.print("Enter address of server");
        //System.out.println();
        address = "localhost";
                //scanner.nextLine();
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
}*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class Client {
    String name;
    int port;
    JTextField textField;
    String address;
    Writer writer;
    Scanner reader;
    JFrame frame;
    JMenuBar mb;
    JTextArea textArea;
    JButton helpButton;
    JPanel panel; // панель не видна при выводе
    JLabel label;
    JTextField nameField;
    JTextField addressField;
    JButton sendButton;
    JButton ConnectButton;
    JButton reset;

    ActionListener actionListenerHelpButton = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] rules = Client.getRules();
            for (int i = 0; i < 10; i++) {
                textArea.append(rules[i] + "\n");
            }
        }
    };


    ActionListener actionListenerSendButton = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String message = textField.getText();
                if(message.toLowerCase().equals("quit")) {
                    writer.write(message + "\n");
                } else {
                    writer.write(name + ">:" + message + "\n");
                }
                printToUser(message);
                writer.flush();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    };

    ActionListener actionListenerConnectButton = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            printToUser(textField.getText());
            address = addressField.getText();
            name = nameField.getText();
            System.out.println(address);
            System.out.println(name);
            ChatListener chatListener = null;
            try (Socket socket = new Socket(address, port)) {
                textField.setEditable(true);
                nameField.setEditable(false);
                addressField.setEditable(false);

                writer=new OutputStreamWriter(socket.getOutputStream());
                reader=new Scanner(socket.getInputStream());

                chatListener = new ChatListener(socket);
                chatListener.isWork=true;
                Thread thread = new Thread(chatListener);

                thread.start();
                System.out.println("I here");
                writer.write(name + "\n");
                writer.flush();

                printToUser("To leave print 'quit'dasdsa");
            } catch (IOException ex) {
                printToUser("Connection error");

            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        }
    };

    public static String[] getRules() {
        String[] rules = new String[10];
        rules[0] = "Commands:";
        rules[1] = "===================================================";
        rules[2] = "1. add\nExample of using:\nadd 5 3\nServer will answer:8";
        rules[3] = "2. subtract\nExample of using:\nsubtract 5 3\nServer will answer:2";
        rules[4] = "3. multiply\nExample of using:\nmultiply 5 3\nServer will answer:15";
        rules[5] = "4. divide\nExample of using:\ndivide 6 3\nServer will answer:2";
        rules[6] = "5. MD5\n Example of using:\nMD5 torules\nServer will answer:";
        rules[7] = "6. DeshMD5\nExample of using:\nDeshMd5 39D89CD686B43C82A7509A638A4AB6DD 100\nServer will answer:rulesss\nATTENTION:\n" +
                "This command is available for 7 letter message(in decrypted form)";
        rules[8] = "7. help\nExample of using:\nhelp\nServer will write this note.";
        rules[9] = "===================================================";
        return rules;
    }

    private void printToUser(String message) {
        textArea.append(message + "\n");
    }

    public void startClient(int port) {
        this.port = port;
        frame = new JFrame("Chat");
        mb = new JMenuBar();
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        helpButton = new JButton("Help");
        ConnectButton = new JButton("Connect");
        panel = new JPanel();
        label = new JLabel("Enter text");
        textField = new JTextField(20);
        nameField = new JTextField(15);
        nameField.setText("Nickname");
        addressField = new JTextField(11);
        addressField.setText("localhost");
        sendButton = new JButton("Send");
        reset = new JButton("Clear");

        textField.setEditable(false);
        helpButton.addActionListener(actionListenerHelpButton);
        ConnectButton.addActionListener(actionListenerConnectButton);
        sendButton.addActionListener(actionListenerSendButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        mb.add(helpButton);
        mb.add(nameField);
        mb.add(addressField);
        mb.add(ConnectButton);


        panel.add(label);
        panel.add(textField);
        panel.add(sendButton);
        panel.add(reset);

        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.add(scroll);
        frame.setVisible(true);
    }

    class ChatListener implements Runnable {

        public boolean isWork = true;

        Socket socket;

        public ChatListener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Scanner scanner = new Scanner(socket.getInputStream(), "utf-8")) {
                while (true) {
                    if (isWork) {
                        try {
                            String message= reader.nextLine();
                            System.out.println(message);
                            //printToUser(message);
                        } catch (NoSuchElementException e) {
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
                printToUser("Server is unavailable\n");
                throw new RuntimeException(e);
            }
        }
    }
}