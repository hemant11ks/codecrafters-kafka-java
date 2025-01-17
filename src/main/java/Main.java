import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Main {
    private static final int PORT = 9092;

    public static void main(String[] args) {
        System.err.println("Server starting on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setReuseAddress(true);

            while (true) {
                System.out.println("Waiting for a client...");
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Handle client communication
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException {
        try (OutputStream out = clientSocket.getOutputStream()) {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putInt(0); // Message size
            buffer.putInt(7); // Correlation ID

            out.write(buffer.array());
            out.flush();
            System.out.println("Response sent with correlation ID: 7");
        }
    }
}
