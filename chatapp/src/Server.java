import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;

    // Store all connected clients
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                addClient(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add client to set
    public static void addClient(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }

    // Remove client from set
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    // Broadcast message to all clients except sender
    public static void broadcast(String message, ClientHandler excludeUser) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeUser) {
                client.sendMessage(message);
            }
        }
    }

    // Getter for ClientHandler set
    public static Set<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
}
