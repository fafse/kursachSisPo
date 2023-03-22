import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        int menu=0;
        Scanner cin = new Scanner(System.in);
        System.out.println("Who I am?\n1. Server\n2. Client\n");
        menu = cin.nextInt();
        cin.nextLine();
        int port = 2022;
        switch (menu)
        {
            case 1:
            {
                try {
                    new Server().startServer(port);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case 2:
            {
                new Client().startClient(port);
                break;
            }
            default:
            {
                break;
            }
        }
    }
}
