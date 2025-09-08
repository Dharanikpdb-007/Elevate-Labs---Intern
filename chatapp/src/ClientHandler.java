import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String nickname;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println("Enter your nickname: ");
            this.nickname = reader.readLine();

            System.out.println(nickname + " joined.");
            Server.broadcast(nickname + " joined the chat!", this);
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                message = decrypt(message);

                if (message.startsWith("@")) { // private message
                    String[] splitMsg = message.split(" ", 2);
                    if (splitMsg.length < 2) {
                        writer.println("Invalid private message format. Use @nickname message");
                        continue;
                    }
                    String targetNick = splitMsg[0].substring(1);
                    String privateMsg = splitMsg[1];
                    sendPrivateMessage(targetNick, privateMsg);
                } else { // group message
                    System.out.println(nickname + ": " + message);
                    Server.broadcast(nickname + ": " + message, this);
                }
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMessage(String message) {
        writer.println(encrypt(message));
    }

    private void sendPrivateMessage(String targetNick, String message) {
        boolean found = false;
        for (ClientHandler client : Server.getClientHandlers()) {
            if (client.nickname.equalsIgnoreCase(targetNick)) {
                client.sendMessage("[Private] " + nickname + ": " + message);
                writer.println("[Private to " + targetNick + "] " + message);
                found = true;
                break;
            }
        }
        if (!found) {
            writer.println("User " + targetNick + " not found.");
        }
    }

    private void closeEverything() {
        try {
            Server.removeClient(this);
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            Server.broadcast(nickname + " left the chat.", this);
            System.out.println(nickname + " disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Simple Caesar cipher
    private String encrypt(String message) {
        StringBuilder sb = new StringBuilder();
        for (char c : message.toCharArray()) {
            sb.append((char) (c + 3));
        }
        return sb.toString();
    }

    private String decrypt(String message) {
        StringBuilder sb = new StringBuilder();
        for (char c : message.toCharArray()) {
            sb.append((char) (c - 3));
        }
        return sb.toString();
    }
}
