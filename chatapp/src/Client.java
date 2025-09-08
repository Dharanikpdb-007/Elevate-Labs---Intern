import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String nickname;

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            Scanner sc = new Scanner(System.in);

            System.out.println(reader.readLine());
            nickname = sc.nextLine();
            writer.println(nickname);

            new Thread(new ReceiveMessages()).start();

            while (true) {
                String msg = sc.nextLine();
                writer.println(encrypt(msg));
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void closeEverything() {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encrypt(String message) {
        StringBuilder sb = new StringBuilder();
        for (char c : message.toCharArray()) {
            sb.append((char) (c + 3));
        }
        return sb.toString();
    }

    private class ReceiveMessages implements Runnable {
        @Override
        public void run() {
            String msg;
            try {
                while ((msg = reader.readLine()) != null) {
                    System.out.println(decrypt(msg));
                }
            } catch (IOException e) {
                closeEverything();
            }
        }

        private String decrypt(String message) {
            StringBuilder sb = new StringBuilder();
            for (char c : message.toCharArray()) {
                sb.append((char) (c - 3));
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 12345);
    }
}

